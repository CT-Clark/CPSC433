import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTest {
    private Parser parser;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void not_compatible_Test() throws IOException {
        parser = new Parser("resources/Not_compatible_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();
        Stream<String> sol = Files.lines(Paths.get("resources/Not_compatible_solution.txt"));
        StringBuffer singleSol = new StringBuffer();
        sol.map(s -> new String(s + "\n"))
                .forEach(n -> singleSol.append(n));
        String foundSol = best.getPrintableSolution();
        assertEquals(singleSol.toString(), best.getPrintableSolution());
    }

    @Test
    void unwanted_Test() throws IOException {
        parser = new Parser("resources/Unwanted_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();
        Stream<String> sol = Files.lines(Paths.get("resources/Unwanted_solution.txt"));
        StringBuffer singleSol = new StringBuffer();
        sol.map(s -> new String(s + "\n"))
                .forEach(n -> singleSol.append(n));
        String foundSol = best.getPrintableSolution();
        assertEquals(singleSol.toString(), best.getPrintableSolution());
    }

    @Test
    void coursemax_Test() throws IOException {
        parser = new Parser("resources/Coursemax_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();
        assertNull(best);
    }

    @Test
    void labmax_Test() throws IOException {
        parser = new Parser("resources/Labmax_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();
        assertNull(best);
    }

    @Test
    void labsNotOnCourseSlot_Test() throws IOException {
        parser = new Parser("resources/LabsNotOnCourseSlot_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();
        Stream<String> sol = Files.lines(Paths.get("resources/LabsNotOnCourseSlot_solution.txt"));
        StringBuffer singleSol = new StringBuffer();
        sol.map(s -> new String(s + "\n"))
                .forEach(n -> singleSol.append(n));
        assertEquals(singleSol.toString(), best.getPrintableSolution());
    }

    @Test
    void LEC9_EveningSlots_Test() throws IOException {
        parser = new Parser("resources/LEC9_EveningSlots_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();
        Stream<String> sol = Files.lines(Paths.get("resources/LEC9_EveningSlots_solution1.txt"));
        StringBuffer singleSol = new StringBuffer();
        sol.map(s -> new String(s + "\n"))
                .forEach(n -> singleSol.append(n));

        Stream<String> sol2 = Files.lines(Paths.get("resources/LEC9_EveningSlots_solution2.txt"));
        StringBuffer singleSol2 = new StringBuffer();
        sol2.map(s -> new String(s + "\n"))
                .forEach(n -> singleSol2.append(n));

        String foundSol = best.getPrintableSolution();
        assertTrue(singleSol.toString().equals(best.getPrintableSolution()) || singleSol2.toString().equals(best.getPrintableSolution()));
    }

    @Test
    void LEC9_NoEveningSlots_Test() throws IOException {
        parser = new Parser("resources/LEC9_NoEveningSlots_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();
        assertNull(best);
    }

    @Test
    void Level500CoursesDifferentSlots_Test() throws IOException {
        parser = new Parser("resources/Level500CoursesDifferentSlots_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();
        Stream<String> sol = Files.lines(Paths.get("resources/Level500CoursesDifferentSlots_solution1.txt"));
        StringBuffer singleSol = new StringBuffer();
        sol.map(s -> new String(s + "\n"))
                .forEach(n -> singleSol.append(n));

        Stream<String> sol2 = Files.lines(Paths.get("resources/Level500CoursesDifferentSlots_solution2.txt"));
        StringBuffer singleSol2 = new StringBuffer();
        sol2.map(s -> new String(s + "\n"))
                .forEach(n -> singleSol2.append(n));

        String foundSol = best.getPrintableSolution();
        assertTrue(singleSol.toString().equals(best.getPrintableSolution()) || singleSol2.toString().equals(best.getPrintableSolution()));
    }

    @Test
    void CPSC_Course_OnlyForbiddenTU_Slot_Test() throws IOException {
        parser = new Parser("resources/CPSC_Course_OnlyForbiddenTU_Slot_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();
        assertNull(best);
    }

    @Test
    void CPSC_Course_ForbiddenTU_Slot_Test() throws IOException {
        parser = new Parser("resources/CPSC_Course_ForbiddenTU_Slot_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();
        Stream<String> sol = Files.lines(Paths.get("resources/CPSC_Course_ForbiddenTU_Slot_solution.txt"));
        StringBuffer singleSol = new StringBuffer();
        sol.map(s -> new String(s + "\n"))
                .forEach(n -> singleSol.append(n));
        assertEquals(singleSol.toString(), best.getPrintableSolution());
    }

    @Test
    void CPSC813_913_Test() throws IOException {
        parser = new Parser("resources/CPSC813_913_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();
        Stream<String> sol = Files.lines(Paths.get("resources/CPSC813_913_solution1.txt"));
        StringBuffer singleSol = new StringBuffer();
        sol.map(s -> new String(s + "\n"))
                .forEach(n -> singleSol.append(n));

        Stream<String> sol2 = Files.lines(Paths.get("resources/CPSC813_913_solution2.txt"));
        StringBuffer singleSol2 = new StringBuffer();
        sol2.map(s -> new String(s + "\n"))
                .forEach(n -> singleSol2.append(n));

        String foundSol = best.getPrintableSolution();
        assertTrue(singleSol.toString().equals(best.getPrintableSolution()) || singleSol2.toString().equals(best.getPrintableSolution()));
    }
}