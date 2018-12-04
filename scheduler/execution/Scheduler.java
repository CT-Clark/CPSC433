package execution;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.TreeSet;

// Custom objects
import structures.Class;
import structures.Pair;
import structures.Slot;
import tree.Assignment;

public class Scheduler {
	// Fields
	private ArrayList<Slot> slots; 
	private ArrayList<Class> classes;
	private TreeMap<Class, List<Class>> overlappingSections;
	private TreeMap<Slot, List<Slot>> overlappingSlots;
	private TreeMap<Class, List<Class>> notCompatible;
	private TreeMap<Class, List<Slot>> unwanted;
	private static TreeMap<Class, List<Pair<Slot, Double>>> preferences;
	private static TreeMap<Class, List<Class>> pairs;
	private TreeMap<Slot, List<Class>> partAssign;
	
	private static TreeSet<Class> unassignedClasses;
	private PriorityQueue<Assignment> assignList;
	
	public Scheduler(ArrayList<Slot> slots, LinkedHashSet<Class> classes, TreeMap<Class, List<Class>> overlappingSections, TreeMap<Slot, List<Slot>> overlappingSlots, TreeMap<Class, List<Class>> notCompatible, 
			TreeMap<Class, List<Slot>> unwanted, TreeMap<Class, List<Pair<Slot, Double>>> preferences, TreeMap<Class, List<Class>> pairs, TreeMap<Slot, LinkedHashSet<Class>> partAssign) {
		
		this.slots = slots;	
		this.classes = new ArrayList<>(classes);
		this.overlappingSections = overlappingSections;
		this.overlappingSlots = overlappingSlots;
		this.notCompatible = notCompatible;
		this.unwanted = unwanted;
		Scheduler.preferences = preferences;
		Scheduler.pairs = pairs;
		
		for (Entry<Slot, LinkedHashSet<Class>> e : partAssign.entrySet()) {
			this.partAssign.put(e.getKey(), new ArrayList<>(e.getValue()));	
		}
		
		this.setUnassignedClasses(new TreeSet<>(classes));
		this.setAssignList(new PriorityQueue<>());		
	}
	
	// -------------------------------------------------------- //
	// -------------------- SEARCH CONTROL -------------------- //
    // -------------------------------------------------------- //
	
	public void search() {
		double run = 0;
		boolean foundResult = false;

		Assignment currentResult = new Assignment(getPartAssign());
		getAssignList().add(currentResult);
		
		while(!foundResult) {
			run++;
			
			// Find the assignment with the lowest eval score
			currentResult = getAssignList().poll();

			/*
			// Uncomment if you would like to see progress for long runs
			if (run % 10 == 0) { 
				System.out.println("RUN: " + run);
				System.out.println(currentResult);
				System.out.println("Number of courses left to assign: " + currentResult.getUnassignedClasses().size());
				currentResult.printTotalPens();
				System.out.println("Size of assignList: " + getAssignList().size());
				
				System.out.println("Num of Assigns in assignList: " + assignList.size());
			}
			*/

			// An answer has been found
			if(currentResult.getUnassignedClasses().size() == 0){
				foundResult = true;
				System.out.println("FINAL ASSIGNMENT");
				System.out.println(currentResult);
				currentResult.printTotalPens();
			}
			
			// If an answer hasn't been found yet...
			else {
				// Go through all of the unassigned classes
				for(Class c : currentResult.getUnassignedClasses()) {

					// And try to assign them to a slot
					for(Map.Entry<Slot, List<Class>> slotEntry : currentResult.getAssign().entrySet()) {

						// If this slot is acceptable under the hard constraints
						if (Const(c, slotEntry, currentResult.getAssign())) {
							// Create a new assignment
							Assignment a = new Assignment(currentResult);

							// Add the class to the assignment
							a.assignClass(slotEntry.getKey(), c);
							
							// Update the assignment eval
							a.evalue(); // Update assignment evalValue
							
							// Add the assignment to the list
							if (!getAssignList().contains(a)) {
								getAssignList().add(a);
							}
						}
					}
				}
			}
			
			// Remove the previous parent from the priority queue
			getAssignList().remove(currentResult);
		}

	}
	
    // ------------------------------------------------------------------- //
    // ---------------------- CONST AND ITS METHODS ---------------------- //
    // ------------------------------------------------------------------- //
	
	private boolean Const(Class cl, Entry<Slot, List<Class>> slotEntry, TreeMap<Slot, List<Class>> assign) {
		return typeCheck(cl, slotEntry) && maxLimit(slotEntry) && eveningCheck(cl, slotEntry) && quizCheck(cl, slotEntry) && unwantedCheck(cl, slotEntry) && lunchCheck(cl, slotEntry) && seniorCheck(cl, slotEntry) 
				&& overlappingSectionCheck(cl, slotEntry, assign) && nonCompatibleCheck(cl, slotEntry);
	}
	
	// Check to make sure the course going into the slot is of the correct type
	private boolean typeCheck(Class cl, Entry<Slot, List<Class>> slot) {
		return cl.getType().equals(slot.getKey().getType());
	}
	
	// Check to see if the course is about to break the max limit
	private boolean maxLimit(Entry<Slot, List<Class>> slot) {
		return slot.getValue().size() <= slot.getKey().getMax();
	}
	
	// Checks to see if it's an evening class not being put in the evening
	private boolean eveningCheck(Class cl, Entry<Slot, List<Class>> slot) {
		return cl.getLecture() >= 90 && slot.getKey().getStartTime().isBefore(LocalTime.parse("18:00"));
	}
	
