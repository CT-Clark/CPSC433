import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExampleTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;


    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

     @Test
    void gehtnicht1_Test() throws IOException {
        String[] args = new String[1];
        args[0] = "resources/test/gehtnicht1.txt";
        Project.main(args);
        assertTrue(outContent.toString().endsWith("No solution was found!\n"));
    }

     @Test
    void gehtnicht2_Test() throws IOException {
        String[] args = new String[1];
        args[0] = "resources/test/gehtnicht2.txt";
        Project.main(args);
        assertTrue(outContent.toString().endsWith("No solution was found!\n"));
    }

     @Test
    void gehtnicht3_Test() throws IOException {
        String[] args = new String[1];
        args[0] = "resources/test/gehtnicht3.txt";
        Project.main(args);
        assertTrue(outContent.toString().endsWith("No solution was found!\n"));
    }

     @Test
    void gehtnicht4_Test() throws IOException {
        String[] args = new String[1];
        args[0] = "resources/test/gehtnicht4.txt";
        Project.main(args);
        assertTrue(outContent.toString().endsWith("No solution was found!\n"));
    }

     @Test
    void gehtnicht5_Test() throws IOException {
        String[] args = new String[1];
        args[0] = "resources/test/gehtnicht5.txt";
        Project.main(args);
        assertTrue(outContent.toString().endsWith("No solution was found!\n"));
    }

     @Test
    void gehtnicht6_Test() throws IOException {
        String[] args = new String[1];
        args[0] = "resources/test/gehtnicht6.txt";
        Project.main(args);
        assertTrue(outContent.toString().endsWith("No solution was found!\n"));
    }

     @Test
    void gehtnicht7_Test() throws IOException {
        String[] args = new String[1];
        args[0] = "resources/test/gehtnicht7.txt";
        Project.main(args);
        assertTrue(outContent.toString().endsWith("No solution was found!\n"));
    }

     @Test
    void gehtnicht8_Test() throws IOException {
        String[] args = new String[1];
        args[0] = "resources/test/gehtnicht8.txt";
        Project.main(args);
        assertTrue(outContent.toString().endsWith("No solution was found!\n"));
    }

     @Test
    void gehtnicht9_Test() throws IOException {
        String[] args = new String[1];
        args[0] = "resources/test/gehtnicht9.txt";
        Project.main(args);
        assertTrue(outContent.toString().endsWith("No solution was found!\n"));
    }

     @Test
    void gehtnicht10_Test() throws IOException {
        String[] args = new String[1];
        args[0] = "resources/test/gehtnicht10.txt";
        Project.main(args);
        assertTrue(outContent.toString().endsWith("No solution was found!\n"));
    }

     @Test
    void gehtnicht11_Test() throws IOException {
        String[] args = new String[1];
        args[0] = "resources/test/gehtnicht11.txt";
        Project.main(args);
        assertTrue(outContent.toString().endsWith("No solution was found!\n"));
    }

     @Test
    void gehtnicht12_Test() throws IOException {
        String[] args = new String[1];
        args[0] = "resources/test/gehtnicht12.txt";
        Project.main(args);
        assertTrue(outContent.toString().endsWith("No solution was found!\n"));
    }

    @Test
    void pairing_Test() throws IOException {
        String[] args = new String[9];
        args[0] = "resources/test/pairing.txt";
        args[1] = "0";
        args[2] = "0";
        args[3] = "11";
        args[4] = "0";
        args[5] = "0";
        args[6] = "0";
        args[7] = "1";
        args[8] = "0";
        Project.main(args);
        assertEquals("Eval-value: 55", outContent.toString().split("\n")[0]);
    }

     @Test
    void prefexamp_Test() throws IOException {
        String[] args = new String[9];
        args[0] = "resources/test/prefexamp.txt";
        args[1] = "100";
        args[2] = "100";
        args[3] = "100";
        args[4] = "0";
        args[5] = "0";
        args[6] = "2";
        args[7] = "0";
        args[8] = "0";
        Project.main(args);
        assertEquals("Eval-value: 60", outContent.toString().split("\n")[0]);
    }
}
