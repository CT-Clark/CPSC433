package CPSC433master.scheduler.tree;

import structures.Pair;
import structures.Slot;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;

import CPSC433master.scheduler.structures.Class;
import CPSC433master.scheduler.execution.Scheduler;

/**
 * This is the assignment object which contains a mapping of courses/labs onto their respective slots
 */

public class Assignment {

	private double evalValue;
	
	private ArrayList<Pair<Slot, ArrayList<Class>>> assign;
	private ArrayList<Class> unassignedClasses;
	private static ArrayList<Pair<Pair<Class, Slot>, Double>> preferences = new ArrayList<>();
	private ArrayList<Pair<Class, Class>> notCompatible;
	private ArrayList<Pair<Class, Slot>> unwanted;
	private ArrayList<Pair<Class, Class>> pairs;

	int total_pen_coursemin = 0;
	int total_pen_labsmin = 0;
	int total_pen_notpaired = 0;
	int total_pen_section = 0;
	int total_pen_preferences = 0;
	
	// empty constructor for the root node
	public Assignment() {
		this.assign = new ArrayList<>();

		// add each slot to the assignment with a blank arrayList of classes
		if(Scheduler.getSlots().size() != 0) {
			for (int i = 0; i < Scheduler.getSlots().size(); i++) {
				Pair<Slot, ArrayList<Class>> p = new Pair<>(Scheduler.getSlots().get(i), new ArrayList<>());
				this.assign.add(p);
			}
		}

		if(Scheduler.getClasses().size() != 0) { this.unassignedClasses = new ArrayList<>(Scheduler.getClasses()); }
		else { this.unassignedClasses = new ArrayList<>(); }
		if(Scheduler.getNotCompatible().size() != 0) { this.notCompatible = new ArrayList<>(Scheduler.getNotCompatible()); }
		else { this.notCompatible = new ArrayList<>(); }
		if(Scheduler.getUnwanted().size() != 0) { this.unwanted = new ArrayList<>(Scheduler.getUnwanted()); }
		else { this.unwanted = new ArrayList<>(); }
		if(Scheduler.getPairs().size() != 0) { this.pairs = new ArrayList<>(Scheduler.getPairs()); }
		else { this.pairs = new ArrayList<>(); }
		if(Scheduler.getPreferences().size() != 0) { this.preferences = new ArrayList<>(Scheduler.getPreferences()); }
		else { this.preferences = new ArrayList<>(); }

		this.evalValue = 10000;
	}
	
	public Assignment(ArrayList<Pair<Slot, ArrayList<Class>>> assign, ArrayList<Class> unassigned) {
		this.assign = new ArrayList<>(assign);
		this.unassignedClasses = new ArrayList<>(unassigned);
		if(Scheduler.getNotCompatible().size() != 0) { this.notCompatible = new ArrayList<>(Scheduler.getNotCompatible()); }
		else { this.notCompatible = new ArrayList<>(); }
		if(Scheduler.getUnwanted().size() != 0) { this.unwanted = new ArrayList<>(Scheduler.getUnwanted()); }
		else { this.unwanted = new ArrayList<>(); }
		if(Scheduler.getPairs().size() != 0) { this.pairs = new ArrayList<>(Scheduler.getPairs()); }
		else { this.pairs = new ArrayList<>(); }
		if(Scheduler.getPreferences().size() != 0) { this.preferences = new ArrayList<>(Scheduler.getPreferences()); }
		else { this.preferences = new ArrayList<>(); }

		this.evalValue = 10000;
	}

	// -------------------------------------------------------- //
	// -------------------- CONSTR METHODS -------------------- //
	// -------------------------------------------------------- //

