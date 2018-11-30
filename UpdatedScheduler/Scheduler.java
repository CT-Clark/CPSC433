package CPSC433master.scheduler.execution;

// Custom objects
import CPSC433master.scheduler.structures.Class;
import CPSC433master.scheduler.structures.Course;
import CPSC433master.scheduler.structures.Lab;
import structures.Pair;
import structures.Slot;
import CPSC433master.scheduler.tree.Assignment;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Scheduler {

    // Fields
	private static ArrayList<Slot> slots = new ArrayList<>();
	private static ArrayList<Class> classes = new ArrayList<>();
	private static ArrayList<Class> unassignedClasses = new ArrayList<>();
	private static ArrayList<Pair<Class, Class>> notCompatible = new ArrayList<>();
	private static ArrayList<Pair<Class, Slot>> unwanted = new ArrayList<>();
	private static ArrayList<Pair<Pair<Class, Slot>, Double>> preferences = new ArrayList<>();
	private static ArrayList<Pair<Class, Class>> pairs = new ArrayList<>();
	private static ArrayList<Assignment> assignList = new ArrayList<>();
	private static Assignment partassign;
	
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
		
		// System.out.println("Finished parsing and creating first tier nodes.");

		// -------------------------------------------------------- //
		// -------------------- SEARCH CONTROL -------------------- //
        // -------------------------------------------------------- //

		// Search for the assignment with the lowest eval value
		// If it's finished, that's our answer

		double run = 0;
		boolean foundResult = false;

		while(!foundResult) {

			// If there are no more nodes to expand and a solution hasn't been found, then there isn't one
			if(assignList.size() == 0) {
				System.out.println("No valid solution found.");
				break;
			}
			run++;

			//System.out.println("RUN: " + run);

			Assignment result = new Assignment();
			result.setEvalValue(1000000);
			// Find the assignment with the lowest eval score
			for(Assignment a : assignList) {
				if (a.getEvalValue() < result.getEvalValue()) {
					result = a;
					//System.out.println(a);
				}
			}

			// Uncomment if you would like to see progress for long runs
			//if((run % 10) == 0) { System.out.println("RUN: " + run);
			//System.out.println(result); }

			//System.out.println("Num of Assigns in assignList: " + assignList.size());

			// An answer has been found
			if(result.getUnassignedClasses().size() == 0){
				foundResult = true;
				System.out.println("FINAL ASSIGNMENT");
				System.out.println(result);
			}
			// If an answer hasn't been found yet...
			else {
				for(Class c : result.getUnassignedClasses()) {
					for(Slot st : getSlots()) {

						// Create a new node
						Assignment a = new Assignment(result.getAssign(), result.getUnassignedClasses());
						a.assignClass(st, c);
						//System.out.println(a);

						if(a.constr()) {
							a.evalue(); // Update assignment evalValue
							//a.printTotalPens();
							assignList.add(a);
							//System.out.println(a);
						}
					}
				}
			}
			assignList.remove(result);
		}

	}

	// ------------------------------------------------ //
	// -------------------- PARSER -------------------- //
	// ------------------------------------------------ //

	// Parses the input file
	private static void parseInput(String path) throws IOException {
		try (Scanner scanner = new Scanner(new File(path))) {
			List<String> inputFile = Files.readAllLines(Paths.get(path));
			boolean partialAssignmentReached = false;
			boolean partialAssignmentReachedAgain = false;

			int lastBreakpoint = 0;
			int counter = 0;
			for (String line : inputFile) {
				line = line.toLowerCase();
				switch (line) {

					case "name:":
						lastBreakpoint = counter + 1;
						break;
					// Name of the department
					case "course slots:":
						saveName(inputFile, lastBreakpoint, counter);
						lastBreakpoint = counter + 1;
						break;
					// Generate list of course slots
					case "lab slots:":
						saveSlots(inputFile, lastBreakpoint, counter, "course");
						lastBreakpoint = counter + 1;
						break;
					// Generate list of lab slots
					case "courses:":
						saveSlots(inputFile, lastBreakpoint, counter, "lab");
						lastBreakpoint = counter + 1;
						break;
					// Generate list of courses
					case "labs:":
						saveCourses(inputFile, lastBreakpoint, counter);
						lastBreakpoint = counter + 1;
						break;
					// Generate list of labs
					case "not compatible:":
						saveLabs(inputFile, lastBreakpoint, counter);
						lastBreakpoint = counter + 1;
						break;
					// Generate list of not-compatible courses/labs
					case "unwanted:":
						saveNotCompatible(inputFile, lastBreakpoint, counter);
						lastBreakpoint = counter + 1;
						break;
					// Generate list of courses that cannot be assigned to a particular slot
					case "preferences:":
						saveUnwanted(inputFile, lastBreakpoint, counter);
						lastBreakpoint = counter + 1;
						break;
					// Generate the list of courses/tutorials that should be put into a specific slot
					// Along with the Eval penalty if they're not
					case "pair:":
						savePreferences(inputFile, lastBreakpoint, counter);
						lastBreakpoint = counter + 1;
						break;
					// Generate the list of courses/labs that should be placed int other same slot
					case "partial assignments:":
						savePairs(inputFile, lastBreakpoint, counter);
						partialAssignmentReached = true;
						partassign = new Assignment(); // Initialize the starting assignment
						lastBreakpoint = counter + 1;
						break;
				}
				counter++;

				// We want the line after "partial assignment"
				if(partialAssignmentReached) {
					if(partialAssignmentReachedAgain) {
						parsePartassign(line);
					}
					partialAssignmentReachedAgain = true;
				}

			}
			partassign.evalue();
			assignList.add(partassign); // At the starting assignment to the list of assignments
		}

	}

	// -------------------- PARSER METHODS -------------------- //

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

		return new Slot(type, day, time, max, min);
	}

	// Saves all of the slot objects
	private static void saveSlots(List<String> inputFile, int lastBreakpoint, int counter, String type) {
		for (String line : inputFile.subList(lastBreakpoint, counter)) {
			if (line.isEmpty()) {
				continue;
			}
			getSlots().add(parseSlot(line, type));
		}
	}

	// Parses the class objects
	private static Class parseClass(String line) {
		String[] inputLine = line.split(" ");
		ArrayList<String> classItems = new ArrayList<>();

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

		return new Pair<>(cl, sl);
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

		return new Pair<>(p1, Double.parseDouble((pairItems[3])));
	}

	// Saves the pairs of courses/labs that should run at the same time
	private static void savePairs(List<String> inputFile, int lastBreakpoint, int counter) {
		for (String line : inputFile.subList(lastBreakpoint, counter)) {
			if(line.isEmpty()) {
				continue;
			}
			Pair<Class, Class> pair = parsePair(line);
			getPairs().add(pair);
		}
	}

	// Parses the pair of courses/labs that should run at the same time
	private static Pair<Class, Class> parsePair(String line) {
		String[] pairItems = line.split(", ");
		
		Class cl1 = parseClass(pairItems[0]);
		Class cl2 = parseClass(pairItems[1]);

		return new Pair<>(cl1, cl2);
		
	}

	// Parses the partial assignment section and initializes the assignment, even if there isn't a partassign
	private static void parsePartassign(String line) {
		if(line.length() > 0) {
			String[] pairItems = line.split(", ");
			Class cl = parseClass(pairItems[0].toUpperCase());

			// Iterate through all the slots
			for(Pair<Slot, ArrayList<Class>> p : partassign.getAssign()) {
				// See if the Slot's type is the same as the class' type
				if(p.getFirst().getType().equals(cl.getType())) { // If the slot is the same type...
					// If the slot is the same day...
					if(p.getFirst().getDay().equals(pairItems[1].toUpperCase())) {
						// If they share a start time...
						if(p.getFirst().getStartTime().toString().equals((getLocalTime(pairItems[2])).toString()) ) { // If the slot starts at the same time...
							cl.setDept(cl.getDept().toUpperCase()); // Reset the case of the dept
							partassign.assignClass(p.getFirst(), cl); // Assign the class to the slot
						}
					}
				}
			}
		}
	}

	// -------------------------------------------------------------- //
	// -------------------- EXTRA METHODS --------------------------- //
	// -------------------------------------------------------------- //

	
	private static LocalTime getLocalTime(String str) {
		String timeString = str.replaceAll("\\s+", "");
		 
		if (timeString.length() == 4) {
			timeString = "0" + timeString;
		}

		return LocalTime.parse(timeString);
	}

	// Assemble all of the dedicated course slots
	private static ArrayList<Slot> getCourseSlots() {
		ArrayList<Slot> slots = new ArrayList<>();
		
		for (int i = 0; i < getSlots().size(); i++) { 
			if (getSlots().get(i).getType().equals("course")) {
				slots.add(getSlots().get(i));
			}
		}
		
		return slots;
	}

	// Assemble all the dedicated lab slots
	private static ArrayList<Slot> getLabSlots() {
		ArrayList<Slot> slots = new ArrayList<>();
		
		for (int i = 0; i < getSlots().size(); i++) { 
			if (getSlots().get(i).getType().equals("lab")) {
				slots.add(getSlots().get(i));
			}
		}
		
		return slots;
	}

    // ------------------------------------------------------------------- //
    // -------------------- GETTER AND SETTER METHODS -------------------- //
    // ------------------------------------------------------------------- //


    public static ArrayList<Slot> getSlots() {
        return slots;
    }


    public static ArrayList<Class> getClasses() {
        return classes;
    }

    public static ArrayList<Pair<Class, Class>> getNotCompatible() {
        return notCompatible;
    }

    public static ArrayList<Pair<Class, Slot>> getUnwanted() {
        return unwanted;
    }

    public static ArrayList<Pair<Pair<Class, Slot>, Double>> getPreferences() {
        return preferences;
    }

    public static ArrayList<Pair<Class, Class>> getPairs() { return pairs; }

}

