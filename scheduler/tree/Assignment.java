package tree;

import structures.Pair;
import structures.Slot;

import java.util.ArrayList;
import structures.Class;
import execution.Scheduler;

public class Assignment {
	
	private ArrayList<Pair<Slot, ArrayList<Class>>> assign;
	private Assignment parent;
	private ArrayList<Assignment> children = new ArrayList<Assignment>();
	
	// empty constructor for the root node
	public Assignment() {
		this.assign = new ArrayList<Pair<Slot, ArrayList<Class>>>();
		
		// add each slot to the assignment with a blank arrayList of classes
		for (int i = 0; i < Scheduler.getSlots().size(); i++) {
			Pair<Slot, ArrayList<Class>> p = new Pair<Slot, ArrayList<Class>>(Scheduler.getSlots().get(i), new ArrayList<Class>());
			this.assign.add(p);
		}
		
		this.setParent(null);
		this.setChildren(null);
	}
	
	public Assignment(ArrayList<Pair<Slot, ArrayList<Class>>> assign, Assignment parent) {
		this.assign = assign;
		this.setParent(parent);
		this.setChildren(null);
	}
	
	public String toString() {
		String returnStr = "";
		
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
	
	public ArrayList<Pair<Slot, ArrayList<Class>>> getAssign() {
		return assign;
	}
	
	public void setAssign(ArrayList<Pair<Slot, ArrayList<Class>>> assign) {
		this.assign = assign;
	}

	public Assignment getParent() {
		return parent;
	}

	public void setParent(Assignment parent) {
		this.parent = parent;
	}

	public ArrayList<Assignment> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<Assignment> children) {
		this.children = children;
	}
	
	
	
}