	public boolean constr() {
		boolean debugConstr = false; // Enable to see the reasons why assignments have failed
		// Ensure that course objects are in course slots, and lab objects are in lab slots
		if(!ensureType(assign)) {
			if(debugConstr) { System.out.println("Assignment broke ensureType"); }
			return false; }

		// Ensure that the number of courses assigned to a slot is less than courseMax
		if(!slotMax(assign)) {
			if(debugConstr) { System.out.println("Assignment broke slotMax"); }
			return false; }

		// Ensure that each lab is scheduled at a different time than its course
		//if(!sectionOverlap(assign)) { return false; }

		// Ensure that non-compatible courses are assigned to different slots
		if(!nonCompatible(assign)) {
			if(debugConstr) { System.out.println("Assignment broke nonCompatible"); }
			return false; }

		// Ensure that no course is placed into a slot where it's unwanted
		if(!unwanted(assign)) {
			if(debugConstr) { System.out.println("Assignment broke unwanted"); }
			return false; }

		// Ensure that no classes take palce during lunch
		if(!lunchBreakCheck(assign)) {
			if(debugConstr) { System.out.println("Assignment broke lunchBreakCheck"); }
			return false; }

		// Ensure that LEC 9 courses are assigned in the evenings
		if(!eveningCheck(assign)) {
			if(debugConstr) { System.out.println("Assignment broke eveningCheck"); }
			return false; }

		// Ensure that all 500 level courses are placed into different slots
		if(!seniorLevelCheck(assign)) {
			if(debugConstr) { System.out.println("Assignment broke seniorLevelCheck"); }
			return false; }

		// Ensure that CPSC 813 and CPSC 913 behave
		if(!quizzesCheck(assign)) {
			if(debugConstr) { System.out.println("Assignment broke quizzesCheck"); }
			return false; }

		return true; // If nothing breaks the hard constraints, return true
	}

	// -------------------- //

	// Method for ensuring that all class objects are set to their respective slots
	private boolean ensureType(ArrayList<Pair<Slot, ArrayList<Class>>> assign) {
		for(int i = 0; i < assign.size(); i++) { // Go through all the slots
			if( assign.get(i).getSecond() != null) {
				for (Class c : assign.get(i).getSecond()) { // Go through all the classes in the slots
					if (!c.getType().equals(assign.get(i).getFirst().getType())) {
						return false;
					}
				}
			}
		}
		return true;
	}

	// Checks whether there are more courses assigned to a slot than there should be
	private boolean slotMax(ArrayList<Pair<Slot, ArrayList<Class>>> assign) {

		for(int i = 0; i < assign.size(); i++) { // Go through all the slots
			int courseCount = 0;
			int labCount = 0;
			// Increment course or lab count if either of those courses are found
			if(assign.get(i).getSecond() != null) {
				for (int j = 0; j < assign.get(i).getSecond().size(); j++) {
					if (assign.get(i).getSecond().get(j).getType().equals("course")) {
						courseCount++;
					} else {
						labCount++;
					}
				}
				// Max is exceeded, return false
				if (courseCount > assign.get(i).getFirst().getMax() && assign.get(i).getFirst().getType().equals("course")) {
					return false; // False if courseMax exceeded
				} else if (labCount > assign.get(i).getFirst().getMax() && assign.get(i).getFirst().getType().equals("lab")) {
					return false; // False if labMax is exceeded
				}
			}
		}
		return true;
	}

/*
	private static boolean sectionOverlap(ArrayList<Pair<Slot, ArrayList<Class>>> assign) {
		ArrayList<Slot> overlappingSlots = getOverlappingSlots(slot);
		ArrayList<Class> overlappingClasses = new ArrayList<>();

		for (int i = 0; i < overlappingSlots.size(); i++) {
			ArrayList<Class> classesToAdd = getClassesFromSlot(overlappingSlots.get(i), assign);
			overlappingClasses.addAll(classesToAdd);
		}

		for (int i = 0; i < classes.size(); i++) {
			ArrayList<Class> courses = new ArrayList<Class>();
			ArrayList<Class> labs = new ArrayList<Class>();

			// put current class into correct array depending on type
			if (classes.get(i).getType().equals("course")) {
				courses.add(classes.get(i));
			} else {
				labs.add(classes.get(i));
			}

			// iterate through overlapping looking for potential matches
			for (int j = 0; j < overlappingClasses.size(); j++) {
				if (overlappingClasses.get(j).getDept().equals(classes.get(i).getDept()) && overlappingClasses.get(j).getId() == classes.get(i).getId()
						&& overlappingClasses.get(j).getLecture() == classes.get(i).getLecture()) {
					if (overlappingClasses.get(j).getType().equals("course")) {
						courses.add(overlappingClasses.get(j));
					} else {
						labs.add(overlappingClasses.get(j));
					}
				}
			}

			if (courses.size() > 0 && labs.size() != 0) {
				return false;
			}

		}
		return true;
	}


	// Generates a list of slots which overlap
	private static ArrayList<Slot> getOverlappingSlots(Slot slot) {
		ArrayList<Slot> overlappingSlots = new ArrayList<>();

		//iterate through all the slots
		for (int i = 0; i < Scheduler.getSlots().size(); i++) {
			if (slot.getDay().equals("MO") || slot.getDay().equals("FR")) {
				if (Scheduler.getSlots().get(i).getDay().equals("MO") || Scheduler.getSlots().get(i).getDay().equals("FR")) {
					if (overlapCheck(slot, Scheduler.getSlots().get(i)) && !slot.equals(Scheduler.getSlots().get(i))) {
						overlappingSlots.add(Scheduler.getSlots().get(i));
					}
				}
			} else {
				if (Scheduler.getSlots().get(i).getDay().equals("TU")) {
					if (overlapCheck(slot, Scheduler.getSlots().get(i)) && !slot.equals(Scheduler.getSlots().get(i))) {
						overlappingSlots.add(Scheduler.getSlots().get(i));
					}
				}
			}
		}

		return overlappingSlots;
	}


	// Checks whether or not two slots overlap
	private static boolean overlapCheck(Slot s1, Slot s2) {
		if (s1.getStartTime().equals(s2.getStartTime()) || (s1.getStartTime().isAfter(s2.getStartTime())
				&& s1.getStartTime().isBefore(s2.getEndTime())) || (s1.getEndTime().isAfter(s2.getStartTime())
				&& s1.getEndTime().isBefore(s2.getEndTime())) || s1.getEndTime().equals(s2.getEndTime())) {
			return true;
		}

		return false;
	}

*/

