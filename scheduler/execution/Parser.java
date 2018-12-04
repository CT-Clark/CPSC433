package execution;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

import structures.Class;
import structures.Course;
import structures.Lab;
import structures.Pair;
import structures.Slot;
import execution.Scheduler;

public class Parser {
	private String path;
	private int initSlotRank;
	private int initClassRank;
	
	private ArrayList<Slot> slots; 
	private LinkedHashSet<Class> classes;
	private TreeMap<Class, List<Class>> overlappingSections;
	private TreeMap<Slot, List<Slot>> overlappingSlots;
	private TreeMap<Class, List<Class>> notCompatible;
	private TreeMap<Class, List<Slot>> unwanted;
	private TreeMap<Class, List<Pair<Slot, Double>>> preferences;
	private TreeMap<Class, List<Class>> pairs;
	private TreeMap<Slot, LinkedHashSet<Class>> partAssign;
	
	public Parser(String path) {
		this.path = path;
		this.initSlotRank = 1;
		this.initClassRank = 1;
		
		this.slots = new ArrayList<>();
		this.classes = new LinkedHashSet<>();
		this.notCompatible = new TreeMap<>();
		this.unwanted = new TreeMap<>();
		this.preferences = new TreeMap<>();
		this.pairs = new TreeMap<>();
		this.partAssign = new TreeMap<>();
	}
	
	public Scheduler parseInput() throws IOException {
		try (Scanner scanner = new Scanner(new File(path))) {
			List<String> inputFile = Files.readAllLines(Paths.get(path));

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
						lastBreakpoint = counter + 1;
						break;
				}
				counter++;
			}		
			
