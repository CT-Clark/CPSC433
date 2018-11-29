package execution;

// Review the package names before attempting to compile the code

import structures.Class;
import structures.Course;
import structures.Lab;
import structures.Pair;
import structures.Slot;
import tree.AssignTree;
import tree.Assignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Scheduler {

	private static ArrayList<Slot> slots = new ArrayList<Slot>();
	private static ArrayList<Class> classes = new ArrayList<Class>();
	private static ArrayList<Class> rankedClasses = new ArrayList<Class>();
	private static ArrayList<Pair<Slot, Integer>> rankedSlots = new ArrayList<Pair<Slot, Integer>>();
	private static ArrayList<Pair<Class, Class>> notCompatible = new ArrayList<Pair<Class, Class>>();
	private static ArrayList<Pair<Class, Slot>> unwanted = new ArrayList<Pair<Class, Slot>>();
	private static ArrayList<Pair<Pair<Class, Slot>, Double>> preferences = new ArrayList<Pair<Pair<Class, Slot>, Double>>();
	private static ArrayList<Pair<Class, Class>> pair = new ArrayList<Pair<Class, Class>>();
	
	public static void main(String[] args){
	
		// parse text file
		//File file = new File(args[0]); // First argument to file
		// Set info for parser
		String path = args[0];
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		System.out.println("Current relative path is: " + s);
		if (!Files.exists(Paths.get(path)) || !Files.isRegularFile(Paths.get(path))) {
			throw new IllegalArgumentException("Input file not available: " + Paths.get(path) + "!");
		}

		try {
			parseInput(args[0]);
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		setRankedClasses(determineClassRank());
		setRankedSlots(determineSlotRank());
		
		AssignTree T = new AssignTree();
		Assignment currentChild = T.getRoot();	
		ArrayList<Assignment> possibleChildren = new ArrayList<Assignment>();
		boolean noChildren = false;
		
		ArrayList<Class> ranked = getRankedClasses();

		// This is the search control
		while ((!rankedClasses.isEmpty()) && (!noChildren)) {			
			possibleChildren = createChildren(currentChild);
			
			if (ranked.size() == 134) {
				System.out.print("Stop");
			}
			
			if (possibleChildren.size() == 0) {
				noChildren = true;
			} else {				
				currentChild = selectChild(possibleChildren, currentChild.getAssign());	
			}
		}
		
		System.out.println(currentChild);
		
	}

	// Parses the input file
	private static void parseInput(String path) throws IOException {
		try (Scanner scanner = new Scanner(new File(path))) {
			List<String> inputFile = Files.readAllLines(Paths.get(path));

			int lastBreakpoint = 0;
			int counter = 0;
			for (String line : inputFile) {
				line = line.toLowerCase();
				switch (line) {
					// Name of the department
					case "name:":
						lastBreakpoint = counter + 1;
						break;
					// Generate list of course slots
					case "course slots:":
						saveName(inputFile, lastBreakpoint, counter);
						lastBreakpoint = counter + 1;
						break;
					// Generate list of lab slots
					case "lab slots:":
						saveSlots(inputFile, lastBreakpoint, counter, "course");
						lastBreakpoint = counter + 1;
						break;
					// Generate list of courses
					case "courses:":
						saveSlots(inputFile, lastBreakpoint, counter, "lab");
						lastBreakpoint = counter + 1;
						break;
					// Generate list of labs
					case "labs:":
						saveCourses(inputFile, lastBreakpoint, counter);
						lastBreakpoint = counter + 1;
						break;
					// Generate list of not-compatible courses/labs
					case "not compatible:":
						saveLabs(inputFile, lastBreakpoint, counter);
						lastBreakpoint = counter + 1;
						break;
					// Generate list of courses that cannot be assigned to a particular slot
					case "unwanted:":
						saveNotCompatible(inputFile, lastBreakpoint, counter);
						lastBreakpoint = counter + 1;
						break;
					// Generate the list of courses/tutorials that should be put into a specific slot
					// Along with the Eval penalty if they're not
					case "preferences:":
						saveUnwanted(inputFile, lastBreakpoint, counter);
						lastBreakpoint = counter + 1;
						break;
					// Generate the list of courses/labs that should be placed int othe same slot
					case "pair:":
						savePreferences(inputFile, lastBreakpoint, counter);
						lastBreakpoint = counter + 1;
						break;
					// Generate a partial assignment to start with
					case "partial assignments:":
						savePairs(inputFile, lastBreakpoint, counter);
						lastBreakpoint = counter + 1;
						break;
				}
				counter++;
			}
			//savePartialAssignments(inputFile, lastBreakpoint, counter - 1);
		}

	}



	// Saves the name of the assignment
	private static void saveName(List<String> inputFile, int lastBreakpoint, int counter) {
		for (String line : inputFile.subList(lastBreakpoint, counter)) {
			System.out.println(line);
		}
	}

	// Parses the line and creates a slot object
	private static Slot parseSlot(String line, String type) {
		String[] slotItems = line.split(",");
		
		String day = slotItems[0];
		
		LocalTime time = getLocalTime(slotItems[1]);
		int max = Integer.parseInt(slotItems[2].replaceAll("\\s+", ""));
		int min = Integer.parseInt(slotItems[3].replaceAll("\\s+", ""));
		
		Slot sl = new Slot(type, day, time, max, min);

		return sl;
	}

	// Saves all of the slot objects
	private static void saveSlots(List<String> inputFile, int lastBreakpoint, int counter, String type) {
		for (String line : inputFile.subList(lastBreakpoint, counter)) {
			//line = line.replaceAll("\\s", "");
			if (line.isEmpty()) {
				continue;
			}
			getSlots().add(parseSlot(line, type));
		}
	}

	// Parses the class objects
	private static Class parseClass(String line) {
		String[] inputLine = line.split(" ");
		ArrayList<String> classItems = new ArrayList<String>();

		for (int i = 0; i < inputLine.length; i++) {
			if (!inputLine[i].equals("")) {
				classItems.add(inputLine[i]);
			}
		}

		String dept = classItems.get(0);
		int id = Integer.parseInt(classItems.get(1));
		int lecture, tutorial;

		if (classItems.size() < 5) {
			if (classItems.get(2).equals("LEC")) {
				lecture = Integer.parseInt(classItems.get(3));
				tutorial = -1;
			} else {
				lecture = -1;
				tutorial = Integer.parseInt(classItems.get(3));
			}
		} else {
			lecture = Integer.parseInt(classItems.get(3));
			tutorial = Integer.parseInt(classItems.get(5));
		}

		Class cl;
		if (tutorial == -1) {
			cl = new Course(dept, id, lecture);
		} else {
			cl = new Lab(dept, id, lecture, tutorial);
		}
		return cl;
	}

	// Saves all of the course objects
	private static void saveCourses(List<String> inputFile, int lastBreakpoint, int counter) {
		for (String line : inputFile.subList(lastBreakpoint, counter)) {
			if (line.isEmpty()) {
				continue;
			}
			getClasses().add(parseClass(line));
		}
	}

	// Saves all the lab objects
	private static void saveLabs(List<String> inputFile, int lastBreakpoint, int counter) {
		for (String line : inputFile.subList(lastBreakpoint, counter)) {
			if (line.isEmpty()) {
				continue;
			}
			getClasses().add(parseClass(line));
		}
	}

	// Saves all of the courses and labs which aren't compatible with each other
	private static void saveNotCompatible(List<String> inputFile, int lastBreakpoint, int counter) {
		for (String line : inputFile.subList(lastBreakpoint, counter)) {
			if(line.isEmpty()) {
				continue;
			}
			Pair<Class, Class> pair = parsePair(line);
			getNotCompatible().add(pair);
		}
	}

	// Saves all of the courses/labs which should not go in particular slots
	private static void saveUnwanted(List<String> inputFile, int lastBreakpoint, int counter) {
		for (String line : inputFile.subList(lastBreakpoint, counter)) {
			if(line.isEmpty()) {
				continue;
			}
			Pair<Class, Slot> pair = parseUnwanted(line);
			getUnwanted().add(pair);
		}
	}

	// Saves all of the desires course/lab slot preferences and their respective values
	private static void savePreferences(List<String> inputFile, int lastBreakpoint, int counter) {
		for (String line : inputFile.subList(lastBreakpoint, counter)) {
			if(line.isEmpty()) {
				continue;
			}
			Pair<Pair<Class, Slot>, Double> pair = parsePreferences(line);
			if (pair.getFirst().getSecond() != null) {
				getPreferences().add(pair);
			}
		}
		normalizeRankings(getPreferences());
	}

	// Saves the pairs of courses/labs that should run at the same time
	private static void savePairs(List<String> inputFile, int lastBreakpoint, int counter) {
		for (String line : inputFile.subList(lastBreakpoint, counter)) {
			if(line.isEmpty()) {
				continue;
			}
			Pair<Class, Class> pair = parsePair(line);
			getPair().add(pair);
		}
	}

	// Parses the pair of courses/labs that should run at the same time
	private static Pair<Class, Class> parsePair(String line) {
		String[] pairItems = line.split(", ");
		
		Class cl1 = parseClass(pairItems[0]);
		Class cl2 = parseClass(pairItems[1]);
			
		Pair<Class, Class> p = new Pair<Class, Class>(cl1, cl2);
		return p;
		
	}

	// Parses the unwanted course/lab slot pairings
	private static Pair<Class, Slot> parseUnwanted(String line) {
		String[] pairItems = line.split(", ");		
		Class cl = parseClass(pairItems[0]); 
		Slot sl = null;
		
		if (cl.getType().equals("course")) {
			ArrayList<Slot> courseSlots = getCourseSlots();
			for (int i = 0; i < courseSlots.size(); i++) {
				if (courseSlots.get(i).getDay().equals(pairItems[1]) && courseSlots.get(i).getStartTime().equals(getLocalTime(pairItems[2]))) {
					sl = courseSlots.get(i);					}
			}
		} else {
			for (int i = 0; i < getLabSlots().size(); i++) {
				ArrayList<Slot> labSlots = getLabSlots();
				if (labSlots.get(i).getDay().equals(pairItems[1]) && labSlots.get(i).getStartTime().equals(getLocalTime(pairItems[2]))) {
					sl = labSlots.get(i);					}
			}
		}
		
		Pair<Class, Slot> p = new Pair<Class, Slot>(cl, sl);
		return p;
	}

	// Parses the course/lab slot preferences and their respective values
	private static Pair<Pair<Class, Slot>, Double> parsePreferences(String line) {
		String[] pairItems = line.split(", ");
		
		Pair<Class, Slot> p1 = parseUnwanted(pairItems[2] + ", " + pairItems[0] + ", " + pairItems[1]);
		Pair<Pair<Class, Slot>, Double> p = new Pair<Pair<Class, Slot>, Double>(p1, Double.parseDouble((pairItems[3])));
				
		return p;
	}
	
	private static LocalTime getLocalTime(String str) {
		String timeString = str.replaceAll("\\s+", "");
		 
		if (timeString.length() == 4) {
			timeString = "0" + timeString;
		}
		
		LocalTime time = LocalTime.parse(timeString);
		return time;		
	}
	
	private static ArrayList<Slot> getCourseSlots() {
		ArrayList<Slot> slots = new ArrayList<Slot>();
		
		for (int i = 0; i < getSlots().size(); i++) { 
			if (getSlots().get(i).getType().equals("course")) {
				slots.add(getSlots().get(i));
			}
		}
		
		return slots;
	}
	
	private static ArrayList<Slot> getLabSlots() {
		ArrayList<Slot> slots = new ArrayList<Slot>();
		
		for (int i = 0; i < getSlots().size(); i++) { 
			if (getSlots().get(i).getType() == "lab") {
				slots.add(getSlots().get(i));
			}
		}
		
		return slots;
	}		
	
	private static void normalizeRankings(ArrayList<Pair<Pair<Class, Slot>, Double>> p) {
		ArrayList<Double> rankings = new ArrayList<Double>();
		
		// iterate through preferences and create an array of only the rankings
		for (int i = 0; i < p.size(); i++) {
			rankings.add(p.get(i).getSecond());
		}
		
		// sort list and declare max and min values
		Collections.sort(rankings);
		double max = rankings.get(rankings.size() - 1);
		double min = 0;
		
		// reiterate through preferences updating each of the rankings
		for (int i = 0; i < p.size(); i++) {
			double newRank = (p.get(i).getSecond() - min)/(max - min);
			p.get(i).setSecond(newRank);
		}
	}
	
	private static ArrayList<Pair<Class, Integer>> determineClassRank() {
		ArrayList<Pair<Class, Integer>> rankList = new ArrayList<Pair<Class, Integer>>();
		
		// iterate through classes 
		for (int i = 0; i < getClasses().size(); i++) {
			int rankValue = 1;
			int constraintValue = 0;
						
			
			// increase rankValue once for every other class that shares its dept, id and lecture 
			if (getClasses().get(i).getType().equals("course")) {
				for (int j = 0; j < getClasses().size(); j++) {
					if (getClasses().get(j).getType().equals("lab") && getClasses().get(j).getDept().equals(getClasses().get(i).getDept()) 
							&& getClasses().get(j).getId() == getClasses().get(i).getId() && getClasses().get(j).getLecture() == getClasses().get(i).getLecture()) {
						rankValue++;
					}
				}
			}
			
			// increase constraintValue each time class appears in notCompatible
			for (int j = 0; j < getNotCompatible().size(); j++) {
				if (getNotCompatible().get(j).getFirst().equals(getClasses().get(i)) || getNotCompatible().get(j).getSecond().equals(getClasses().get(i))) {
					constraintValue++;
				}
			}
			
			// increase constraintValue each time class appears in unwanted
			for (int j = 0; j < getUnwanted().size(); j++) {
				if (getUnwanted().get(j).getFirst().equals(getClasses().get(i))) {
					constraintValue++;
				}
			}
			
			// increase constraintValue by 2 for evening classes and senior classes
			if (getClasses().get(i).getId() > 499 || (getClasses().get(i).getLecture() > 89 && getClasses().get(i).getLecture() < 100)) {
				constraintValue = constraintValue + 2;
			}
			
			// ensure 813 and 913, if they're a member of the class list, are at the top of the rankings
			if (getClasses().get(i).getId() == 813 || getClasses().get(i).getId() == 913) {
				rankValue = (getClasses().size() - 1) * (getNotCompatible().size() + getUnwanted().size());
			} else {
				rankValue = rankValue * constraintValue;
			}
				
			/*
			
			// increase rankValue each time class appears in preferences
			for (int j = 0; j < getPreferences().size(); j++) {
				if (getPreferences().get(j).getFirst().getFirst().equals(getClasses().get(i))) {
					rankValue++;
				}
			}
			
			// increase rankValue each time class appears in pair
			for (int j = 0; j < getPair().size(); j++) {
				if (getPair().get(j).getFirst().equals(getClasses().get(i)) || getPair().get(j).getSecond().equals(getClasses().get(i))) {
					rankValue++;
				}
			}
			
			*/
			
			// add class to rankList with rankValue
			Pair<Class, Integer> p = new Pair<Class, Integer>(getClasses().get(i), rankValue);
			rankList.add(p);
		}
		
		// sort rankList by rankValue descending
		rankList.sort(Comparator.comparing(Pair<Class, Integer>::getSecond));
		Collections.reverse(rankList);
		
		return rankList;
	}
	
	private static ArrayList<Pair<Slot, Integer>> determineSlotRank() {
		ArrayList<Pair<Slot, Integer>> rankList = new ArrayList<Pair<Slot, Integer>>();
		
		// iterate through slots
		for (int i = 0; i < getSlots().size(); i++) {
			int rankValue = 0;
			ArrayList<Slot> overlappingSlots = getOverlappingSlots(getSlots().get(i));
			
			// increase rankValue for every overlappingSlots
			rankValue = rankValue + overlappingSlots.size();
			
			// add slot to rankList with rankValue
			Pair<Slot, Integer> p = new Pair<Slot, Integer>(getSlots().get(i), rankValue);
			rankList.add(p);
		}
		
		rankList.sort(Comparator.comparing(Pair<Slot, Integer>::getSecond));
		Collections.reverse(rankList);
		
		return rankList;
	}
	
	public static ArrayList<Assignment> createChildren(Assignment parent) {
		parent.setChildren(new ArrayList<Assignment>());
		Class newClass = getRankedClasses().get(0);
		System.out.println(newClass.getDept() + " " + newClass.getId() + newClass.getLectureString() + newClass.getTutorialString());
		
		for (int i = 0; i < parent.getAssign().size(); i++) {
			ArrayList<Pair<Slot, ArrayList<Class>>> nextAssign = cloneParent(parent);
			nextAssign.get(i).getSecond().add(newClass);
			
			// only create children that meet all hard constraints
			if (Const(nextAssign, i)) {
				Assignment nextAssignment = new Assignment(nextAssign, parent);				
				parent.getChildren().add(nextAssignment);
			}
		}
		
		getRankedClasses().remove(0);
		
		return parent.getChildren();
	}
	
	private static ArrayList<Pair<Slot, ArrayList<Class>>> cloneParent(Assignment parent) {
		ArrayList<Pair<Slot, ArrayList<Class>>> child = new ArrayList<Pair<Slot, ArrayList<Class>>>(); 
		
		for (int i = 0; i < parent.getAssign().size(); i++) {
			Slot slotClone = (Slot) parent.getAssign().get(i).getFirst().clone();
			ArrayList<Class> classList = new ArrayList<Class>();
			for (int j = 0; j < parent.getAssign().get(i).getSecond().size(); j++) {
				Class classClone = (Class) parent.getAssign().get(i).getSecond().get(j).clone(); 
				classList.add(classClone);
			}
			Pair<Slot, ArrayList<Class>> pairClone = new Pair<Slot, ArrayList<Class>>(slotClone, classList);
			child.add(pairClone);
		}
		
		return child;
	}
	
	private static Assignment selectChild(ArrayList<Assignment> children, ArrayList<Pair<Slot, ArrayList<Class>>> previousAssign) {
		ArrayList<Pair<Assignment, Double>> evalRankings = new ArrayList<Pair<Assignment, Double>>();
		ArrayList<Pair<Assignment, Integer>> selectRankings = new ArrayList<Pair<Assignment, Integer>>();
		Pair<Assignment, Integer> chosenChild = new Pair<Assignment, Integer>();
		Slot changedSlot;
		
		for (int i = 0; i < children.size(); i++) {		
			Pair<Assignment, Double> p = new Pair<Assignment, Double>(children.get(i), Eval(children.get(i).getAssign(), true));
			evalRankings.add(p);
		}
		
		evalRankings.sort(Comparator.comparing(Pair<Assignment, Double>::getSecond));
		BigDecimal lowEval = new BigDecimal(evalRankings.get(0).getSecond());
		
		// placing a lecture limits only the placement of that lectures labs (lectures of the same class is only a soft constraint)
		// placing a tutorial limits the placement of its lecture and its lectures labs
		
		for (int i = 0; i < evalRankings.size(); i++) {
			if (new BigDecimal(evalRankings.get(i).getSecond()).compareTo(lowEval) == 0) {
				
			}
		}
		
		selectRankings.sort(Comparator.comparing(Pair<Assignment, Integer>::getSecond));
		
		for (int i = 0; i < selectRankings.size(); i++) {
			if (chosenChild.getSecond() == null || selectRankings.get(i).getSecond() < chosenChild.getSecond()) {
				chosenChild = selectRankings.get(i);
			}
		}
		
		return evalRankings.get(0).getFirst();
		
	}
	
	private static Integer slotRanking(Slot changedSlot) {
		int rank = 0;
		
		for (int i = 0; i < getRankedSlots().size(); i++) {
			if (getRankedSlots().get(i).getFirst().equals(changedSlot)) {
				rank = getRankedSlots().get(i).getSecond();
				break;
			}
		}
		
		return rank;
	}

	private static Slot getChangedSlot(ArrayList<Pair<Slot, ArrayList<Class>>> newAssign, ArrayList<Pair<Slot, ArrayList<Class>>> previousAssign) {
		for (int i = 0; i < newAssign.size(); i++) {
			if (!newAssign.get(i).getSecond().equals(previousAssign.get(i).getSecond())) {
				return newAssign.get(i).getFirst();
			}
		}
		
		return null;
	}

	public static boolean Const(ArrayList<Pair<Slot, ArrayList<Class>>> assign, Integer i) {
		boolean constraint = true;		
	
		constraint = ensureType(assign.get(i).getFirst(), assign.get(i).getSecond());
		
		if (!constraint) {
			return false;
		}
		
		constraint = courseMax(assign.get(i).getFirst(), assign.get(i).getSecond());			// courses assigned to a slot is less than that slots coursemax
		
		if (!constraint) {
			return false;
		}
		
		constraint = sectionOverlap(assign.get(i).getFirst(), assign.get(i).getSecond(), assign);		// course cannot occur at the same time as a tutorial that course and vice versa
		
		if (!constraint) {
			return false;
		}
		
		constraint = nonCompatible(assign.get(i).getFirst(), assign.get(i).getSecond());		// non-compatible courses need to be assigned different slots
		
		if (!constraint) {
			return false;
		}
		
		constraint = unwanted(assign.get(i).getFirst(), assign.get(i).getSecond());				// unwanted course/slot combos need to be checked
		
		if (!constraint) {
			return false;
		}
		
		constraint = lunchBreakCheck(assign.get(i).getFirst());										// classes cannot take place during lunch 
		
		if (!constraint) {
			return false;
		}
	
		constraint = eveningCheck(assign);																	// lectures with ids >= 90 needs to be assigned to an evening slot		
		
		if (!constraint) {
			return false;
		}
		
		constraint = seniorLevelCheck(assign);																// 500 level courses need to be assigned to different slots
	
		if (!constraint) {
			return false;
		}
		
		constraint = quizzesCheck(assign);																	// 813 and 913 constraints
		
		return constraint;
	}

	private static boolean ensureType(Slot s, ArrayList<Class> a) {
		for (int i = 0; i < a.size(); i++) {
			if (!a.get(i).getType().equals(s.getType())) {
				return false;
			}
		}
		
		return true;
	}

	private static boolean courseMax(Slot s, ArrayList<Class> a) {
		if (a.size() > s.getMax()) {
			return false;
		}
		
		return true;
	}

	private static boolean sectionOverlap(Slot slot, ArrayList<Class> classes, ArrayList<Pair<Slot, ArrayList<Class>>> assign) {
		ArrayList<Slot> overlappingSlots = getOverlappingSlots(slot);
		ArrayList<Class> overlappingClasses = new ArrayList<Class>();
		
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
	
	private static ArrayList<Class> getClassesFromSlot(Slot slot, ArrayList<Pair<Slot, ArrayList<Class>>> assign) {
		ArrayList<Class> classes = new ArrayList<Class>();
		
		for (int i = 0; i < assign.size(); i++) {
			if (assign.get(i).getFirst().equals(slot)) {
				classes.addAll(assign.get(i).getSecond());
			}
		}

		return classes;
	}

	private static ArrayList<Slot> getOverlappingSlots(Slot slot) {
		ArrayList<Slot> overlappingSlots = new ArrayList<Slot>();
		
		//iterate through all the slots
		for (int i = 0; i < getSlots().size(); i++) {
			if (slot.getDay().equals("MO") || slot.getDay().equals("FR")) {
				if (getSlots().get(i).getDay().equals("MO") || getSlots().get(i).getDay().equals("FR")) {
					if (overlapCheck(slot, getSlots().get(i)) && !slot.equals(getSlots().get(i))) {
						overlappingSlots.add(getSlots().get(i));
					}
				}	
			} else {
				if (getSlots().get(i).getDay().equals("TU")) {
					if (overlapCheck(slot, getSlots().get(i)) && !slot.equals(getSlots().get(i))) {
						overlappingSlots.add(getSlots().get(i));
					}
				}
			}
		}
		
		return overlappingSlots;
	}

	private static boolean overlapCheck(Slot s1, Slot s2) {
		if (s1.getStartTime().equals(s2.getStartTime()) || (s1.getStartTime().isAfter(s2.getStartTime()) 
					&& s1.getStartTime().isBefore(s2.getEndTime())) || (s1.getEndTime().isAfter(s2.getStartTime()) 
							&& s1.getEndTime().isBefore(s2.getEndTime())) || s1.getEndTime().equals(s2.getEndTime())) {
				return true;
		}
		
		return false;
	}

	private static ArrayList<Class> getCurrentClasses(ArrayList<Pair<Slot, ArrayList<Class>>> assign, boolean courseFlag) {
		HashSet<Class> currentClassesSet = new HashSet<Class>();
		
		// get classes in current assign 
		for (int i = 0; i < assign.size(); i++) {
			for (int j = 0; j < assign.get(i).getSecond().size(); j++) {
				if (courseFlag) {					
					if (assign.get(i).getSecond().get(j).getType().equals("course")) {
						currentClassesSet.add(assign.get(i).getSecond().get(j));											
					}
				} else {
					currentClassesSet.add(assign.get(i).getSecond().get(j));					
				}
			}
		}
		
		ArrayList<Class> currentClasses = new ArrayList<Class>(currentClassesSet);
		return currentClasses;
	}
	
	private static boolean nonCompatible(Slot s, ArrayList<Class> a) {
		ArrayList<Pair<Class, Class>> possiblePairs = new ArrayList<Pair<Class, Class>>();
		
		// find all combinations of arrays
		for (int i = 0; i < a.size(); i++) {
			for (int j = 0; j < a.size(); j++) {
				if (i != j) {					
					Pair<Class, Class> p = new Pair<Class, Class>(a.get(i), a.get(j));
					possiblePairs.add(p);	
				}
			}
		}
			
		for (int i = 0; i < possiblePairs.size(); i++) {
			for (int j = 0; j < getNotCompatible().size(); j++) {
				if (getNotCompatible().get(j).getFirst().equals(possiblePairs.get(i).getFirst()) && 
						getNotCompatible().get(j).getSecond().equals(possiblePairs.get(i).getSecond())) {
					return false;
				}
			}
		}
		
		return true;
	}

	private static boolean unwanted(Slot s, ArrayList<Class> a) {
		// iterate through classes
		for (int i = 0; i < a.size(); i++) {
			Pair<Class, Slot> p = new Pair<Class, Slot>(a.get(i), s);
			if (getUnwanted().contains(p)) {
				return false;
			}
		}
		
		return true;
	}

	private static boolean lunchBreakCheck(Slot s) {
		LocalTime start = LocalTime.parse("11:00");
		LocalTime end = LocalTime.parse("12:30");
		
		if (s.getDay().equals("TU")) {
			if (s.getStartTime().equals(start) || (s.getStartTime().isAfter(start) && s.getStartTime().isBefore(end))) {
				return false;
			}
		}
		
		return true;
	}
	
	private static boolean eveningCheck(ArrayList<Pair<Slot, ArrayList<Class>>> assign) {
		for (int i = 0; i < getClasses().size(); i++) {	
			int firstDigit;
			
			if (getClasses().get(i).getLecture() > 0) {
				firstDigit = Integer.parseInt(Integer.toString(getClasses().get(i).getLecture()).substring(0, 1));				
			} else {
				firstDigit = -1;
			}			
			
			if (firstDigit == 9) {
				for (int j = 0; j < assign.size(); j++) {
					if (assign.get(j).getSecond().contains(getClasses().get(i)) && assign.get(j).getFirst().getStartTime().isBefore(LocalTime.parse("18:00"))) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	private static boolean seniorLevelCheck(ArrayList<Pair<Slot, ArrayList<Class>>> assign) {
		ArrayList<Class> seniorCourses = new ArrayList<Class>();
		ArrayList<Slot> seniorSlots = new ArrayList<Slot>();
		
		// get 500 level courses
		for (int i = 0; i < getClasses().size(); i++) {
			if (getClasses().get(i).getType().equals("course") && (getClasses().get(i).getId() > 499 && getClasses().get(i).getId() < 600))
				seniorCourses.add(getClasses().get(i));
		}
		
		// find slots all 500 level courses are assigned to
		for (int i = 0; i < seniorCourses.size(); i++) {
			for (int j = 0; j < assign.size(); j++) {
				if (assign.get(i).getSecond().contains(seniorCourses.get(i))) {
					seniorSlots.add(assign.get(j).getFirst());
				}
			}			
		}
		
		// check to see if slots are distinct
		HashSet<Slot> seniorSlotHash = new HashSet<Slot>(seniorSlots);
		if (seniorSlotHash.size() < seniorSlots.size()) {
			return false;
		}
		
		return true;
	}

	private static boolean quizzesCheck(ArrayList<Pair<Slot, ArrayList<Class>>> assign) {
		// check that 813 and 913 are scheduled at the correct time
		ArrayList<Class> quizzes = new ArrayList<Class>();
		Class eightThirteen = null;
		Class nineThirteen = null;
		LocalTime start = LocalTime.parse("18:00");
		LocalTime end = LocalTime.parse("19:00");
		
		// get quizzes
		for (int i = 0; i < getClasses().size(); i++) {
			if (getClasses().get(i).getId() == 813) {
				eightThirteen = getClasses().get(i);
				quizzes.add(getClasses().get(i));
				
			} else if (getClasses().get(i).getId() == 913) {
				nineThirteen = getClasses().get(i);
				quizzes.add(getClasses().get(i));
			}
		}
		
	
		// check to see that they are assigned to the correct slot
		for (int i = 0; i < quizzes.size(); i++) {
			for (int j = 0; j < assign.size(); j++) {
				if (assign.get(j).getSecond().contains(quizzes.get(i)) && !(assign.get(j).getFirst().getDay() == "TU" && assign.get(j).getFirst().getStartTime().equals(LocalTime.parse("18:00")))) {
					return false;
				}
			}
			
			Class duplicateClass = (quizzes.get(i).getId() == 813) ? eightThirteen : nineThirteen;
			int transitiveId = (quizzes.get(i).getId() == 813) ? 313 : 413;
			
			// find all potential overlapping courses to check for 813/913
			ArrayList<Class> overlappingCourses = new ArrayList<Class>();
			ArrayList<Slot> overlappingSlots = new ArrayList<Slot>();
			
			// get all courses and tutorials for 313/413
			for (int j = 0; j < getClasses().size(); j++) {
				if (getClasses().get(j).getId() == transitiveId) {
					overlappingCourses.add(getClasses().get(j));
				}
			}
			
			// add any courses and tutorials that are non-compatible with 313
			for (int j = 0; i < getNotCompatible().size(); j++) {
				if (getNotCompatible().get(j).getFirst().getId() == transitiveId) {
					overlappingCourses.add(getNotCompatible().get(j).getSecond());
				} else if (getNotCompatible().get(j).getSecond().getId() == transitiveId) {
					overlappingCourses.add(getNotCompatible().get(j).getFirst());
				}
			}
			

			// remove 813 from overlappingCourses if necessary
			if (overlappingCourses.contains(duplicateClass)) {
				overlappingCourses.remove(overlappingCourses.indexOf(duplicateClass));
			}
			
			// get all assigned slots for any potential overlapping courses
			for (int j = 0; j < overlappingCourses.size(); j++) {
				for (int k = 0; k < assign.size(); k++) {
					if (assign.get(k).getSecond().contains(overlappingCourses.get(j))) {
						overlappingSlots.add(assign.get(k).getFirst());
					}
				}
			}
			
			// check to see if any of the slots overlap with the TU 18:00 slot
			for (int j = 0; j < overlappingSlots.size(); j++) {
				if (overlappingSlots.get(j).getStartTime().equals(start) || (overlappingSlots.get(j).getStartTime().isAfter(start) && overlappingSlots.get(j).getStartTime().isBefore(end))) {
					return false;
				}
			}
		}
		
		return true;
	}

	public static double Eval(ArrayList<Pair<Slot, ArrayList<Class>>> assign, boolean star) {
		ArrayList<Pair<Pair<Class, Slot>, Double>> preferences;
		ArrayList<Pair<Class, Class>> pair;
		
		int wMinFilled = 10;
		int wPref = 10;
		int wPair = 10;
		int wSecDiff = 10;
		
		if (star) {
			ArrayList<Class> currentClasses = getCurrentClasses(assign, false);
			preferences = new ArrayList<Pair<Pair<Class, Slot>, Double>>(); 
			pair = new ArrayList<Pair<Class, Class>>();
			
			// iterate through preferences
			for (int i = 0; i < getPreferences().size(); i++) {
				if (currentClasses.contains(getPreferences().get(i).getFirst().getFirst())) {
					preferences.add(getPreferences().get(i));
				}
			}
			
			// iterate through pair
			for (int i = 0; i < getPair().size(); i++) {
				if (currentClasses.contains(getPair().get(i).getFirst()) && currentClasses.contains(getPair().get(i).getSecond())) {
					pair.add(getPair().get(i));
				}
			}
			
			return pref(assign, preferences) * wPref + pair(assign, pair) * wPair + secDiff(assign) * wSecDiff;
			
		}
		
		return minFilled(assign) * wMinFilled + pref(assign, getPreferences()) * wPref + pair(assign, getPair()) * wPair + secDiff(assign) * wSecDiff;	
	}

	private static double minFilled(ArrayList<Pair<Slot, ArrayList<Class>>> assign) {
		int value = assign.size() * 5;

		// iterate through each slot
		for (int i = 0; i < assign.size(); i++) {
			if (assign.get(i).getSecond().size() < assign.get(i).getFirst().getMin())
				// if size of array of classes is less than coursemin subtract from value
				value = value - 5;
		}
		
		return value * 100 / assign.size();
	}

	private static double pref(ArrayList<Pair<Slot, ArrayList<Class>>> assign, ArrayList<Pair<Pair<Class, Slot>, Double>> preferences) {
		if (preferences.size() == 0) {
			return 0;
		}
		
		double maxPenalty = 0;
		double penalty = 0;
		
		// iterate through preferences
		for (int i = 0; i < preferences.size(); i++) {
			maxPenalty = maxPenalty + preferences.get(i).getSecond();
			// find slot in schedule
			for (int j = 0; j < assign.size(); j++) {
				// if the slots match and the class array does not contain the course in the preference
				if (preferences.get(i).getFirst().getSecond().equals(assign.get(j).getFirst())) {
					if (!assign.get(j).getSecond().contains(preferences.get(i).getFirst().getFirst())) {						
						penalty = penalty + preferences.get(i).getSecond();
					}
				}
			}
		}
		
		return penalty / maxPenalty * 100;
	}
	
	private static double pair(ArrayList<Pair<Slot, ArrayList<Class>>> assign, ArrayList<Pair<Class, Class>> pair) {
		if (pair.size() == 0) {
			return 0;
		}
		
		int value = pair.size() * 5;
		Slot first = new Slot();
		Slot second = new Slot();
		
		// iterate through the pairs
		for (int i = 0; i < pair.size(); i++) {
			// iterate through the schedule 
			for (int j = 0; j < assign.size(); j++) {
				// get assignment for first class in pair
				if (assign.get(j).getSecond().contains(pair.get(i).getFirst())) {
					first = assign.get(j).getFirst();
				}
				
				// get assignment for second class in pair
				if (assign.get(j).getSecond().contains(pair.get(i).getSecond())) {
					second = assign.get(j).getFirst();
				}	
			}
			
			// if assignments don't equal each other apply penalty
			if (!first.equals(second)) {
				value = value - 5;
			}
		}
		
		return value * 100 / pair.size();
	}

	private static double secDiff(ArrayList<Pair<Slot, ArrayList<Class>>> assign) {
		int maxPenalty = 0;
		int penalty = 0;
		
		// iterate through the schedule
		for (int i = 0; i < assign.size(); i++) {
			// get only courses from class array
			ArrayList<Integer> courseSections = new ArrayList<Integer>();
			for (int j = 0; j < assign.get(i).getSecond().size(); j++) {
				if (assign.get(i).getSecond().get(j).getType().equals("course")) {
					courseSections.add(assign.get(i).getSecond().get(j).getId());
				}
			}
			
			// determine max penalty for this slot
			maxPenalty = maxPenalty + courseSections.size() * (courseSections.size() - 1) / 2;
			
			if (maxPenalty == 0) {
				return 0;
			}
			
			// create courseSection Hash and determine penalty from difference in size
			HashSet<Integer> courseSectionHash = new HashSet<Integer>(courseSections);
			penalty = penalty + (courseSections.size() - courseSectionHash.size()) * 5;
		}
		
		return penalty / maxPenalty * 100;
	}	
	 
	public static ArrayList<Slot> getSlots() {
		return slots;
	}
	
	public static void getSlots(ArrayList<Slot> slots) {
		Scheduler.slots = slots;
	}
	
	private static ArrayList<Class> getClasses() {
		return classes;
	}

	public static void setClasses(ArrayList<Class> classes) {
		Scheduler.classes = classes;
	}

	private static ArrayList<Class> getRankedClasses() {
		return rankedClasses;
	}

	private static void setRankedClasses(ArrayList<Pair<Class, Integer>> rankList) {
		ArrayList<Class> rankedClasses = new ArrayList<Class>();
		
		for (int i = 0; i < rankList.size(); i++) {
			rankedClasses.add(rankList.get(i).getFirst());
		}
		
		Scheduler.rankedClasses = rankedClasses;
	}

	private static ArrayList<Pair<Slot, Integer>> getRankedSlots() {
		return rankedSlots;
	}

	private static void setRankedSlots(ArrayList<Pair<Slot, Integer>> rankedSlots) {
		Scheduler.rankedSlots = rankedSlots;
	}

	private static ArrayList<Pair<Class, Class>> getNotCompatible() {
		return notCompatible;
	}

	public static void setNotCompatible(ArrayList<Pair<Class, Class>> notCompatible) {
		Scheduler.notCompatible = notCompatible;
	}

	private static ArrayList<Pair<Class, Slot>> getUnwanted() {
		return unwanted;
	}

	public static void setUnwanted(ArrayList<Pair<Class, Slot>> unwanted) {
		Scheduler.unwanted = unwanted;
	}

	private static ArrayList<Pair<Pair<Class, Slot>, Double>> getPreferences() {
		return preferences;
	}

	public static void setPreferences(ArrayList<Pair<Pair<Class, Slot>, Double>> preferences) {
		Scheduler.preferences = preferences;
	}

	public static ArrayList<Pair<Class, Class>> getPair() { return pair; }

	public static void setPair(ArrayList<Pair<Class, Class>> pair) {
		Scheduler.pair = pair;
	}

	
}