	// Check to make sure all of the classes that are not compatible with each other are not assigned at the same time
	private boolean nonCompatible(ArrayList<Pair<Slot, ArrayList<Class>>> assign) {
		for(int i = 0; i < assign.size(); i++) { // Go through all the slots
			if(assign.get(i).getSecond() != null) {
				for(int j = 0; j < assign.get(i).getSecond().size(); j++) { // Then all the courses
					for(int k = 0; k < notCompatible.size(); k++) {
						// If The first of the notCompatible pairs == the assigned course, look to see
						// if the slot contains the second of the pair
						if (notCompatible.get(k).getFirst().equals(assign.get(i).getSecond().get(j))) {
							if (assign.get(i).getSecond().contains(notCompatible.get(k).getSecond())) {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	// Checks whether any courses/labs have been placed in an unwanted slot
	private boolean unwanted(ArrayList<Pair<Slot, ArrayList<Class>>> assign) {
		for(int i = 0; i < assign.size(); i++) { // Go through all the slots
			if(assign.get(i).getSecond() != null) {
				for (int j = 0; j < assign.get(i).getSecond().size(); j++) { // Then all the courses
					for(int k = 0; k < unwanted.size(); k++) { // Go through all the unwanted pairs
						if (assign.get(i).getSecond().get(j).equals(unwanted.get(k).getFirst()) && // If the course matches
								assign.get(i).getFirst().equals(unwanted.get(k).getSecond())) { // And the slot matches
							return false; // Return false
						}
					}
				}
			}
		}
		return true;
	}

	// Checks to see whether any courses/labs have been assigned to lunch time
	private boolean lunchBreakCheck(ArrayList<Pair<Slot, ArrayList<Class>>> assign) {
		LocalTime start = LocalTime.parse("11:00");
		LocalTime end = LocalTime.parse("12:30");

		for(int i = 0; i < assign.size(); i++) { // Go through all the slots
			if(assign.get(i).getSecond() != null) {
				for (int j = 0; j < assign.get(i).getSecond().size(); j++) { // Then all the courses
					if(assign.get(i).getFirst().getDay().equals("TU")) { // Just check on Tuesday
						// Now check if there's a slot at that time
						if(assign.get(i).getFirst().getStartTime().equals(start) ||
								(assign.get(i).getFirst().getStartTime().isAfter(start)) &&
										assign.get(i).getFirst().getStartTime().isBefore(end)) {
							// If there is, check if a course has been scheduled into there
							if (assign.get(i).getSecond().get(j).getType().equals("course")) {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	// Checks to see whether the correct courses have been placed in the evening
	private boolean eveningCheck(ArrayList<Pair<Slot, ArrayList<Class>>> assign) {
		for (int i = 0; i < assign.size(); i++) {
			// Go through all the slots
			if(assign.get(i).getSecond() != null) {
				for (int j = 0; j < assign.get(i).getSecond().size(); j++) {
					// Then all the courses
					if (assign.get(i).getSecond().get(j).getLecture() >= 90) {
						if (assign.get(i).getFirst().getStartTime().isBefore(LocalTime.parse("18:00"))) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	// Checks to see if any 500 level course has been assigned the same slot as another 500 level course
	private boolean seniorLevelCheck(ArrayList<Pair<Slot, ArrayList<Class>>> assign) {
		for(int i = 0; i < assign.size(); i++) { // Go through all the slots
			boolean found500 = false; // Set flag to see if the slot contains a 500 level course
			if(assign.get(i).getSecond() != null) {
				for (int j = 0; j < assign.get(i).getSecond().size(); j++) { // Then all the courses {
					// If a 500 level course has already been found, and another one is found, return false
					if (found500 && (assign.get(i).getSecond().get(j).getId() >= 500 &&
							assign.get(i).getSecond().get(j).getType().equals("course"))) {
						return false;
					}
					// If this is the first 500 level course in the slot, set the flag
					if (assign.get(i).getSecond().get(j).getId() >= 500 &&
							assign.get(i).getSecond().get(j).getType().equals("course")) {
						found500 = true;
					}
				}
			}
		}
		return true;
	}

	// TODO: Check overlap with 313 and 413 courses
	// check that 813 and 913 are scheduled at the correct time
	private boolean quizzesCheck(ArrayList<Pair<Slot, ArrayList<Class>>> assign) {
		for (int i = 0; i < assign.size(); i++) { // Go through the slots
			if(assign.get(i).getSecond() != null) {
				for (int j = 0; j < assign.get(i).getSecond().size(); j++) { // Go through the classes
					// Check to make sure that the courses are in the TU slot at 18:00
					if (assign.get(i).getSecond().get(j).getId() == 813 || assign.get(i).getSecond().get(j).getId() == 913) {
						// Make sure that 813 and 913 are on Tuesday
						if (!assign.get(i).getFirst().getDay().equals("TU")) {
							return false;
						}
						// Check if 813 or 913 start at 18:00
						LocalTime time = LocalTime.of(18, 0, 0);
						if(assign.get(i).getFirst().getStartTime().compareTo(time) != 0) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	// --------------------------------------------------------------------- //
	// -------------------- EVAL AND ASSOCIATED METHODS -------------------- //
	// --------------------------------------------------------------------- //


	public void evalue() {
		double result = 0;
		int pen_coursemin = 10;
		int pen_labsmin = 10;
		int pen_notpaired = 10;
		int pen_section = 10;


		// Evaluate minCourse and minLab
		for(Pair<Slot, ArrayList<Class>> p : assign) {
			int difference = p.getFirst().getMin() - p.getSecond().size();
			if(difference > 0) {
				if(p.getFirst().getType().equals("course")) {
					result += (difference * pen_coursemin);
					total_pen_coursemin += (difference * pen_coursemin);
				}
				if(p.getFirst().getType().equals("lab")) {
					result += (difference * pen_labsmin);
					total_pen_labsmin += (difference * pen_labsmin);
				}
			}
		}

		// Evaluate slot preferences
		for(Pair<Slot, ArrayList<Class>> p1 : assign) {
			if(p1.getSecond() != null) {
				for(Class c : p1.getSecond()) {
					for(Pair<Pair<Class, Slot>, Double> p2 : preferences) {
						if(p2.getFirst().getFirst().equals(c) && !p2.getFirst().getSecond().equals(p1.getFirst())) {
							result += p2.getSecond();
							total_pen_preferences += p2.getSecond();
						}
					}
				}
			}
		}

		// Evaluate not paired penalty
		for(int i = 0; i < assign.size(); i++) { // Go through all the slots
			if (assign.get(i).getSecond() != null) { // Make sure the array of classes isn't empty
				for (int j = 0; j < assign.get(i).getSecond().size(); j++) { // Then all the courses
					for (Pair<Class, Class> p : pairs) { // Iterate through all of the possible pairs
						// If the first of the pair is present, make sure the second of the pair is present, or...
						if (p.getFirst().equals((assign.get(i).getSecond().get(j)))) {
							if (!assign.get(i).getSecond().contains(p.getSecond())) {
								// Make sure it hasn't been assigned to anything yet
								if (!unassignedClasses.contains(p.getSecond())) {
									result += pen_notpaired;
									total_pen_notpaired += pen_notpaired;
								}
							}
							// If the second of the pair is present, make sure the first of the pair is present, or...
						} else if (p.getSecond().equals(assign.get(i).getSecond().get(j))) {
							if (!assign.get(i).getSecond().contains(p.getFirst())) {
								// Make sure it hasn't been assigned yet
								if (!unassignedClasses.contains(p.getFirst())) {
									result += pen_notpaired;
									total_pen_notpaired += pen_notpaired;
								}
							}
						}
					}
				}
			}
		}

		// Evaluate the different sections in the same slot penalty
		for(Pair<Slot, ArrayList<Class>> p : assign) {
			for(int i = 0; i < p.getSecond().size(); i++) {
				for(int j = i+1; j < p.getSecond().size(); j++) {
					if(p.getSecond().get(i).getType().equals(p.getSecond().get(j).getType())) {
						if(p.getSecond().get(i).getDept().equals(p.getSecond().get(j).getDept())) {
							if(p.getSecond().get(i).getId() == (p.getSecond().get(j).getId())) {
								result += pen_section;
								total_pen_section += pen_section;
							}
						}
					}
				}
			}
		}

		setEvalValue(result);
	}

	public void printTotalPens() {
		System.out.println("Coursemin: " + total_pen_coursemin + " Labsmin: " + total_pen_labsmin);
		System.out.println("NotPaired: " + total_pen_notpaired + " Preferences: " + total_pen_preferences);
		System.out.println("Section: " + total_pen_section);
	}

	
	
	
	
	// ----- PRINT THIS OBJECT ----- //
	
	public String toString() {
		String returnStr = "\n||--------------------||--------------------||\n\nEval value: " + this.getEvalValue() + "\n";
		
		for (int i = 0; i < assign.size(); i++) {
			String slotStr = assign.get(i).getFirst().getDay() + " " + assign.get(i).getFirst().getStartTime().toString() + "\t";
			String classStr;
			
			for (int j = 0; j < assign.get(i).getSecond().size(); j++) {
				classStr = assign.get(i).getSecond().get(j).getDept() + " " + assign.get(i).getSecond().get(j).getId() 
						+ assign.get(i).getSecond().get(j).getLectureString() + assign.get(i).getSecond().get(j).getTutorialString();
				returnStr = returnStr + slotStr + classStr + "\n";
			}
		}
		
		return returnStr;
		
	}

	// ----- GETTER AND SETTER METHODS ----- //

	// Method to return the current assignment to be copied
	public ArrayList<Pair<Slot, ArrayList<Class>>> getAssign() {
		ArrayList<Pair<Slot, ArrayList<Class>>> result = new ArrayList<>(); // Create new assign object
		// Initialize the new object with all of the slots
		for (int i = 0; i < Scheduler.getSlots().size(); i++) {
			Pair<Slot, ArrayList<Class>> p = new Pair<>(Scheduler.getSlots().get(i), new ArrayList<>());
			result.add(p);
		}
		// Then go through all those slots and where the slot has a class, add that class to the result slot
		for(int i = 0; i < assign.size(); i++) {
			if(assign.get(i).getSecond().size() > 0) {
				for(int j = 0; j < assign.get(i).getSecond().size(); j++) {
					result.get(i).getSecond().add(assign.get(i).getSecond().get(j));
				}
			}
		}

		return result;
	}
	
	public void setAssign(ArrayList<Pair<Slot, ArrayList<Class>>> assign) {
		this.assign = assign;
	}

	public double getEvalValue() { return evalValue; }

	public void setEvalValue(double value) { this.evalValue = value; }

	public void assignClass(Slot s, Class c) {
		for(Pair<Slot, ArrayList<Class>> p : this.assign) {
			if(p.getFirst().equals(s)) {
				p.getSecond().add(c);
			}
		}
		this.unassignedClasses.remove(c);
	}

	public ArrayList<Class> getUnassignedClasses() {
		ArrayList<Class> result = new ArrayList<>(unassignedClasses);
		return result; }

	public int getSizeOfUnassignedClasses() { return unassignedClasses.size(); }

	public void removeCourseFromUnassigned(Class c) {
		unassignedClasses.remove(c);
	}
}
