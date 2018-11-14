import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTest {
    private Parser parser;

    @BeforeEach
    void setUp() {
        parser = new Parser("resources/input1.txt");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void parseInputTest() throws IOException {
        parser.parseInput();
        assertTrue(true);
    }
}