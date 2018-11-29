package CPSC433master.scheduler.execution;

import CPSC433master.scheduler.structures.Class;
import CPSC433master.scheduler.structures.Course;
import CPSC433master.scheduler.structures.Lab;
import structures.Pair;
import structures.Slot;
import CPSC433master.scheduler.tree.Assignment;

import java.io.File;
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
	private static ArrayList<Class> unassignedClasses = new ArrayList<>();
	private static ArrayList<Pair<Class, Class>> notCompatible = new ArrayList<Pair<Class, Class>>();
	private static ArrayList<Pair<Class, Slot>> unwanted = new ArrayList<Pair<Class, Slot>>();
	private static ArrayList<Pair<Pair<Class, Slot>, Double>> preferences = new ArrayList<Pair<Pair<Class, Slot>, Double>>();
	private static ArrayList<Pair<Class, Class>> pair = new ArrayList<Pair<Class, Class>>();
	private static ArrayList<Assignment> assignList = new ArrayList<>();
	
	public static void main(String[] args){
	
		// Parse the text file and set up appropriate attributes
		String path = args[0];
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		System.out.println("Current relative path is: " + s);
		if (!Files.exists(Paths.get(path)) || !Files.isRegularFile(Paths.get(path))) {
			throw new IllegalArgumentException("Input file not available: " + Paths.get(path) + "!");
		}

		try {
			parseInput(args[0]); // Pass the file to parse through the command line
		} catch (IOException e) {
			e.printStackTrace();
		}

		// -------------------- INITIALIZE THE STARTING NODES -------------------- //

		boolean found = false;
		unassignedClasses.addAll(classes); // Copy all of the classes into an unassigned array

		for(Class tempClass : classes) { // For each course/lab
			for(Slot tempSlot : slots) { // Place it in a different possible slot
				// Create a list of all of the slots
				ArrayList<Slot> s1 = new ArrayList<>();
				s1.addAll(slots);
				// Create a proper assign
				ArrayList<Pair<Slot, ArrayList<Class>>> arg = new ArrayList<>();
				for(Slot tempSlot2 : s1) {
					Pair<Slot, ArrayList<Class>> tp = new Pair<>();
					tp.setFirst(tempSlot2);
					arg.add(tp);
				}
				// Create a copy of the unassigned classes
				ArrayList<Class> tc = new ArrayList<>();
				tc.addAll(unassignedClasses);
				Assignment a = new Assignment(arg, tc);
				a.assignClass(tempSlot, tempClass); // Create a starting assignment for every course/lab in every slot
				assignList.add(a); // Add that starting assignment to the list of assignments
			}
		}

		// -------------------- SEARCH CONTROL -------------------- //

		// Search for the assignment with the lowest eval value
		// If it's finished, that's our answer
		Pair<Assignment, Double> result = new Pair<>();
		while(!found) {
			result.setSecond((double)999999); // Set the first best score to slightly lower than the default eval score
			// Find the assignment with the lowest eval score
			for(Assignment a : assignList) {
				if (a.getEvalValue() < result.getSecond()) {
					Pair<Assignment, Double> change = new Pair<>();
					change.setFirst(a);
					change.setSecond(a.getEvalValue());
					result = change; // Save the result for future comparisons
				}
			}
			// If the assignment is finished, then we have our answer
			if (result.getFirst().getSizeOfUnassignedClasses() == 0) { found = true; }
			// If it's not finished then we assign it something from its unassigned list
			else {
				for (Class c : result.getFirst().getUnassignedClasses()) { // For each unassigned class
					for(Slot st : slots) { // Place it in all different possible slots
						Assignment a = new Assignment(result.getFirst().getAssign(), result.getFirst().getUnassignedClasses()); // Copy the current best answer
						a.assignClass(st, c); // Assign one of their unassigned classes to it
						// Check to see if the new assignment is legal
						if(Constr(a)) {
							a.setEvalValue(eval(a, true)); // Figure out the eval value and then add it to the list of assignments
							assignList.add(a); // Add that assignment to the list of assignments
						}
					}
				}
				assignList.remove(result.getFirst()); // Remove an already expanded node from the list
			}
		}

		// Print out results
		result.getFirst().toString();
		System.out.println(result.getFirst());

	}

	// ------------------------------------------------ //
	// -------------------- PARSER -------------------- //
	// ------------------------------------------------ //

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
			// TODO: Find a way to parse and save partial assignments
			//savePartialAssignments(inputFile, lastBreakpoint, counter - 1);
		}

	}

	//--------------------------------------------------//
	//--------------------------------------------------//
	//--------------------------------------------------//

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
		//normalizeRankings(getPreferences());
	}

	// Parses the course/lab slot preferences and their respective values
	private static Pair<Pair<Class, Slot>, Double> parsePreferences(String line) {
		String[] pairItems = line.split(", ");

		Pair<Class, Slot> p1 = parseUnwanted(pairItems[2] + ", " + pairItems[0] + ", " + pairItems[1]);
		Pair<Pair<Class, Slot>, Double> p = new Pair<Pair<Class, Slot>, Double>(p1, Double.parseDouble((pairItems[3])));

		return p;
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


	// -------------------------------------------------------------- //
	// -------------------- EXTRA METHODS --------------------------- //
	// -------------------------------------------------------------- //

	
	private static LocalTime getLocalTime(String str) {
		String timeString = str.replaceAll("\\s+", "");
		 
		if (timeString.length() == 4) {
			timeString = "0" + timeString;
		}
		
		LocalTime time = LocalTime.parse(timeString);
		return time;		
	}

	// Assemble all of the dedicated course slots
	private static ArrayList<Slot> getCourseSlots() {
		ArrayList<Slot> slots = new ArrayList<Slot>();
		
		for (int i = 0; i < getSlots().size(); i++) { 
			if (getSlots().get(i).getType().equals("course")) {
				slots.add(getSlots().get(i));
			}
		}
		
		return slots;
	}

	// Assemble all the dedicated lab slots
	private static ArrayList<Slot> getLabSlots() {
		ArrayList<Slot> slots = new ArrayList<Slot>();
		
		for (int i = 0; i < getSlots().size(); i++) { 
			if (getSlots().get(i).getType() == "lab") {
				slots.add(getSlots().get(i));
			}
		}
		
		return slots;
	}

	// Get a list of all courses/labs assigned to a particular slot
	private static ArrayList<Class> getClassesFromSlot(Assignment a) {
		ArrayList<Class> classes = new ArrayList<Class>();

		for (int i = 0; i < assign.size(); i++) {
			if (assign.get(i).getFirst().equals(slot)) {
				classes.addAll(assign.get(i).getSecond());
			}
		}

		return classes;
	}

	// ----------------------------------------------------------------------- //
	// -------------------- CONSTR AND ASSOCIATED METHODS -------------------- //
	// ----------------------------------------------------------------------- //


	public static boolean Const(Assignment a) {
		// Ensure that course objects are in course slots, and lab objects are in lab slots
		if(!ensureType(a)) { return false; }

		// Ensure that the number of courses assigned to a slot is less than courseMax
		if(!slotMax(a)) { return false; }

		// Ensure that each lab is scheduled at a different time than its course
		if(!sectionOverlap(a)) { return false; }

		// Ensure that non-compatible courses are assigned to different slots
		if(!nonCompatible(a)) { return false; }
		
		// Ensure that no course is placed into a slot where it's unwanted
		if(!unwanted(a)) { return false; }
		
		// Ensure that no classes take palce during lunch
		if(!lunchBreakCheck(a)) { return false; }

		// Ensure that LEC 9 courses are assigned in the evenings
		if(!eveningCheck(a)) { return false; }
		
		// Ensure that all 500 level courses are placed into different slots
		if(!seniorLevelCheck(a)) { return false; }
	
		// Ensure that CPSC 813 and CPSC 913 behave
		if(!quizzesCheck(a))) { return false; }

		return true;
	}

	// Method for ensuring that all class objects are set to their respective slots
	private static boolean ensureType(Assignment a) {
		for(int i = 0; i < a.getAssign().size(); i++) { // Go through all the slots
			Pair<Slot, ArrayList<Class>> ta = a.getAssign().get(i);
			for(Class c : ta.getSecond()) { // Go through all the classes in the slots
				if(!c.getType().equals(ta.getFirst().getType())) { return false; }
			}
		}
		return true;
	}

	// Checks whether there are more courses assigned to a slot than there should be
	private static boolean slotMax(Assignment a) {
		int courseCount = 0;
		int labCount = 0;
		for(int i = 0; i < a.getAssign().size(); i++) { // Go through all the slots
			// Increment course or lab count if either of those courses are found
			for(int j = 0; j < a.getAssign().get(i).getSecond().size(); j++) {
				if(a.getAssign().get(i).getSecond().get(j).getType().equals("course")) { courseCount++; }
				else { labCount++; }
			}
			// Max is exceeded, return false
			if(courseCount > a.getAssign().get(i).getFirst().getMax() && a.getAssign().get(i).getFirst().getType().equals("course")) {
				return false;
			} else if (labCount > a.getAssign().get(i).getFirst().getMax() && a.getAssign().get(i).getFirst().getType().equals("lab")) {
				return false;
			}
		}
		return true;
	}

	private static boolean sectionOverlap(Assignment a) {
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

	// Checks whether or not two slots overlap
	private static boolean overlapCheck(Slot s1, Slot s2) {
		if (s1.getStartTime().equals(s2.getStartTime()) || (s1.getStartTime().isAfter(s2.getStartTime()) 
					&& s1.getStartTime().isBefore(s2.getEndTime())) || (s1.getEndTime().isAfter(s2.getStartTime()) 
							&& s1.getEndTime().isBefore(s2.getEndTime())) || s1.getEndTime().equals(s2.getEndTime())) {
				return true;
		}
		
		return false;
	}

	// Check to make sure all of the classes that are not compatible with each other are not assigned at the same time
	private static boolean nonCompatible(Assignment a) {
		for(int i = 0; i < a.getAssign().size(); i++) { // Go through all the slots
			for(int j = 0; j < a.getAssign().get(i).getSecond().size(); j++) { // Then all the courses
				for(int k = 0; k < notCompatible.size(); k++) {
					// If The first of the notCompatible pairs == the assigned course, look to see
					// if the slot contains the second of the pair
					if(notCompatible.get(k).getFirst().equals(a.getAssign().get(i).getSecond().get(j))) {
						if (a.getAssign().get(i).getSecond().contains(notCompatible.get(k).getSecond())) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	// Checks whether any courses/labs have been placed in an unwanted slot
	private static boolean unwanted(Assignment a) {
		for(int i = 0; i < a.getAssign().size(); i++) { // Go through all the slots
			for (int j = 0; j < a.getAssign().get(i).getSecond().size(); j++) { // Then all the courses
				for(int k = 0; k < unwanted.size(); k++) { // Go through all the unwanted pairs
					if(a.getAssign().get(i).getSecond().get(j).equals(unwanted.get(k).getFirst()) && // If the course matchs
					a.getAssign().get(i).getFirst().equals(unwanted.get(k).getSecond())) { // And the slot matches
						return false; // Return false
					}
				}
			}
		}
		
		return true;
	}

	// Checks to see whether any courses/labs have been assigned to lunch time
	private static boolean lunchBreakCheck(Assignment a) {
		LocalTime start = LocalTime.parse("11:00");
		LocalTime end = LocalTime.parse("12:30");

		for(int i = 0; i < a.getAssign().size(); i++) { // Go through all the slots
			for (int j = 0; j < a.getAssign().get(i).getSecond().size(); j++) { // Then all the courses
				if(a.getAssign().get(i).getFirst().getDay().equals("TU")) { // Just check on Tuesday
					// Now check if there's a slot at that time
					if(a.getAssign().get(i).getFirst().getStartTime().equals(start) ||
							(a.getAssign().get(i).getFirst().getStartTime().isAfter(start)) &&
									a.getAssign().get(i).getFirst().getStartTime().isBefore(end)) {
						// If there is, check if a course has been scheduled into there
						if (a.getAssign().get(i).getSecond().get(j).getType().equals("course")) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	// Checks to see whether the correct courses have been placed in the evening
	private static boolean eveningCheck(Assignment a){ for (int i = 0; i < a.getAssign().size(); i++) {
		// Go through all the slots
		for (int j = 0; j < a.getAssign().get(i).getSecond().size(); j++) {
			// Then all the courses
			if (a.getAssign().get(i).getSecond().get(j).getLecture() >= 90) {
				if (a.getAssign().get(i).getFirst().getStartTime().isBefore(LocalTime.parse("18:00"))) {
					return false;
				}
			}
		}
	}

	// Checks to see if any 500 level course has been assigned the same slot as another 500 level course
	private static boolean seniorLevelCheck(Assignment a) {
		if(true){}
		return true;
	}

	// check that 813 and 913 are scheduled at the correct time
	private static boolean quizzesCheck(Assignment a) {

		return true;
	}


				// --------------------------------------------------------------------- //
				// -------------------- EVAL AND ASSOCIATED METHODS -------------------- //
				// --------------------------------------------------------------------- //


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

				// Check whether or not the slot has a minimum number of courses/labs in it
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

				// Check whether the courses/labs with slot preferences have been assigned to their respective slots
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

				// Check whether two courses have been scheduled at the same time as their paired course
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

				// ------------------------------------------------------------------- //
				// -------------------- GETTER AND SETTER METHODS -------------------- //
				// ------------------------------------------------------------------- //


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
		}
