import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;


public class DepartmentTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;


    @Disabled
    @BeforeEach
    public void setUpStreams() {
        //System.setOut(new PrintStream(outContent));
        //System.setErr(new PrintStream(errContent));
    }

    @Test
    void deptinst1_Test() throws IOException {
        String[] args = new String[9];
        args[0] = "resources/deptinst1.txt"; //path: path to input .txt file
        args[1] = "1"; //pen_coursemin: integer, 0 if not relevant
        args[2] = "1"; //pen_labmin: integer, 0 if not relevant
        args[3] = "1"; //pen_notpaired: integer, 0 if not relevant
        args[4] = "1"; //pen_section: integer, 0 if not relevant
        args[5] = "1"; //w_minfilled: integer, 0 if not relevant
        args[6] = "1"; //w_pref: integer, 0 if not relevant
        args[7] = "1"; //w_pair: integer, 0 if not relevant
        args[8] = "1"; //w_secdiff: integer, 0 if not relevant
        Project.main(args);
    }

    @Test
    void deptinst2_Test() throws IOException {
        String[] args = new String[9];
        args[0] = "resources/deptinst2.txt"; //path: path to input .txt file
        args[1] = "1"; //pen_coursemin: integer, 0 if not relevant
        args[2] = "1"; //pen_labmin: integer, 0 if not relevant
        args[3] = "1"; //pen_notpaired: integer, 0 if not relevant
        args[4] = "1"; //pen_section: integer, 0 if not relevant
        args[5] = "1"; //w_minfilled: integer, 0 if not relevant
        args[6] = "1"; //w_pref: integer, 0 if not relevant
        args[7] = "1"; //w_pair: integer, 0 if not relevant
        args[8] = "1"; //w_secdiff: integer, 0 if not relevant
        Project.main(args);
    }
}

