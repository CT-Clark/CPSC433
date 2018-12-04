package tree;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import execution.Scheduler;
import structures.Class;
import structures.Pair;
import structures.Slot;


// Authors: Stephen Booth, Marcel Baier, Cody Clark
// This class contains all of the data about an assignment
// It contains the evalue() method for scoring the penalty of any assignment

/**
 * This is the assignment object which contains a mapping of courses/labs onto their respective slots
 */

public class Assignment implements Cloneable, Comparable<Assignment> {

	private int evalValue = 1000000;
	
	private TreeMap<Slot, List<Class>> assign;
	private TreeSet<Class> unassignedClasses;

	private int total_pen_coursemin = 0;
	private int total_pen_labsmin = 0;
	private int total_pen_notpaired = 0;
	private int total_pen_section = 0;
	private int total_pen_preferences = 0;
	
	public Assignment(TreeMap<Slot, List<Class>> assign) {
		this.assign = assign;
		this.unassignedClasses = Scheduler.getUnassignedClasses();
	}

	public Assignment(Assignment newAssignment) {
		this.assign = newAssignment.assign;
		this.unassignedClasses = newAssignment.unassignedClasses;
	}
	
	// --------------------------------------------------------------------- //
	// ------------------------ OVERRIDDEN METHODS ------------------------- //
	// --------------------------------------------------------------------- //
	
	@Override
	public String toString() {
		String returnStr = "\n||--------------------||--------------------||\n\nEval value: " + this.getEvalValue() + "\n";
		
		for (Map.Entry<Slot, List<Class>> entry : assign.entrySet()) {
			String slotStr = entry.getKey().getDay() + " " + entry.getKey().getStartTime().toString() + "\t";
			String classStr;
			
			for (Class c : entry.getValue()) {
				classStr = c.getDept() + " " + c.getId()
						+ c.getLectureString() + c.getTutorialString();
				returnStr = returnStr + slotStr + classStr + "\n";
			}
		}
		
		return returnStr;
		
	}
	
	@Override
	public Object clone() {
		return new Assignment(this);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} 
		
		if (!(o instanceof Class)) {
			return false;
		}
		
		Assignment a = (Assignment) o;
		a.assign.entrySet();
		
		return assign.equals(a.assign) && unassignedClasses.equals(a.unassignedClasses);
	}
	
	@Override
	public int compareTo(Assignment a) {
		return this.evalValue - a.evalValue;
	}

	
	public void assignClass(Slot s, Class c) {
		assign.get(s).add(c);
		getUnassignedClasses().remove(c);
	}


	// --------------------------------------------------------------------- //
	// -------------------- EVAL AND ASSOCIATED METHODS -------------------- //
	// --------------------------------------------------------------------- //


	public void evalue() {
		int result = 0;
		int pen_coursemin = 10;
		int pen_labsmin = 0;
		int pen_notpaired = 2;
		int pen_section = 1;
		
		for(Map.Entry<Slot, List<Class>> entry : assign.entrySet()) {
			for(Class c : entry.getValue()) {

				// Evaluate the penalty for Classes not being placed in their preferred slot
				
				
				
				for (Pair<Slot, Double> prefs : Scheduler.getPreferences().get(c)) {
					if (!prefs.getFirst().equals(entry.getKey())) {
						result += prefs.getSecond();
						total_pen_preferences += prefs.getSecond();
						break;
					}
				}

				// Evaluate the different sections in the same slot penalty
				for(Class c2 : entry.getValue()) { // Compare with another class
					if((!c.equals(c2))  // If they're the same object, don't compare them
							&& (c.getDept().equals(c2.getDept())  // If they're in the same dept...
							&& c.getType().equals(c2.getType())   // And they're the same type...
							&& c.getId() == c2.getId())) {        // And they're for the same course...
						result += pen_section;
						total_pen_section += pen_section;
					}
				}

				// Evaluate the penalty for not being paired
				for (Class cl : Scheduler.getPairs().get(c)) { // Iterate through all of the possible pairs

					if(Scheduler.getUnassignedClasses().contains(cl) || entry.getValue().contains(cl)) {
						break;
					} 
					
					// If a pair wasn't found, increase the penalty
					// If the other side of the pair wasn't found, add a penalty
					result += pen_notpaired;
					total_pen_notpaired += pen_notpaired;
				}

			}

			// Evaluate minCourse and minLab
			int difference = entry.getKey().getMin() - entry.getValue().size();
			if(difference > 0) {
				if(entry.getKey().getType().equals("course")) {
					result += (difference * pen_coursemin);
					total_pen_coursemin += (difference * pen_coursemin);
				}
				if(entry.getKey().getType().equals("lab")) {
					result += (difference * pen_labsmin);
					total_pen_labsmin += (difference * pen_labsmin);
				}
			}
		}

		this.evalValue = result;
	}

	public void printTotalPens() {
		System.out.println("Coursemin: " + total_pen_coursemin + " Labsmin: " + total_pen_labsmin);
		System.out.println("NotPaired: " + total_pen_notpaired + " Preferences: " + total_pen_preferences);
		System.out.println("Section: " + total_pen_section);
	}
	
	// --------------------------------------------------------------------- //
	// ------------------------ GETTERS AND SETTERS ------------------------ //
	// --------------------------------------------------------------------- //

	public int getEvalValue() {
		return evalValue;
	}

	public void setEvalValue(int evalValue) {
		this.evalValue = evalValue;
	}

	public TreeMap<Slot, List<Class>> getAssign() {
		return assign;
	}

	public void setAssign(TreeMap<Slot, List<Class>> assign) {
		this.assign = assign;
	}

	public TreeSet<Class> getUnassignedClasses() {
		return unassignedClasses;
	}

	public void setUnassignedClasses(TreeSet<Class> unassignedClasses) {
		this.unassignedClasses = unassignedClasses;
	}

}
