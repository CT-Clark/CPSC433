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
    private List<Slot> courseSlots;
    private List<Slot> labSlots;
    private List<Pair> pairs;
    private List<Pair> not_compatible;

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
        List<Lecture> lectures = new ArrayList<>();
        lectures.addAll(coursesList);
        lectures.addAll(labsList);
        return new SearchControl(lectures, courseSlots, labSlots, not_compatible, pairs);
    }

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

    private void savePairs(List<String> inputFile, int lastBreakpoint, int counter) {
        pairs = savePairedLectures(inputFile, lastBreakpoint, counter);
    }

    private void savePreferences(List<String> inputFile, int lastBreakpoint, int counter) {
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            if(line.isEmpty()) {
                continue;
            }
            String[] preference = line.split(",");

            Lecture lec1 = Lecture.produceLecture(preference[2]);
            Slot slot = GeneralSlot.produceSlot(preference[0] + ", " + preference[1], lec1 instanceof Lab ? !GeneralSlot.COURSE : GeneralSlot.COURSE);

            if (lec1 instanceof Lab) {
                Lecture lab = labsList.get(labsList.indexOf(lec1));
                lab.preferedSlots.add(labSlots.get(labSlots.indexOf(slot)));
                lab.preferenceScore = Integer.valueOf(preference[3].replace(" ", ""));

            } else {
                Lecture lab = coursesList.get(coursesList.indexOf(lec1));
                lab.preferedSlots.add(courseSlots.get(courseSlots.indexOf(slot)));
                lab.preferenceScore = Integer.valueOf(preference[3].replace(" ", ""));
            }
        }
    }

    private void saveUnwanted(List<String> inputFile, int lastBreakpoint, int counter) {
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            if(line.isEmpty()) {
                continue;
            }
            String[] unwanted = line.split(",", 2);
            Lecture lec1 = Lecture.produceLecture(unwanted[0]);

            Slot slot = GeneralSlot.produceSlot(unwanted[1], lec1 instanceof Lab ? !GeneralSlot.COURSE : GeneralSlot.COURSE);
            if(slot.getType() == GeneralSlot.COURSE) {
                courseSlots.get(courseSlots.indexOf(slot)).saveUnwantedLecture(lec1);
            } else {
                courseSlots.get(courseSlots.indexOf(slot)).saveUnwantedLecture(lec1);
            }
        }
    }

    private void saveNotCompatible(List<String> inputFile, int lastBreakpoint, int counter) {
        not_compatible = savePairedLectures(inputFile, lastBreakpoint, counter);
    }

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

    private void saveLabs(List<String> inputFile, int lastBreakpoint, int counter) {
        labsList = new ArrayList<>(inputFile.size());
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            if (line.isEmpty()) {
                continue;
            }

            labsList.add(Lab.produceLab(line));
        }
    }

    private void saveCourses(List<String> inputFile, int lastBreakpoint, int counter) {
        coursesList = new ArrayList<>(inputFile.size());
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            if (line.isEmpty()) {
                continue;
            }
            coursesList.add(Course.produceCourse(line));
        }
    }

    private void saveLabSlots(List<String> inputFile, int lastBreakpoint, int counter) {
        labSlots = saveSlots(inputFile, lastBreakpoint, counter, !GeneralSlot.COURSE);
    }

    private void saveCourseSlots(List<String> inputFile, int lastBreakpoint, int counter) {
        courseSlots = saveSlots(inputFile, lastBreakpoint, counter, GeneralSlot.COURSE);
    }

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

    private void saveName(List<String> inputFile, int lastBreakpoint, int counter) {
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            System.out.println(line);
        }
    }
}