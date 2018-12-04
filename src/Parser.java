import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Parser {
    private String path;
    private HashMap<String, Lecture> courseMap;
    private HashMap<String, Lecture> labsMap;
    private HashMap<String, Slot> courseSlots;
    private HashMap<String, Slot> labSlots;
    private Assignment s0;
    //private List<Pair> pairs;
    //private List<Pair> not_compatible;

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
            s0 = savePartialAssignments(inputFile, lastBreakpoint, counter);
        }
        List<Lecture> lectures = new ArrayList<>();
        lectures.addAll(courseMap.values());
        lectures.addAll(labsMap.values());
        return new SearchControl(lectures, courseSlots, labSlots, s0);
    }

    private Assignment savePartialAssignments(List<String> inputFile, int lastBreakpoint, int i) {
        //TODO: save partial assignment as start state
        Assignment partialAssign = new Assignment(courseSlots.values(), labSlots.values());
        for (String line : inputFile.subList(lastBreakpoint, i)) {
            if (line.isEmpty()) {
                continue;
            }

            String[] lectures = line.split(", ", 2);
            if(lectures.length != 2) {
                throw new IllegalStateException();
            }

            Lecture lec1 = labsMap.get(lectures[0].trim());
            Slot slot = labSlots.get(lectures[1].replace(" ", "").replace(":", "").trim() + "," + !GeneralSlot.COURSE);
            if(lec1 == null) {
                lec1 = courseMap.get(lectures[0].trim());
                String id = lectures[1].replace(" ", "").replace(":", "").trim() + "," + GeneralSlot.COURSE;
                slot = courseSlots.get(id);
            }

            partialAssign = partialAssign.assignLecture(lec1, slot);
            partialAssign.unassignedLectures.add(lec1);
            if(partialAssign == null) {
                throw new IllegalArgumentException("Partial assignments break hard constraints!");
            }
        }
        return partialAssign;
    }

    private void savePairs(List<String> inputFile, int lastBreakpoint, int counter) {
        savePairedLectures(inputFile, lastBreakpoint, counter, true);
    }

    private void savePreferences(List<String> inputFile, int lastBreakpoint, int counter) {
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            if(line.isEmpty()) {
                continue;
            }
            String[] preference = line.split(",", 4);

            Lecture lec1 = labsMap.get(preference[2].trim());
            Slot slot = labSlots.get((preference[0] + "," + preference[1].trim().replace(":", "")).trim() + "," + !GeneralSlot.COURSE);
            if(lec1 == null) {
                String id = (preference[0] + "," + preference[1].trim().replace(":", "")).trim() + "," + GeneralSlot.COURSE;
                lec1 = courseMap.get(preference[2].trim());
                slot = courseSlots.get(id);
            }
            if(lec1 != null && slot != null){
            lec1.preferedSlots.add(slot);
            lec1.preferenceScore = Integer.valueOf(preference[3].trim());
            }  else {
                System.err.println("Preferences list links to not existing slot or course:\n" + line);
            }

            /*Lecture lec1 = Lecture.produceLecture(preference[2]);
            Slot slot = GeneralSlot.produceSlot(preference[0] + ", " + preference[1], lec1 instanceof Lab ? !GeneralSlot.COURSE : GeneralSlot.COURSE);

            if (lec1 instanceof Lab) {
                Lecture lab = labsMap.get(labsMap.indexOf(lec1));
                lab.preferedSlots.add(labSlots.get(labSlots.indexOf(slot)));
                lab.preferenceScore = Integer.valueOf(preference[3].replace(" ", ""));

            } else {
                Lecture lab = courseMap.get(courseMap.indexOf(lec1));
                lab.preferedSlots.add(courseSlots.get(courseSlots.indexOf(slot)));
                lab.preferenceScore = Integer.valueOf(preference[3].replace(" ", ""));
            }*/
        }
    }

    private void saveUnwanted(List<String> inputFile, int lastBreakpoint, int counter) {
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            if(line.isEmpty()) {
                continue;
            }
            String[] unwanted = line.split(",", 2);

            Lecture lec1 = labsMap.get(unwanted[0].trim());
            Slot slot = labSlots.get(unwanted[1].trim().replace(" ", "").replace(":", "") + "," + !GeneralSlot.COURSE);
            if(lec1 == null) {
                lec1 = courseMap.get(unwanted[0].trim());
                slot = courseSlots.get(unwanted[1].trim().replace(" ", "").replace(":", "") + "," + GeneralSlot.COURSE);
            }

            lec1.addUnwanted(slot);
        }
    }

    private void saveNotCompatible(List<String> inputFile, int lastBreakpoint, int counter) {
        //not_compatible = savePairedLectures(inputFile, lastBreakpoint, counter);
        savePairedLectures(inputFile, lastBreakpoint, counter, false);
    }

    private List<Pair> savePairedLectures(List<String> inputFile, int lastBreakpoint, int counter, boolean pair) {
        List<Pair> couples = new ArrayList<>(counter - lastBreakpoint + 1);
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            line = line.replaceAll(" +", " ");
            if(line.isEmpty()) {
                continue;
            }
            String[] notCompatiblePair = line.split(",");
            if(notCompatiblePair.length != 2) {
                throw new IllegalStateException();
            }

            Lecture lec1 = labsMap.get(notCompatiblePair[0].trim());
            if(lec1 == null) {
                lec1 = courseMap.get(notCompatiblePair[0].trim());
            }

            Lecture lec2 = labsMap.get(notCompatiblePair[1].trim());
            if(lec2 == null) {
                lec2 = courseMap.get(notCompatiblePair[1].trim());
            }

            if(pair) {
                lec1.addPair(lec2);
                lec2.addPair(lec1);
            } else {
                lec1.addNotCompatible(lec2);
                lec2.addNotCompatible(lec1);
            }



            //couples.add(new Pair(lec1, lec2));
            //To be aware: This produces redundant Lecture courses!
            /*Lecture lec1 = Lecture.produceLecture(notCompatiblePair[0]);
            Lecture lec2 = Lecture.produceLecture(notCompatiblePair[1]);


            Lecture originalLec = courseMap.indexOf(lec1) == -1 ?
                    labsMap.get(
                            labsMap.indexOf(lec1)) :
                    courseMap.get(
                            courseMap.indexOf(lec1));
            Lecture originalLec2 = courseMap.indexOf(lec2) == -1 ? labsMap.get(labsMap.indexOf(lec2)) : courseMap.get(courseMap.indexOf(lec2));

            couples.add(new Pair(originalLec, originalLec2));*/
        }
        return couples;
    }

    private void saveLabs(List<String> inputFile, int lastBreakpoint, int counter) {
        labsMap = new HashMap<>(inputFile.size());
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            line = line.trim();
            line = line.replaceAll(" +", " ");
            if (line.isEmpty()) {
                continue;
            }
            labsMap.put(line, Lab.produceLab(line));
        }
    }

    private void saveCourses(List<String> inputFile, int lastBreakpoint, int counter) {
        //courseMap = new ArrayList<>(inputFile.size());
        courseMap = new HashMap<>(inputFile.size());
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            line = line.trim();
            line = line.replaceAll(" +", " ");
            if (line.isEmpty()) {
                continue;
            }
            courseMap.put(line, Course.produceCourse(line));
        }
    }

    private void saveLabSlots(List<String> inputFile, int lastBreakpoint, int counter) {
        labSlots = saveSlots(inputFile, lastBreakpoint, counter, !GeneralSlot.COURSE);
    }

    private void saveCourseSlots(List<String> inputFile, int lastBreakpoint, int counter) {
        courseSlots = saveSlots(inputFile, lastBreakpoint, counter, GeneralSlot.COURSE);
    }

    private HashMap<String, Slot> saveSlots(List<String> inputFile, int lastBreakpoint, int counter, boolean type) {
        HashMap<String, Slot> slotMap = new HashMap<>(inputFile.size());

        //List<Slot> slotList = new ArrayList<>(inputFile.size());
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            line = line.replaceAll("\\s", "");
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            Slot newSlot = GeneralSlot.produceSlot(line, type);
            slotMap.put(newSlot.getDay() + "," + newSlot.getStartTime() + "," + type, newSlot);
            //slotList.add();
        }
        return slotMap;
    }

    private void saveName(List<String> inputFile, int lastBreakpoint, int counter) {
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            System.out.println(line);
        }
    }
}