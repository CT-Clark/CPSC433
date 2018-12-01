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
	public Assignment(Assignment a) {
		this.assign = a.getAssignCopy();
		this.unassignedClasses = new ArrayList<>(a.getUnassignedClasses());

		this.pairs = Scheduler.getPairs();
		this.preferences = Scheduler.getPreferences();

	}


	// --------------------------------------------------------------------- //
	// -------------------- EVAL AND ASSOCIATED METHODS -------------------- //
	// --------------------------------------------------------------------- //


	public void evalue() {
		double result = 0;
		int pen_coursemin = 100;
		int pen_labsmin = 50;
		int pen_notpaired = 20;
		int pen_section = 10;


		// Evaluate minCourse and minLab
		for(Pair<Slot, ArrayList<Class>> p : assign) {
			for(Class c : p.getSecond()) {

				// Evaluate the penalty for Classes not being placed in their preferred slot
				for (Pair<Pair<Class, Slot>, Double> prefs : preferences) {
					if (prefs.getFirst().getFirst().equals(c) && !prefs.getFirst().getSecond().equals(p.getFirst())) {
						result += prefs.getSecond();
						total_pen_preferences += prefs.getSecond();
						break;
					}
				}

				// Evaluate the different sections in the same slot penalty
				for(Class c2 : p.getSecond()) { // Compare with another class
					if((!c.equals(c2))  // If they're the same object, don't compare them
							&& (c.getDept().equals(c2.getDept())  // If they're in the same dept...
							&& c.getType().equals(c2.getType())   // And they're the same type...
							&& c.getId() == c2.getId())) {        // And they're for the same course...
						result += pen_section;
						total_pen_section += pen_section;
					}
				}

				// Evalute the penalty for not being paired
				for (Pair<Class, Class> pair : pairs) { // Iterate through all of the possible pairs

					if(unassignedClasses.contains(pair.getFirst()) && unassignedClasses.contains(pair.getSecond())) {
						break;
					}

					if (c.equals(pair.getFirst())) {
						// Let's check if it hasn't been assigned yet
						if (unassignedClasses.contains(pair.getSecond())) {
							break;
						}

						// Go through all the classes assigned to a particular slot
						if(p.getSecond().contains(pair.getSecond())) {
							break;
						}

						// If a pair wasn't found, increase the penalty
						// If the other side of the pair wasn't found, add a penalty
						result += pen_notpaired;
						total_pen_notpaired += pen_notpaired;
					}

				}
			}

			// Evaluate minCourse and minLab
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
		for (Pair<Slot, ArrayList<Class>> s : this.assign) {
			Pair<Slot, ArrayList<Class>> p1 = new Pair<>(s.getFirst(), new ArrayList<>());
			// Then for all of the new slots, copy the class objects
			for(Class c : s.getSecond()) {
				p1.getSecond().add(c);
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
		return unassignedClasses; }

	public int getSizeOfUnassignedClasses() { return unassignedClasses.size(); }

	public void removeCourseFromUnassigned(Class c) {
		unassignedClasses.remove(c);
	}
}
