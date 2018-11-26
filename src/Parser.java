import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Parses the input file

public class Parser {
    private String path;
    private List<Lecture> coursesList; // List of coures
    private List<Lecture> labsList;    // List of labs
    private List<Slot> courseSlots;    // List of slots courses can fit into
    private List<Slot> labSlots;       // List of slots labs can fit into
    private List<Pair> pairs;          // List of courses/labs that should be paired at the same time
    private List<Pair> not_compatible; // List of courses/labs that cannot be scheduled at the same time

    public Parser(String path) {
        // Set info for parser
        this.path = path;
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println("Current relative path is: " + s);
        if (!Files.exists(Paths.get(path)) || !Files.isRegularFile(Paths.get(path))) {
            throw new IllegalArgumentException("Input file not available: " + Paths.get(path) + "!");
        }
    }

    // Parse input and set appropriate fieds
    public SearchControl parseInput() throws IOException {
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
                        saveCourseSlots(inputFile, lastBreakpoint, counter);
                        lastBreakpoint = counter + 1;
                        break;
                    // Generate list of courses
                    case "courses:":
                        saveLabSlots(inputFile, lastBreakpoint, counter);
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
            savePartialAssignments(inputFile, lastBreakpoint, counter - 1);
        }
        List<Lecture> lectures = new ArrayList<>();
        lectures.addAll(coursesList); // Add courses
        lectures.addAll(labsList);    // Add labs
        // Return the data structure that contains all of the info for the parsed items
        return new SearchControl(lectures, courseSlots, labSlots, not_compatible, pairs);
    }

    // Save an initial state to start from
    private void savePartialAssignments(List<String> inputFile, int lastBreakpoint, int i) {
        //TODO: save partial assignment as start state
        for (String line : inputFile.subList(lastBreakpoint, i)) {
            if (line.isEmpty()) {
                continue;
            }

            String[] lectures = line.split(", ");
            if(lectures.length != 2) {
                throw new IllegalStateException();
            }

        }
    }
    
    // Save the items that should be paired
    private void savePairs(List<String> inputFile, int lastBreakpoint, int counter) {
        pairs = savePairedLectures(inputFile, lastBreakpoint, counter);
    }

    // Save the courses/labs that should be placed into specific slots
    private void savePreferences(List<String> inputFile, int lastBreakpoint, int counter) {
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            if(line.isEmpty()) {
                continue;
            }
            String[] preference = line.split(",");

            // Create the course/lab item
            Lecture lec1 = Lecture.produceLecture(preference[2]);
            // Create the slot with a course/lab type
            Slot slot = GeneralSlot.produceSlot(preference[0] + ", " + preference[1], lec1 instanceof Lab ? !GeneralSlot.COURSE : GeneralSlot.COURSE);

            if (lec1 instanceof Lab) {
                Lecture lab = labsList.get(labsList.indexOf(lec1));
                lab.preferedSlots.add(labSlots.get(labSlots.indexOf(slot)));
                lab.preferenceScore = Integer.valueOf(preference[3].replace(" ", "")); // Eval value to use

            } else {
                Lecture lab = coursesList.get(coursesList.indexOf(lec1));
                lab.preferedSlots.add(courseSlots.get(courseSlots.indexOf(slot)));
                lab.preferenceScore = Integer.valueOf(preference[3].replace(" ", "")); // Eval value to use
            }
        }
    }

    // Save the courses/labs that cannot be assigned to a particular slot
    private void saveUnwanted(List<String> inputFile, int lastBreakpoint, int counter) {
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            if(line.isEmpty()) {
                continue;
            }
            String[] unwanted = line.split(",", 2);
            Lecture lec1 = Lecture.produceLecture(unwanted[0]); // Create course/lab object

            // Create the slot
            Slot slot = GeneralSlot.produceSlot(unwanted[1], lec1 instanceof Lab ? !GeneralSlot.COURSE : GeneralSlot.COURSE);
            // ** Is this if-else necessary? ** //
            if(slot.getType() == GeneralSlot.COURSE) {
                courseSlots.get(courseSlots.indexOf(slot)).saveUnwantedLecture(lec1);
            } else {
                courseSlots.get(courseSlots.indexOf(slot)).saveUnwantedLecture(lec1);
            }
        }
    }

    // Save the courses/labs which are not compatible
    private void saveNotCompatible(List<String> inputFile, int lastBreakpoint, int counter) {
        not_compatible = savePairedLectures(inputFile, lastBreakpoint, counter);
    }

    // Save the courses/labs that should occur in the same slot
    private List<Pair> savePairedLectures(List<String> inputFile, int lastBreakpoint, int counter) {
        List<Pair> couples = new ArrayList<>(counter - lastBreakpoint + 1);
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            if(line.isEmpty()) {
                continue;
            }
            String[] notCompatiblePair = line.split(",");
            if(notCompatiblePair.length != 2) {
                throw new IllegalStateException();
            }
            //To be aware: This produces redundant Lecture courses!
            Lecture lec1 = Lecture.produceLecture(notCompatiblePair[0]);
            Lecture lec2 = Lecture.produceLecture(notCompatiblePair[1]);


            Lecture originalLec = coursesList.indexOf(lec1) == -1 ?
                    labsList.get(
                            labsList.indexOf(lec1)) :
                    coursesList.get(
                            coursesList.indexOf(lec1));
            Lecture originalLec2 = coursesList.indexOf(lec2) == -1 ? labsList.get(labsList.indexOf(lec2)) : coursesList.get(coursesList.indexOf(lec2));

            couples.add(new Pair(originalLec, originalLec2));
        }
        return couples;
    }

    // Save the labs
    private void saveLabs(List<String> inputFile, int lastBreakpoint, int counter) {
        labsList = new ArrayList<>(inputFile.size());
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            if (line.isEmpty()) {
                continue;
            }

            labsList.add(Lab.produceLab(line));
        }
    }

    // Save the courses
    private void saveCourses(List<String> inputFile, int lastBreakpoint, int counter) {
        coursesList = new ArrayList<>(inputFile.size());
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            if (line.isEmpty()) {
                continue;
            }
            coursesList.add(Course.produceCourse(line));
        }
    }

    // Save the lab slots
    private void saveLabSlots(List<String> inputFile, int lastBreakpoint, int counter) {
        labSlots = saveSlots(inputFile, lastBreakpoint, counter, !GeneralSlot.COURSE);
    }

    // Save the course slots
    private void saveCourseSlots(List<String> inputFile, int lastBreakpoint, int counter) {
        courseSlots = saveSlots(inputFile, lastBreakpoint, counter, GeneralSlot.COURSE);
    }

    // Method for saving the course/lab slots
    private List<Slot> saveSlots(List<String> inputFile, int lastBreakpoint, int counter, boolean type) {
        List<Slot> slotList = new ArrayList<>(inputFile.size());
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            line = line.replaceAll("\\s", "");
            if (line.isEmpty()) {
                continue;
            }
            slotList.add(GeneralSlot.produceSlot(line, type));
        }
        return slotList;
    }

    // Save the name of the assignment
    private void saveName(List<String> inputFile, int lastBreakpoint, int counter) {
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            System.out.println(line);
        }
    }
}
