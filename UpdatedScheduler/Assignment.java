package CPSC433master.scheduler.tree;

import structures.Pair;
import structures.Slot;

import java.util.ArrayList;
import CPSC433master.scheduler.structures.Class;
import CPSC433master.scheduler.execution.Scheduler;

/**
 * This is the assignment object which contains a mapping of courses/labs onto their respective slots
 */

public class Assignment {
	
	private ArrayList<Pair<Slot, ArrayList<Class>>> assign;
	private double evalValue;
	private ArrayList<Class> unassignedClasses = new ArrayList<>();
	
	// empty constructor for the root node
	public Assignment() {
		this.assign = new ArrayList<>();
		
		// add each slot to the assignment with a blank arrayList of classes
		for (int i = 0; i < Scheduler.getSlots().size(); i++) {
			Pair<Slot, ArrayList<Class>> p = new Pair<Slot, ArrayList<Class>>(Scheduler.getSlots().get(i), new ArrayList<>());
			this.assign.add(p);
		}

		this.setEvalValue(1000000);
	}
	
	public Assignment(ArrayList<Pair<Slot, ArrayList<Class>>> assign, ArrayList<Class> unassigned) {
		this.assign = assign;
		this.unassignedClasses.addAll(unassigned);
		this.setEvalValue(1000000);
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
	
	public ArrayList<Pair<Slot, ArrayList<Class>>> getAssign() {
		return assign;
	}
	
	public void setAssign(ArrayList<Pair<Slot, ArrayList<Class>>> assign) {
		this.assign = assign;
	}

	public double getEvalValue() { return evalValue; }

	public void setEvalValue(double value) { this.evalValue = value; }

	public void assignClass(Slot s, Class c) {
		unassignedClasses.remove(c);
		for(Pair<Slot, ArrayList<Class>> p : assign) {
			if(p.getFirst().equals(s)) {
				ArrayList<Class> tempClassArr = new ArrayList<>();
				tempClassArr = p.getSecond();
				tempClassArr.add(c);
				p.setSecond(tempClassArr);
			}
		}
	}

	public ArrayList<Class> getUnassignedClasses() { return unassignedClasses; }

	public int getSizeOfUnassignedClasses() { return unassignedClasses.size(); }
}