			savePartialAssignment(inputFile, lastBreakpoint, counter);
		}
		
		
		System.out.println("NOPE");
		
		setOverlappingSections(findOverlappingSections());		
		rerankClasses();
		
		setOverlappingSlots(findOverlappingSlots());
		
		return new Scheduler(getSlots(), getClasses(), getOverlappingSections(), getOverlappingSlots(), getNotCompatible(), getUnwanted(), getPreferences(), 
				getPairs(), getPartAssign());
	
	}
	// -------------------------------------------------------------- //
	// ---------------------- RANK METHODS -------------------------- //
	// -------------------------------------------------------------- //
	

	private TreeMap<Class, List<Class>> findOverlappingSections() {
		TreeMap<Class, List<Class>> overlappingSections = new TreeMap<Class, List<Class>>();
		
		for (Class cl1 : getClasses()) {
			List<Class> classSect = new ArrayList<Class>();
			
			for (Class cl2 : getClasses()) {
				// if the two classes share the same dept and id AND they are not the same class
				// AND (if the two classes share the same lecture OR either lecture is -1)
				if (cl2.getDept().equals(cl1.getDept()) && cl2.getId() == cl1.getId() && !cl2.equals(cl1)
						&& (cl2.getLecture() == cl1.getLecture() || cl1.getLecture() == -1 || cl2.getLecture() == -1)) {
					classSect.add(cl2);
				}
			}
			
			if (cl1.getDept().equals("CPSC") && cl1.getId() == 313) {
				for (Class cl2 : getClasses()) {
					if (cl2.getDept().equals("CPSC") && cl2.getId() == 813) {
						classSect.add(cl2);
					}
				}	
			}
			
			if (cl1.getDept().equals("CPSC") && cl1.getId() == 413) {
				for (Class cl2 : getClasses()) {
					if (cl2.getDept().equals("CPSC") && cl2.getId() == 913) {
						classSect.add(cl2);
					}
				}	
			}
			
			if (cl1.getDept().equals("CPSC") && cl1.getId() == 813) {
				for (Class cl2 : getClasses()) {
					if (cl2.getDept().equals("CPSC") && cl2.getId() == 313) {
						classSect.add(cl2);
					}
				}	
			}
			
			if (cl1.getDept().equals("CPSC") && cl1.getId() == 913) {
				for (Class cl2 : getClasses()) {
					if (cl2.getDept().equals("CPSC") && cl2.getId() == 413) {
						classSect.add(cl2);
					}
				}	
			}
			
			overlappingSections.put(cl1, classSect);
		}
		
		return overlappingSections;
	}
	
	private void rerankClasses() {
		int rank = 1;
		
		for (Class c : getClasses()) {
			List<Class> nonComp = getNotCompatible().get(c);
			List<Slot> unWant = getUnwanted().get(c);
			List<Pair<Slot, Double>> pref = getPreferences().get(c);
			List<Class> pair = getPairs().get(c);
	
			boolean what = getNotCompatible().containsKey(c);
			
			rank = nonComp.size() + unWant.size() + pref.size() + pair.size();
			
			List<Class> overSect = getOverlappingSections().get(c);
			
			rank = rank + overSect.size() * 10;
		
			if (c.getId() >= 500) {
				rank = rank * 100;
			}
			
			if (c.getDept().equals("CPSC") && (c.getId() == 813 || c.getId() == 913)) {
				rank = rank * 1000;
			}
			
			c.setRank(rank);
		}
	}
	
	private TreeMap<Slot, List<Slot>> findOverlappingSlots() {
		TreeMap<Slot, List<Slot>> slots = new TreeMap<>();
		
		for (Slot s1 : getSlots()) {
			List<Slot> overlappingSlots = new ArrayList<>();
			
			for (Slot s2 : getSlots()) {
				if (concurrentDays(s1, s2) && concurrentTimes(s1, s2)) {					
					overlappingSlots.add(s2);
				}
			}
			
			slots.put(s1, overlappingSlots);
		}
		
		return slots;
		
	}
	
	private boolean concurrentDays(Slot s1, Slot s2) {
		return s1.getDay().equals(s2.getDay()) || (s1.getDay().equals("MO") && s2.getDay().equals("FR")) || (s1.getDay().equals("FR") && s2.getDay().equals("MO"));
	}
	
	private boolean concurrentTimes(Slot s1, Slot s2) {
		return (s1.getStartTime().equals(s2.getStartTime()) || (s1.getStartTime().isAfter(s2.getStartTime()) && s1.getStartTime().isBefore(s2.getEndTime()))
				|| (s2.getStartTime().isAfter(s1.getStartTime()) && s2.getStartTime().isBefore(s1.getEndTime())));
	}
	
	// -------------------------------------------------------------- //
	// -------------------- PARSER METHODS -------------------------- //
	// -------------------------------------------------------------- //

	// Saves the name of the assignment
	private void saveName(List<String> inputFile, int lastBreakpoint, int counter) {
		for (String line : inputFile.subList(lastBreakpoint, counter)) {
			System.out.println(line);
		}
	}

	// Parses the line and creates a slot object
	private Slot parseSlot(String line, String type) {
		String[] slotItems = line.split(",");
		
		String day = slotItems[0];
		
		LocalTime time = getLocalTime(slotItems[1]);
		int max = Integer.parseInt(slotItems[2].replaceAll("\\s+", ""));
		int min = Integer.parseInt(slotItems[3].replaceAll("\\s+", ""));

		Slot s = new Slot(type, day, time, max, min, initSlotRank);
		initSlotRank++;
		
		return s; 
	}

	// Saves all of the slot objects
	private void saveSlots(List<String> inputFile, int lastBreakpoint, int counter, String type) {
		for (String line : inputFile.subList(lastBreakpoint, counter)) {
			if (line.isEmpty()) {
				continue;
			}
			
			Slot s = parseSlot(line, type);
			
			getSlots().add(s);
			getPartAssign().put(s, new LinkedHashSet<Class>());
			
		}
	}

	// Parses the class objects
	private Class parseClass(String line) {
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
			cl = new Course(dept, id, lecture, initClassRank);
		} else {
			cl = new Lab(dept, id, lecture, tutorial, initClassRank);
		}
		
		initClassRank++;
		return cl;
	}

	// Saves all of the course objects
	private void saveCourses(List<String> inputFile, int lastBreakpoint, int counter) {
		for (String line : inputFile.subList(lastBreakpoint, counter)) {
			if (line.isEmpty()) {
				continue;
			}
			
			Class cl = parseClass(line);
			getClasses().add(cl);
			getNotCompatible().put(cl, new ArrayList<Class>());
			getUnwanted().put(cl, new ArrayList<Slot>());
			getPreferences().put(cl, new ArrayList<Pair<Slot, Double>>());
			getPairs().put(cl, new ArrayList<Class>());
		}
	}

	// Saves all the lab objects
	private void saveLabs(List<String> inputFile, int lastBreakpoint, int counter) {
		for (String line : inputFile.subList(lastBreakpoint, counter)) {
			if (line.isEmpty()) {
				continue;
			}
			
			Class cl = parseClass(line);
			getClasses().add(cl);
			getNotCompatible().put(cl, new ArrayList<Class>());
			getUnwanted().put(cl, new ArrayList<Slot>());
			getPreferences().put(cl, new ArrayList<Pair<Slot, Double>>());
			getPairs().put(cl, new ArrayList<Class>());
		
		}
	}

	// Saves all of the courses and labs which aren't compatible with each other
	private void saveNotCompatible(List<String> inputFile, int lastBreakpoint, int counter) {
		for (String line : inputFile.subList(lastBreakpoint, counter)) {
			if(line.isEmpty()) {
				continue;
			}
	
			Pair<Class, Class> pair = parsePair(line);
			
			if (getNotCompatible().containsKey(pair.getFirst())) {				
				getNotCompatible().get(pair.getFirst()).add(pair.getSecond());
			}
			
			if (getNotCompatible().containsKey(pair.getSecond())) {
				getNotCompatible().get(pair.getSecond()).add(pair.getSecond());
			}
		}
	}

	// Saves all of the courses/labs which should not go in particular slots
	private void saveUnwanted(List<String> inputFile, int lastBreakpoint, int counter) {
		for (String line : inputFile.subList(lastBreakpoint, counter)) {
			if(line.isEmpty()) {
				continue;
			}
			
			Pair<Class, Slot> pair = parseUnwanted(line);
			
			if (getUnwanted().containsKey(pair.getFirst())) {				
				getUnwanted().get(pair.getFirst()).add(pair.getSecond());
			}
		}
	}

	// Parses the unwanted course/lab slot pairings
	private Pair<Class, Slot> parseUnwanted(String line) {
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
	private void savePreferences(List<String> inputFile, int lastBreakpoint, int counter) {
		for (String line : inputFile.subList(lastBreakpoint, counter)) {
			if(line.isEmpty()) {
				continue;
			}
			Pair<Pair<Class, Slot>, Double> pair = parsePreferences(line);
			if (pair.getFirst().getSecond() != null && getPreferences().containsKey(pair.getFirst().getFirst())) {
				getPreferences().get(pair.getFirst().getFirst()).add(new Pair<Slot, Double>(pair.getFirst().getSecond(), pair.getSecond()));
			}
		}
	}

	// Parses the course/lab slot preferences and their respective values
	private Pair<Pair<Class, Slot>, Double> parsePreferences(String line) {
		String[] pairItems = line.split(", ");

		Pair<Class, Slot> p1 = parseUnwanted(pairItems[2] + ", " + pairItems[0] + ", " + pairItems[1]);

		return new Pair<>(p1, Double.parseDouble((pairItems[3])));
	}

	// Saves the pairs of courses/labs that should run at the same time
	private void savePairs(List<String> inputFile, int lastBreakpoint, int counter) {
		for (String line : inputFile.subList(lastBreakpoint, counter)) {
			if(line.isEmpty()) {
				continue;
			}
			
			Pair<Class, Class> pair = parsePair(line);
			
			if (getPairs().containsKey(pair.getFirst())) {				
				getPairs().get(pair.getFirst()).add(pair.getSecond());
			}
			
			if (getPairs().containsKey(pair.getSecond())) {
				getPairs().get(pair.getSecond()).add(pair.getFirst());
			}
		}
	}

	// Parses the pair of courses/labs that should run at the same time
	private Pair<Class, Class> parsePair(String line) {
		String[] pairItems = line.split(", ");
		
		Class cl1 = parseClass(pairItems[0]);
		Class cl2 = parseClass(pairItems[1]);

		return new Pair<>(cl1, cl2);
		
	}

	// Saves the pairs of slots and assigned courses that will make up a partial assignment
	private void savePartialAssignment(List<String> inputFile, int lastBreakpoint, int counter) {
		for (String line : inputFile.subList(lastBreakpoint, counter)) {
			if (line.isEmpty()) {
				continue;
			}	
			
			Pair<Class, Slot> pair = parseUnwanted(line);
			
			if (getPartAssign().containsKey(pair.getSecond())) {
				getPartAssign().get(pair.getSecond()).add(pair.getFirst());
			}
		}
	}
	
	// -------------------------------------------------------------- //
	// -------------------- EXTRA METHODS --------------------------- //
	// -------------------------------------------------------------- //

	// Assemble all of the dedicated course slots
	private ArrayList<Slot> getCourseSlots() {
		ArrayList<Slot> slots = new ArrayList<>();
		
		for (Slot s : getSlots()) {
			if (s.getType().equals("course")) {
				slots.add(s);
			}
		}
		
		return slots;
	}

	// Assemble all the dedicated lab slots
	private ArrayList<Slot> getLabSlots() {
		ArrayList<Slot> slots = new ArrayList<>();
		
		for (Slot s : getSlots()) {
			if (s.getType().equals("lab")) {
				slots.add(s);
			}
		}
		
		return slots;
	}
	
	private LocalTime getLocalTime(String str) {
		String timeString = str.replaceAll("\\s+", "");
		 
		if (timeString.length() == 4) {
			timeString = "0" + timeString;
		}

		return LocalTime.parse(timeString);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getInitSlotRank() {
		return initSlotRank;
	}

	public void setInitSlotRank(int initSlotRank) {
		this.initSlotRank = initSlotRank;
	}

	public int getInitClassRank() {
		return initClassRank;
	}

	public void setInitClassRank(int initClassRank) {
		this.initClassRank = initClassRank;
	}

	public ArrayList<Slot> getSlots() {
		return slots;
	}

	public void setSlots(ArrayList<Slot> slots) {
		this.slots = slots;
	}

	public LinkedHashSet<Class> getClasses() {
		return classes;
	}

	public void setClasses(LinkedHashSet<Class> classes) {
		this.classes = classes;
	}

	public TreeMap<Class, List<Class>> getOverlappingSections() {
		return overlappingSections;
	}

	public void setOverlappingSections(TreeMap<Class, List<Class>> overlappingSections) {
		this.overlappingSections = overlappingSections;
	}

	public TreeMap<Slot, List<Slot>> getOverlappingSlots() {
		return overlappingSlots;
	}

	public void setOverlappingSlots(TreeMap<Slot, List<Slot>> overlappingSlots) {
		this.overlappingSlots = overlappingSlots;
	}

	public TreeMap<Class, List<Class>> getNotCompatible() {
		return notCompatible;
	}

	public void setNotCompatible(TreeMap<Class, List<Class>> notCompatible) {
		this.notCompatible = notCompatible;
	}

	public TreeMap<Class, List<Slot>> getUnwanted() {
		return unwanted;
	}

	public void setUnwanted(TreeMap<Class, List<Slot>> unwanted) {
		this.unwanted = unwanted;
	}

	public TreeMap<Class, List<Pair<Slot, Double>>> getPreferences() {
		return preferences;
	}

	public void setPreferences(TreeMap<Class, List<Pair<Slot, Double>>> preferences) {
		this.preferences = preferences;
	}

	public TreeMap<Class, List<Class>> getPairs() {
		return pairs;
	}

	public void setPairs(TreeMap<Class, List<Class>> pairs) {
		this.pairs = pairs;
	}

	public TreeMap<Slot, LinkedHashSet<Class>> getPartAssign() {
		return partAssign;
	}

	public void setPartAssign(TreeMap<Slot, LinkedHashSet<Class>> partAssign) {
		this.partAssign = partAssign;
	}

	
}