	private boolean quizCheck(Class cl, Entry<Slot, List<Class>> slot) {
		if (cl.getDept().equals("CPSC") && (cl.getId() == 813 || cl.getId() == 913)) {
			LocalTime time = LocalTime.of(18, 0, 0);
			if (!(slot.getKey().getDay().equals("TU") && slot.getKey().getStartTime().compareTo(time) == 0)) {
				return false;
			}
		}
		
		return true;
	}
	
	// Check to see if the course is about to occupy an unwanted slot
	private boolean unwantedCheck(Class cl, Entry<Slot, List<Class>> slot) {
		List<Slot> classUnwanted = getUnwanted().get(cl);
		
		if (!classUnwanted.contains(slot.getKey())) {
			return true;
		}
		
		return false;
	}
	
	// Checks to see if it occurs over lunch
	private boolean lunchCheck(Class cl, Entry<Slot, List<Class>> slot) {
		LocalTime start = LocalTime.parse("11:00");
		LocalTime end = LocalTime.parse("12:30");
		
		return (!(cl.getType().equals("course") && slot.getKey().getDay().equals("TU") && 
						(slot.getKey().getStartTime().equals(start) || (slot.getKey().getStartTime().isAfter(start) && slot.getKey().getStartTime().isBefore(end)))));

	}
	
	// Checks to make sure that if a 500 level course has been assigned to this slot that it doesn't assign another 500 level course to it
	private boolean seniorCheck(Class cl1, Entry<Slot, List<Class>> slot) {
		
		// if cl1 is a 500 level course
		if (cl1.getType().equals("course") && cl1.getId() >= 500) {
			for (Class cl2 : slot.getValue()) {
				// if cl2 is a 500 level course
				if (cl2.getId() >= 500 && cl2.getType().equals("course")) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	// Check to see if placing it here would conflict with other slots
	private boolean overlappingSectionCheck(Class cl1, Entry<Slot, List<Class>> slot, TreeMap<Slot, List<Class>> assign) {
		
		// for each overlappingSlot
		for (Slot sl : getOverlappingSlots().get(slot.getKey())) {	
			
			// for each overlappingSection
			for (Class cl2 : getOverlappingSections().get(cl1)) {
				// if an overlappingSlot contains an overlappingSection
				if (assign.get(sl).contains(cl2)) {
					return false;
				}	
			}
		}
			
		return true;
	}
	
	// Check to make sure there will not be a nonCompatible pair of courses in the assignment
	private boolean nonCompatibleCheck(Class cl1, Entry<Slot, List<Class>> slot) {
		List<Class> classNotCompatible = getNotCompatible().get(cl1);
		
		for (Class cl2 : classNotCompatible) {
			if (slot.getValue().contains(cl2)) {
				return false;
			}
		}
		
		return true;
	}
		
    // ------------------------------------------------------------------- //
    // -------------------- GETTER AND SETTER METHODS -------------------- //
    // ------------------------------------------------------------------- //

	public ArrayList<Slot> getSlots() {
		return slots;
	}

	public void setSlots(ArrayList<Slot> slots) {
		this.slots = slots;
	}

	public ArrayList<Class> getClasses() {
		return classes;
	}

	public void setClasses(ArrayList<Class> classes) {
		this.classes = classes;
	}

	public TreeMap<Class, List<Class>> getOverlappingSections() {
		return overlappingSections;
	}

	public void setOverlappingSections(TreeMap<Class, List<Class>> overlappingSections) {
		this.overlappingSections = overlappingSections;
	}

	public TreeMap<Slot, List<Slot>> getOverlappingSlots() {
		return overlappingSlots;
	}

	public void setOverlappingSlots(TreeMap<Slot, List<Slot>> overlappingSlots) {
		this.overlappingSlots = overlappingSlots;
	}

	public TreeMap<Class, List<Class>> getNotCompatible() {
		return notCompatible;
	}

	public void setNotCompatible(TreeMap<Class, List<Class>> notCompatible) {
		this.notCompatible = notCompatible;
	}

	public TreeMap<Class, List<Slot>> getUnwanted() {
		return unwanted;
	}

	public void setUnwanted(TreeMap<Class, List<Slot>> unwanted) {
		this.unwanted = unwanted;
	}

	public static TreeMap<Class, List<Pair<Slot, Double>>> getPreferences() {
		return preferences;
	}

	public void setPreferences(TreeMap<Class, List<Pair<Slot, Double>>> preferences) {
		Scheduler.preferences = preferences;
	}

	public static TreeMap<Class, List<Class>> getPairs() {
		return pairs;
	}

	public void setPairs(TreeMap<Class, List<Class>> pairs) {
		Scheduler.pairs = pairs;
	}

	public TreeMap<Slot, List<Class>> getPartAssign() {
		return partAssign;
	}

	public void setPartAssign(TreeMap<Slot, List<Class>> partAssign) {
		this.partAssign = partAssign;
	}

	public static TreeSet<Class> getUnassignedClasses() {
		return unassignedClasses;
	}

	public void setUnassignedClasses(TreeSet<Class> unassignedClasses) {
		Scheduler.unassignedClasses = unassignedClasses;
	}

	public PriorityQueue<Assignment> getAssignList() {
		return assignList;
	}

	public void setAssignList(PriorityQueue<Assignment> assignList) {
		this.assignList = assignList;
	}

}

