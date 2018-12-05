import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

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
        parser = new Parser("resources/hard_constraints/Not_compatible_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();
        Stream<String> sol = Files.lines(Paths.get("resources/hard_constraints/Not_compatible_solution.txt"));
        StringBuffer singleSol = new StringBuffer();
        sol.map(s -> new String(s + "\n"))
                .forEach(n -> singleSol.append(n));
        String foundSol = best.getPrintableSolution();
        assertEquals(singleSol.toString(), best.getPrintableSolution());
    }


    @Test
    void unwanted_Test() throws IOException {
        parser = new Parser("resources/hard_constraints/Unwanted_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();
        Stream<String> sol = Files.lines(Paths.get("resources/hard_constraints/Unwanted_solution.txt"));
        StringBuffer singleSol = new StringBuffer();
        sol.map(s -> new String(s + "\n"))
                .forEach(n -> singleSol.append(n));
        String foundSol = best.getPrintableSolution();
        assertEquals(singleSol.toString(), best.getPrintableSolution());
    }

     @Test
    void coursemax_Test() throws IOException {
        parser = new Parser("resources/hard_constraints/Coursemax_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();
        assertNull(best);
    }

     @Test
    void labmax_Test() throws IOException {
        parser = new Parser("resources/hard_constraints/Labmax_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();
        assertNull(best);
    }

     @Test
    void labsNotOnCourseSlot_Test() throws IOException {
        parser = new Parser("resources/hard_constraints/LabsNotOnCourseSlot_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();
        Stream<String> sol = Files.lines(Paths.get("resources/hard_constraints/LabsNotOnCourseSlot_solution.txt"));
        StringBuffer singleSol = new StringBuffer();
        sol.map(s -> new String(s + "\n"))
                .forEach(n -> singleSol.append(n));
        assertEquals(singleSol.toString(), best.getPrintableSolution());
    }

     @Test
    void LEC9_EveningSlots_Test() throws IOException {
        parser = new Parser("resources/hard_constraints/LEC9_EveningSlots_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();
        Stream<String> sol = Files.lines(Paths.get("resources/hard_constraints/LEC9_EveningSlots_solution1.txt"));
        StringBuffer singleSol = new StringBuffer();
        sol.map(s -> new String(s + "\n"))
                .forEach(n -> singleSol.append(n));

        Stream<String> sol2 = Files.lines(Paths.get("resources/hard_constraints/LEC9_EveningSlots_solution2.txt"));
        StringBuffer singleSol2 = new StringBuffer();
        sol2.map(s -> new String(s + "\n"))
                .forEach(n -> singleSol2.append(n));

        String foundSol = best.getPrintableSolution();
        assertTrue(singleSol.toString().equals(best.getPrintableSolution()) || singleSol2.toString().equals(best.getPrintableSolution()));
    }

     @Test
    void LEC9_NoEveningSlots_Test() throws IOException {
        parser = new Parser("resources/hard_constraints/LEC9_NoEveningSlots_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();
        assertNull(best);
    }

     @Test
    void Level500CoursesDifferentSlots_Test() throws IOException {
        parser = new Parser("resources/hard_constraints/Level500CoursesDifferentSlots_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();
        Stream<String> sol = Files.lines(Paths.get("resources/hard_constraints/Level500CoursesDifferentSlots_solution1.txt"));
        StringBuffer singleSol = new StringBuffer();
        sol.map(s -> new String(s + "\n"))
                .forEach(n -> singleSol.append(n));

        Stream<String> sol2 = Files.lines(Paths.get("resources/hard_constraints/Level500CoursesDifferentSlots_solution2.txt"));
        StringBuffer singleSol2 = new StringBuffer();
        sol2.map(s -> new String(s + "\n"))
                .forEach(n -> singleSol2.append(n));

        String foundSol = best.getPrintableSolution();
        assertTrue(singleSol.toString().equals(best.getPrintableSolution()) || singleSol2.toString().equals(best.getPrintableSolution()));
    }

     @Test
    void CPSC_Course_OnlyForbiddenTU_Slot_Test() throws IOException {
        parser = new Parser("resources/hard_constraints/CPSC_Course_OnlyForbiddenTU_Slot_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();
        assertNull(best);
    }

     @Test
    void CPSC_Course_ForbiddenTU_Slot_Test() throws IOException {
        parser = new Parser("resources/hard_constraints/CPSC_Course_ForbiddenTU_Slot_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();
        Stream<String> sol = Files.lines(Paths.get("resources/hard_constraints/CPSC_Course_ForbiddenTU_Slot_solution.txt"));
        StringBuffer singleSol = new StringBuffer();
        sol.map(s -> new String(s + "\n"))
                .forEach(n -> singleSol.append(n));
        assertEquals(singleSol.toString(), best.getPrintableSolution());
    }

     @Disabled @Test
    void CPSC813_913_Test() throws IOException {
        parser = new Parser("resources/hard_constraints/CPSC813_913_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();
        Stream<String> sol = Files.lines(Paths.get("resources/hard_constraints/CPSC813_913_solution1.txt"));
        StringBuffer singleSol = new StringBuffer();
        sol.map(s -> new String(s + "\n"))
                .forEach(n -> singleSol.append(n));

        Stream<String> sol2 = Files.lines(Paths.get("resources/hard_constraints/CPSC813_913_solution2.txt"));
        StringBuffer singleSol2 = new StringBuffer();
        sol2.map(s -> new String(s + "\n"))
                .forEach(n -> singleSol2.append(n));

        String foundSol = best.getPrintableSolution();
        assertTrue(singleSol.toString().equals(best.getPrintableSolution()) || singleSol2.toString().equals(best.getPrintableSolution()));
    }

     @Test
    void example1_Test() throws IOException {
        parser = new Parser("resources/mixed_examples/Example1.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();

        assertNotNull(best);
        System.out.println("Example 1:\n");
        System.out.println(best.getPrintableSolution());
    }

     @Test
    void example2_Test() throws IOException {
        parser = new Parser("resources/mixed_examples/Example2.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();

        assertNotNull(best);
        System.out.println("Example 2:\n");
        System.out.println(best.getPrintableSolution());
    }

     @Test
    void example3_Test() throws IOException {
        parser = new Parser("resources/mixed_examples/Example3.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();

        assertNotNull(best);
        System.out.println("Example 3:\n");
        System.out.println(best.getPrintableSolution());
    }

     @Test
    void example4_Test() throws IOException {
        parser = new Parser("resources/mixed_examples/Example4.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();

        assertNotNull(best);
        System.out.println("Example 4:\n");
        System.out.println(best.getPrintableSolution());
    }

    @Test
    void example5_Test() throws IOException {
        parser = new Parser("resources/mixed_examples/Example5.txt");
        assertThrows(NullPointerException.class, () -> {
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();
        System.out.println(best.getPrintableSolution());
        });
    }

     @Test
    void example6_Test() throws IOException {
        parser = new Parser("resources/mixed_examples/Example6.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();

        assertNull(best);
    }

     @Test
    void example7_Test() throws IOException {
        parser = new Parser("resources/mixed_examples/Example7.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();

        assertNull(best);
    }

     @Test
    void coursemin_Test() throws IOException {
        parser = new Parser("resources/soft_constraints/Coursemin_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();

        assertEquals(Project.pen_coursemin, best.evalValue);
    }

     @Test
    void labsmin_Test() throws IOException {
        parser = new Parser("resources/soft_constraints/Labsmin_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();

        System.out.println(best.getPrintableSolution());

        assertEquals(2*Project.pen_labsmin, best.evalValue);
    }

     @Test
    void preferences_Test() throws IOException {
        parser = new Parser("resources/soft_constraints/Preferences_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();

        System.out.println(best.getPrintableSolution());

        assertEquals(175, best.evalValue);
    }

     @Test
    void notPaired_Test() throws IOException {
        parser = new Parser("resources/soft_constraints/NotPaired_test.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();

        System.out.println(best.getPrintableSolution());

        assertEquals(Project.pen_notpaired, best.evalValue);
    }

     @Test
    void CPSC_Course_Section_Overlap_Test() throws IOException {
        parser = new Parser("resources/soft_constraints/CPSC_Course_Section_Overlap.txt");
        SearchControl sc = parser.parseInput();
        Assignment best = sc.searchOptimum();

        System.out.println(best.getPrintableSolution());

        assertEquals(Project.pen_section, best.evalValue);
    }
}