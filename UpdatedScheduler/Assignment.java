package CPSC433master.scheduler.tree;

import CPSC433master.scheduler.structures.Pair;
import CPSC433master.scheduler.structures.Slot;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;

import CPSC433master.scheduler.structures.Class;
import CPSC433master.scheduler.execution.Scheduler;

// Authors: Stephen Booth, Marcel Baier, Cody Clark
// This class contains all of the data about an assignment
// It contains the evalue() method for scoring the penalty of any assignment

/**
 * This is the assignment object which contains a mapping of courses/labs onto their respective slots
 */

public class Assignment {

	private double evalValue = 10000;
	
	private ArrayList<Pair<Slot, ArrayList<Class>>> assign;
	private ArrayList<Class> unassignedClasses;
	private ArrayList<Pair<Pair<Class, Slot>, Double>> preferences;
	private ArrayList<Pair<Class, Class>> pairs;

	private int total_pen_coursemin = 0;
	private int total_pen_labsmin = 0;
	private int total_pen_notpaired = 0;
	private int total_pen_section = 0;
	private int total_pen_preferences = 0;
	
	// Used to generate a blank canvas of an assignment with all of the fields initialized
	public Assignment() {
		this.assign = new ArrayList<>();

		// add each slot to the assignment with a blank arrayList of classes

		for (Slot s : Scheduler.getSlots()) {
			Pair<Slot, ArrayList<Class>> p = new Pair<>(s, new ArrayList<>());
			this.assign.add(p);
		}

		this.unassignedClasses = new ArrayList<>(Scheduler.getClasses());
		this.pairs = Scheduler.getPairs();
		this.preferences = Scheduler.getPreferences();

	}

	// Used in the search control to create a new assignment based on an old assignment
	public Assignment(ArrayList<Pair<Slot, ArrayList<Class>>> assign, ArrayList<Class> unassigned) {
		this.assign = assign;
		this.unassignedClasses = new ArrayList<>(unassigned);

		this.pairs = Scheduler.getPairs();
		this.preferences = Scheduler.getPreferences();

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
		for(Pair<Pair<Class, Slot>, Double> p2 : preferences) {
			for(Pair<Slot, ArrayList<Class>> p1 : assign) {
				for(Class c : p1.getSecond()) {
					if(p2.getFirst().getFirst().equals(c) && !p2.getFirst().getSecond().equals(p1.getFirst())) {
						result += p2.getSecond();
						total_pen_preferences += p2.getSecond();
						break;
					}
				}
			}
		}

		// Evaluate not paired penalty
		for (Pair<Class, Class> p : pairs) { // Iterate through all of the possible pairs
			boolean firstFound = false;
			boolean secondFound = false;
			boolean pairFound = false;
			// Test if the pair has had at least one of its items assigned
			for(Class uc : unassignedClasses) {
				if(uc.equals(p.getFirst())) { firstFound = true; }
				if(uc.equals(p.getSecond())) { secondFound = true; }
				if(firstFound && secondFound) {
					pairFound = true;
					break;
				} // True if neither item has been assigned yet
			}

			// At this point we know that at least one of the classes has been assigned
			if(!pairFound) {
				for (Pair<Slot, ArrayList<Class>> ap : assign) { // Go through all the slots
					for (Class c : ap.getSecond()) { // Then all the courses in each slot
						// If the first of the pair is present, check to see if the second is unassigned
						if (c.equals(p.getFirst())) {
							// Let's check if it hasn't been assigned yet
							for (Class c2 : unassignedClasses) {
								if (c2.equals(p.getSecond())) {
									pairFound = true;
									break;
								}
							}
							if (pairFound) { break; }

							// Go through all the classes assigned to a particular slot
							for (Class cl : ap.getSecond()) {
								// If there exists the other pair, break
								if (cl.equals(p.getSecond())) {
									pairFound = true;
									break;
								}
							}
							if (pairFound) { break; }
						}
					}
				}

			}
			// If a pair wasn't found, increase the penalty
			if(!pairFound) {
				// If the other side of the pair wasn't found, add a penalty
				result += pen_notpaired;
				total_pen_notpaired += pen_notpaired;
			}
		}


		// Evaluate the different sections in the same slot penalty
		for(Pair<Slot, ArrayList<Class>> p : assign) { // For each slot
			for(Class c1 : p.getSecond()) { // For each class
				for(Class c2 : p.getSecond()) { // Compare with another class
					if((!c1.equals(c2))  // If they're the same object, don't compare them
							&& (c1.getDept().equals(c2.getDept())  // If they're in the same dept...
							&& c1.getType().equals(c2.getType())   // And they're the same type...
							&& c1.getId() == c2.getId())) {        // And they're for the same course...
						result += pen_section;
						total_pen_section += pen_section;
					}
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



	// ----- PRINT THIS OBJECT ----- //
	
	public String toString() {
		String returnStr = "\n||--------------------||--------------------||\n\nEval value: " + this.getEvalValue() + "\n";
		
		for (Pair<Slot, ArrayList<Class>> p : assign) {
			String slotStr = p.getFirst().getDay() + " " + p.getFirst().getStartTime().toString() + "\t";
			String classStr;
			
			for (Class c : p.getSecond()) {
				classStr = c.getDept() + " " + c.getId()
						+ c.getLectureString() + c.getTutorialString();
				returnStr = returnStr + slotStr + classStr + "\n";
			}
		}
		
		return returnStr;
		
	}

	// ----- GETTER AND SETTER METHODS ----- //

	public ArrayList<Pair<Slot, ArrayList<Class>>> getAssign() {
		return assign;
	}

	// Method to return the current assignment to be copied
	public ArrayList<Pair<Slot, ArrayList<Class>>> getAssignCopy() {
		ArrayList<Pair<Slot, ArrayList<Class>>> result = new ArrayList<>(); // Create new assign object
		// Initialize the new object with all of the slots
		for (Slot s : Scheduler.getSlots()) {
			Pair<Slot, ArrayList<Class>> p1 = new Pair<>(s, new ArrayList<>());
			// Then for all of the new slots, copy the class objects
			for(Pair<Slot, ArrayList<Class>> p2 : this.assign) {
				if(p2.getFirst().equals(s)) {
					for(Class c : p2.getSecond()) {
						p1.getSecond().add(c);
					}
				}
			}

			result.add(p1);
		}

		return result;
	}
	
	public void setAssign(ArrayList<Pair<Slot, ArrayList<Class>>> assign) {
		this.assign = new ArrayList<>(assign);
	}

	public double getEvalValue() { return evalValue; }

	public void setEvalValue(double value) { this.evalValue = value; }

	public void assignClass(Slot s, Class c) {
		for(Pair<Slot, ArrayList<Class>> p : assign) {
			if(p.getFirst().equals(s)) {
				p.getSecond().add(c);
				break;
			}
		}
		unassignedClasses.remove(c);
	}

	public ArrayList<Class> getUnassignedClasses() {
		return new ArrayList<>(unassignedClasses); }

	public int getSizeOfUnassignedClasses() { return unassignedClasses.size(); }

	public void removeCourseFromUnassigned(Class c) {
		unassignedClasses.remove(c);
	}
}
