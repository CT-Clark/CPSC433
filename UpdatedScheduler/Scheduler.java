package CPSC433master.scheduler.execution;

// Custom objects
import CPSC433master.scheduler.structures.Class;
import CPSC433master.scheduler.structures.Course;
import CPSC433master.scheduler.structures.Lab;
import CPSC433master.scheduler.structures.Pair;
import CPSC433master.scheduler.structures.Slot;
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
import java.util.HashSet;

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

	// For 813/913 requirements
	private static Course class813 = new Course("CPSC", 813, 1);
	private static Course class913 = new Course("CPSC", 813, 1);

	// Diagnostics
	public static long creationSteps = 0;
	public static long assignClassSteps = 0;
	public static long preferenceEvalSteps = 0;
	public static long sameSlotEvalSteps = 0;
	public static long pairEvalSteps = 0;
	private static long unwantedSteps = 0;
	private static long steps500 = 0;
	private static long conflictSteps = 0;
	private static long notCompatibleSteps = 0;
	
	public static void main(String[] args){
	
		// Parse the text file and set up appropriate attributes
		String path = args[0];
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		System.out.println("Current relative path is: " + s);
		if (!Files.exists(Paths.get(path)) || !Files.isRegularFile(Paths.get(path))) {
			throw new IllegalArgumentException("Input file not available: " + Paths.get(path) + "!");
		}

		// Parse the input file and create lists of important info
		try {
			parseInput(args[0]); // Pass the file to parse through the command line
		} catch (IOException e) {
			e.printStackTrace();
		}

		// These ensure the 813-313/913-413 requirements are met
		// Creates a new notCompatible listing if CPSC 813/913 exist
		for(Class c : getClasses()) {
			// Check if CPSC 813 even exists
			if(c.getType().equals("course") && c.getDept().equals("CPSC") && c.getId() == 813) {
				// Any nonCompatible 313 course also extends to not being compatible with 813
				for(Class c1 : getClasses()) {
					if(c1.getDept().equals("CPSC") && c1.getId() == 313) {
						Pair<Class, Class> newPair1 = new Pair(c, c1);
						getNotCompatible().add(newPair1);
					}
				}
				// Go through and any course notCompatible with CPSC 313 is also not compatible with CPSC 813
				for(Pair<Class, Class> pair : getNotCompatible()) {
					boolean dupFound = false; // Make sure we're not adding in duplicate pairs
					// Check the first side of the notCompatible, then the second
					if(pair.getFirst().getDept().equals("CPSC") && pair.getFirst().getId() == 313) {
						Pair<Class, Class> newPair1 = new Pair(c, pair.getFirst());
						Pair<Class, Class> newPair2 = new Pair(c, pair.getSecond());
						for(Pair<Class, Class> pair2 : getNotCompatible()) {
							if (pair2.equals(newPair1)) {
								dupFound = true;
							}
						}
						if(!dupFound) { getNotCompatible().add(newPair1); }
						dupFound = false;
						for(Pair<Class, Class> pair2 : getNotCompatible()) {
							if (pair2.equals(newPair2)) {
								dupFound = true;
							}
						}
						if(!dupFound) { getNotCompatible().add(newPair2); }
					} else if (pair.getSecond().getDept().equals("CPSC") && pair.getSecond().getId() == 313) {
						Pair<Class, Class> newPair1 = new Pair(c, pair.getFirst());
						Pair<Class, Class> newPair2 = new Pair(c, pair.getSecond());
						for(Pair<Class, Class> pair2 : getNotCompatible()) {
							if (pair2.equals(newPair1)) {
								dupFound = true;
							}
						}
						if(!dupFound) { getNotCompatible().add(newPair1); }
						dupFound = false;
						for(Pair<Class, Class> pair2 : getNotCompatible()) {
							if (pair2.equals(newPair2)) {
								dupFound = true;
							}
						}
						if(!dupFound) { getNotCompatible().add(newPair2); }
					}
				}
			// Check if CPSC 913 even exists
			} else if (c.getType().equals("course") && c.getDept().equals("CPSC") && c.getId() == 913) {
				for(Class c1 : getClasses()) {
					if(c1.getDept().equals("CPSC") && c1.getId() == 413) {
						Pair<Class, Class> newPair1 = new Pair(c, c1);
						getNotCompatible().add(newPair1);
					}
				}
				// Go through and any course notCompatible with CPSC 313 is also not compatible with CPSC 813
				for(Pair<Class, Class> pair : getNotCompatible()) {
					boolean dupFound = false; // Make sure we're not adding in duplicate pairs
					// Check the first side of the notCompatible, then the second
					if(pair.getFirst().getDept().equals("CPSC") && pair.getFirst().getId() == 413) {
						Pair<Class, Class> newPair1 = new Pair(c, pair.getFirst());
						Pair<Class, Class> newPair2 = new Pair(c, pair.getSecond());
						for(Pair<Class, Class> pair2 : getNotCompatible()) {
							if (pair2.equals(newPair1)) {
								dupFound = true;
							}
						}
						if(!dupFound) { getNotCompatible().add(newPair1); }
						dupFound = false;
						for(Pair<Class, Class> pair2 : getNotCompatible()) {
							if (pair2.equals(newPair2)) {
								dupFound = true;
							}
						}
						if(!dupFound) { getNotCompatible().add(newPair2); }
					} else if (pair.getSecond().getDept().equals("CPSC") && pair.getSecond().getId() == 413) {
						Pair<Class, Class> newPair1 = new Pair(c, pair.getFirst());
						Pair<Class, Class> newPair2 = new Pair(c, pair.getSecond());
						for(Pair<Class, Class> pair2 : getNotCompatible()) {
							if (pair2.equals(newPair1)) {
								dupFound = true;
							}
						}
						if(!dupFound) { getNotCompatible().add(newPair1); }
						dupFound = false;
						for(Pair<Class, Class> pair2 : getNotCompatible()) {
							if (pair2.equals(newPair2)) {
								dupFound = true;
							}
						}
						if(!dupFound) { getNotCompatible().add(newPair2); }
					}
				}
			}
		}

		System.out.println(partassign); // Prints out the initial assignment

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
			Assignment result = new Assignment();

			// Find the assignment with the lowest eval score
			for(Assignment a : assignList) {
				if (a.getEvalValue() < result.getEvalValue()) {
					result = a;
				}
			}

			assignList.remove(result); // Remove the selected assignment from the list of assignments to expand
			assignList.trimToSize(); // Just an extra trim for the assignList

			// Uncomment if you would like to see progress for long runs
			if((run % 100) == 0) {
				System.out.println("RUN: " + run);
				System.out.println(result);
				System.out.println("Number of courses left to assign: " + result.getUnassignedClasses().size());
				result.printTotalPens();
				System.out.println("Size of assignList: " + assignList.size());

				// Diagnostic numbers
				System.out.println(creationSteps);
				System.out.println(assignClassSteps);
				System.out.println(preferenceEvalSteps);
				System.out.println(sameSlotEvalSteps);
				System.out.println(pairEvalSteps + "\n");
				// -----
				System.out.println(unwantedSteps);
				System.out.println(steps500);
				System.out.println(conflictSteps);
				System.out.println(notCompatibleSteps);
			}

			// An answer has been found
			if(result.getUnassignedClasses().size() == 0){
				foundResult = true;
				System.out.println("FINAL ASSIGNMENT");
				System.out.println(result);

				// Diagnostic numbers
				result.printTotalPens();
				System.out.println(creationSteps);
				System.out.println(assignClassSteps);
				System.out.println(preferenceEvalSteps);
				System.out.println(sameSlotEvalSteps);
				System.out.println(pairEvalSteps + "\n");
				// -----
				System.out.println(unwantedSteps);
				System.out.println(steps500);
				System.out.println(conflictSteps);
				System.out.println(notCompatibleSteps);
			}
			// If an answer hasn't been found yet...
			else {
				// Go through all of the unassigned classes
				for(Class c : result.getUnassignedClasses()) {

					// And try to assign them to a slot
					for(Pair<Slot, HashSet<Class>> p : result.getAssign()) {

						// -------------------- CONSTR CHECKS -------------------- //

						// Check to make sure the course going into the slot is of the correct type
						// Works correctly
						// Check to see if the course is about to break the max limit
						// Works correctly
						//System.out.println("Course: " + c + " Slot: " + p.getFirst());}
						//else { System.out.println("Course max exceeded");
						if (c.getType().equals(p.getFirst().getType()) && p.getSecond().size() < p.getFirst().getMax()) {

							// Checks to see if it's an evening class not being put in the evening
							// Works correctly
							if (!(c.getLecture() >= 90 && p.getFirst().getStartTime().isBefore(LocalTime.parse("18:00")))) {

								// Check to make sure CPSC 813/913 are on Tuesdays at a particular time
								if (c.getId() == 813 || c.getId() == 913) {
									// Make sure that 813 and 913 are on Tuesday
									if (!p.getFirst().getDay().equals("TU")) {
										continue;
									}
									// Check if 813 or 913 start at 18:00
									LocalTime time = LocalTime.of(18, 0, 0);
									if (p.getFirst().getStartTime().compareTo(time) != 0) {
										continue;
									}
								}

								// Check to see if the course is about to occupy an unwanted slot
								boolean unwantedFound = false;
								for (Pair<Class, Slot> up : unwanted) {
									unwantedSteps++;
									if (c.equals(up.getFirst()) && p.getFirst().equals(up.getSecond())) {
										unwantedFound = true;
										break;
									}
								}
								if (unwantedFound) {
									//System.out.println("Course unwanted in this slot");
									//System.out.println("Course: " + c + " Slot: " + p.getFirst());
									continue;
								}

								// Checks to see if it occurs over lunch
								LocalTime start = LocalTime.parse("11:00");
								LocalTime end = LocalTime.parse("12:30");
								if ((c.getType().equals("course")
										&& p.getFirst().getDay().equals("TU")
										&& (p.getFirst().getStartTime().equals(start)
										|| (p.getFirst().getStartTime().isAfter(start)
										&& p.getFirst().getStartTime().isBefore(end))))) {
									continue;
								}


								// Checks to make sure that if a 500 level course has been assigned to this slot
								// that it doesn't assign another 500 level course to it
								// Works correctly
								boolean found500 = false;
								if (c.getType().equals("course")) {
									for (Class cl : p.getSecond()) {
										steps500++;
										// If this is the second 500 level course in the slot, don't assign
										if (cl.getId() >= 500
												&& c.getId() >= 500
												&& cl.getType().equals("course")) {
											found500 = true;
											//System.out.println("Too many 500 level courses");
												//System.out.println(c);
											break;
										}
									}
								}
								// Go on to try to assign the next class
								if (found500) {
									continue;
								}


								// Check to see if placing it here would conflict with other slots
								// 813/913 conflict prevention built-in
								boolean conflictFound = false;
								for (Pair<Slot, HashSet<Class>> conSlot : result.getAssign()) {
									conflictSteps++;
									// Don't worry about checking slots if they're empty
									if (!conSlot.getSecond().isEmpty()) {
										// Don;t check if the slots aren't on the same day
										// Check only slots that occur at the same time
										if ((conSlot.getFirst().getDay().equals(p.getFirst().getDay())
												|| (conSlot.getFirst().getDay().equals("MO") && p.getFirst().getDay().equals("FR"))
												|| (conSlot.getFirst().getDay().equals("FR") && p.getFirst().getDay().equals("MO")))
												&& (conSlot.getFirst().getStartTime().equals(p.getFirst().getStartTime())
												|| (conSlot.getFirst().getStartTime().isAfter(p.getFirst().getStartTime())
												&& conSlot.getFirst().getStartTime().isBefore(p.getFirst().getEndTime()))
												|| (p.getFirst().getStartTime().isAfter(conSlot.getFirst().getStartTime())
												&& p.getFirst().getStartTime().isBefore(conSlot.getFirst().getEndTime())))) {

											// Tutorials and Courses for the same LEC number cannot go together
											// Go through all of the classes in the slots that conflict with the slot we're trying to assign a class to
											for (Class conClass : conSlot.getSecond()) {
												conflictSteps++;
												// If it's the same type of class, break, they can't go together
												if (conClass.getDept().equals(c.getDept())
														&& conClass.getId() == c.getId()
														&& (conClass.getLecture() == c.getLecture() // If the lecture number is the same
														|| c.getLecture() == -1             // Or if it's supposed to be open to everyone no matter the lecture
														|| conClass.getLecture() == -1)
														&& !conClass.equals(c)) { // Don't compare the same classes
													conflictFound = true;
													//System.out.println("Courses and their respective tutorials cannot be assigned at the same times");
													break;
												}

												// Check to make sure there will not be a nonCompatible pair of courses in the assignment
												for (Pair<Class, Class> p2 : notCompatible) {
													notCompatibleSteps++;
													// If the class to add is the same as the first nonCompatible element
													if (c.equals(p2.getFirst())) {
														notCompatibleSteps++;
														if (p2.getSecond().equals(conClass)) {
															conflictFound = true;
															//System.out.println("Class wasn't compatible");
															break;
														}
														// Of if it's the second
													} else if (c.equals(p2.getSecond())) {
														notCompatibleSteps++;
														if (p2.getFirst().equals(conClass)) {
															conflictFound = true;
															//System.out.println("Class wasn't compatible");
															break;
														}
													}
												}
											}
											// Go on to try to assign the next class
											if (conflictFound) {
												break;
											}
										}
									}

									// Create a new node
									Assignment a = new Assignment(result);

									a.assignClass(p.getFirst(), c);
									//System.out.println(a);

									// Make sure the assignList doesn't already contain an identical assignment
									boolean alreadyInList = false;
									a.evalue(); // Update assignment evalValue
									for (Assignment assign : assignList) {
										if (a.getEvalValue() == assign.getEvalValue()) {
											if (a.getUnassignedClasses().equals(assign.getUnassignedClasses())) {
												if (a.equals(assign)) {
													//System.out.println("Identifical assignment found");
													alreadyInList = true;
													break;
												}
											}
										}
									}


									//a.printTotalPens();
									if (!alreadyInList) {
										assignList.add(a);
									}

								}
							} //else { System.out.println("Evening course not in the evening");
							//System.out.println("Course: " + c + " Slot: " + p.getFirst());}
						} //else { System.out.println("Course scheduled during lunch");
								//System.out.println("Course: " + c + " Slot: " + p.getFirst()); }
						} //else { System.out.println("Wrong type");
							//System.out.println("Course: " + c + " Slot: " + p.getFirst()); }
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
						unassignedClasses = getClasses();
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

		for (String l : inputLine) {
			if (!l.equals("")) {
				classItems.add(l);
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
			for (Slot s : courseSlots) {
				if (s.getDay().equals(pairItems[1]) && s.getStartTime().equals(getLocalTime(pairItems[2]))) {
					sl = s;					}
			}
		} else {
			ArrayList<Slot> labSlots = getLabSlots();
			for (Slot s : labSlots) {
				if (s.getDay().equals(pairItems[1]) && s.getStartTime().equals(getLocalTime(pairItems[2]))) {
					sl = s;					}
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

			for(Class c : unassignedClasses) {
				if(c.equals(cl)) {
					cl = c;
				}
			}

			// Iterate through all the slots
			for(Pair<Slot, HashSet<Class>> p : partassign.getAssign()) {
				// See if the Slot's type is the same as the class' type
				if(p.getFirst().getType().equals(cl.getType())) { // If the slot is the same type...
					// If the slot is the same day...
					if(p.getFirst().getDay().equals(pairItems[1].toUpperCase())) {
						// If they share a start time...
						if(p.getFirst().getStartTime().toString().equals((getLocalTime(pairItems[2])).toString()) ) { // If the slot starts at the same time...
							cl.setDept(cl.getDept().toUpperCase()); // Reset the case of the dept

							partassign.assignClass(p.getFirst(), cl); // Assign the class to the slot

							unassignedClasses.remove(cl);
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
		
		for (Slot s : getSlots()) {
			if (s.getType().equals("course")) {
				slots.add(s);
			}
		}
		
		return slots;
	}

	// Assemble all the dedicated lab slots
	private static ArrayList<Slot> getLabSlots() {
		ArrayList<Slot> slots = new ArrayList<>();
		
		for (Slot s : getSlots()) {
			if (s.getType().equals("lab")) {
				slots.add(s);
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

    public static ArrayList<Class> getUnassignedClasses() {
		return unassignedClasses;
	}
}

