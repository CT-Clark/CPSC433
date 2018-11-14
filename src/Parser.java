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
            System.out.println(line);
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
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            System.out.println(line);
        }
    }

    private void saveLabs(List<String> inputFile, int lastBreakpoint, int counter) {
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            System.out.println(line);
        }
    }

    private void saveCourses(List<String> inputFile, int lastBreakpoint, int counter) {
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            System.out.println(line);
        }
    }

    private void saveLabSlots(List<String> inputFile, int lastBreakpoint, int counter) {
        saveSlots(inputFile, lastBreakpoint, counter, !Slot.COURSE);
    }

    private void saveCourseSlots(List<String> inputFile, int lastBreakpoint, int counter) {
        saveSlots(inputFile, lastBreakpoint, counter, Slot.COURSE);
    }

    private void saveSlots(List<String> inputFile, int lastBreakpoint, int counter, boolean type) {
        List<Slot> courseList = new ArrayList<>(inputFile.size());
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            line = line.replaceAll("\\s", "");
            if (line.isEmpty()) {
                continue;
            }
            String[] slotData = line.split(",");
            if (slotData.length != 4) {
                throw new IllegalStateException("Each slot should be defined by four attributes!");
            }

            courseList.add(new Slot(slotData[0], Integer.parseInt(slotData[1].replaceAll(":", "")),
                    Integer.parseInt(slotData[2]), Integer.parseInt(slotData[3]), type));
        }
    }

    private void saveName(List<String> inputFile, int lastBreakpoint, int counter) {
        for (String line : inputFile.subList(lastBreakpoint, counter)) {
            System.out.println(line);
        }
    }
}