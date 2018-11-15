import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Parser {
    private String path;
    private List<Lecture> coursesList;
    private List<Lecture> labsList;

    public Parser(String path) {
        this.path = path;
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println("Current relative path is: " + s);
        if (!Files.exists(Paths.get(path)) || !Files.isRegularFile(Paths.get(path))) {
            throw new IllegalArgumentException("Input file not available: " + Paths.get(path) + "!");
        }
    }

    public SearchControl parseInput() throws IOException {
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
                    case "course slots:":
                        saveName(inputFile, lastBreakpoint, counter);
                        lastBreakpoint = counter + 1;
                        break;
                    case "lab slots:":
                        saveCourseSlots(inputFile, lastBreakpoint, counter);
                        lastBreakpoint = counter + 1;
                        break;
                    case "courses:":
                        saveLabSlots(inputFile, lastBreakpoint, counter);
                        lastBreakpoint = counter + 1;
                        break;
                    case "labs:":
                        saveCourses(inputFile, lastBreakpoint, counter);
                        lastBreakpoint = counter + 1;
                        break;
                    case "not compatible:":
                        saveLabs(inputFile, lastBreakpoint, counter);
                        lastBreakpoint = counter + 1;
                        break;
                    case "unwanted:":
                        saveNotCompatible(inputFile, lastBreakpoint, counter);
                        lastBreakpoint = counter + 1;
                        break;
                    case "preferences:":
                        saveUnwanted(inputFile, lastBreakpoint, counter);
                        lastBreakpoint = counter + 1;
                        break;
                    case "pair:":
                        savePreferences(inputFile, lastBreakpoint, counter);
                        lastBreakpoint = counter + 1;
                        break;
                    case "partial assignments:":
                        savePairs(inputFile, lastBreakpoint, counter);
                        lastBreakpoint = counter + 1;
                        break;
                }
                counter++;
            }
            savePartialAssignments(inputFile, lastBreakpoint, counter - 1);
        }
        return null;
    }

    private void savePartialAssignments(List<String> inputFile, int lastBreakpoint, int i) {
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

    private void savePairs(List<String> inputFile, int lastBreakpoint, int counter) {
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            System.out.println(line);
        }
    }

    private void savePreferences(List<String> inputFile, int lastBreakpoint, int counter) {
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            System.out.println(line);
        }
    }

    private void saveUnwanted(List<String> inputFile, int lastBreakpoint, int counter) {
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            System.out.println(line);
        }
    }

    private void saveNotCompatible(List<String> inputFile, int lastBreakpoint, int counter) {
        List<Pair> not_compatible = new ArrayList<>(counter - lastBreakpoint + 1);
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            if(line.isEmpty()) {
                continue;
            }
            String[] notCompatiblePair = line.split(",");
            if(notCompatiblePair.length != 2) {
                throw new IllegalStateException();
            }
            Lecture lec1 = produceLecture(notCompatiblePair[0]);
            Lecture lec2 = produceLecture(notCompatiblePair[1]);

            Lecture originalLec = coursesList.indexOf(lec1) == -1 ?
                    labsList.get(
                            labsList.indexOf(lec1)) :
                    coursesList.get(
                            coursesList.indexOf(lec1));
            Lecture originalLec2 = coursesList.indexOf(lec2) == -1 ? labsList.get(labsList.indexOf(lec2)) : coursesList.get(coursesList.indexOf(lec2));

            not_compatible.add(new Pair(originalLec, originalLec2));
        }
    }

    private void saveLabs(List<String> inputFile, int lastBreakpoint, int counter) {
        labsList = new ArrayList<>(inputFile.size());
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            if (line.isEmpty()) {
                continue;
            }

            labsList.add(produceLab(line));
        }
    }

    public Lecture produceLecture(String representation) {
        if(representation.contains("TUT")) {
            return produceLab(representation);
        } else {
            return produceCourse(representation);
        }
    }

    public Lab produceLab(String representation) {
        representation = representation.trim();
        String[] courseData = representation.split("\\s");
        if (courseData.length == 4){
            return new Lab(courseData[0], Integer.parseInt(courseData[1]), Integer.parseInt(courseData[3]));
        } else if (courseData.length == 6) {
            return new Lab(courseData[0], Integer.parseInt(courseData[1]), Integer.parseInt(courseData[3]), Integer.parseInt(courseData[5]));
        }
        throw new IllegalStateException();
    }

    private void saveCourses(List<String> inputFile, int lastBreakpoint, int counter) {
        coursesList = new ArrayList<>(inputFile.size());
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            if (line.isEmpty()) {
                continue;
            }
            coursesList.add(produceCourse(line));
        }
    }

    public Course produceCourse(String representation) {
        representation = representation.trim();
        String[] courseData = representation.split("\\s");
        if (courseData.length != 4){
            throw new IllegalStateException("Each course should be defined by four attributes!");
        }
        return new Course(courseData[0], Integer.parseInt(courseData[1]), Integer.parseInt(courseData[3]));
    }

    private void saveLabSlots(List<String> inputFile, int lastBreakpoint, int counter) {
        saveSlots(inputFile, lastBreakpoint, counter, !Slot.COURSE);
    }

    private void saveCourseSlots(List<String> inputFile, int lastBreakpoint, int counter) {
        saveSlots(inputFile, lastBreakpoint, counter, Slot.COURSE);
    }

    private void saveSlots(List<String> inputFile, int lastBreakpoint, int counter, boolean type) {
        List<Slot> slotList = new ArrayList<>(inputFile.size());
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            line = line.replaceAll("\\s", "");
            if (line.isEmpty()) {
                continue;
            }
            String[] slotData = line.split(",");
            if (slotData.length != 4) {
                throw new IllegalStateException("Each slot should be defined by four attributes!");
            }

            slotList.add(new Slot(slotData[0], Integer.parseInt(slotData[1].replaceAll(":", "")),
                    Integer.parseInt(slotData[2]), Integer.parseInt(slotData[3]), type));
        }
    }

    private void saveName(List<String> inputFile, int lastBreakpoint, int counter) {
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            System.out.println(line);
        }
    }
}