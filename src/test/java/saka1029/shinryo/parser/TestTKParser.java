package saka1029.shinryo.parser;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Logging;

public class TestTKParser {

    static { Logging.init(); } 
    static final Logger logger = Logger.getLogger(TestTKParser.class.getSimpleName());
    
    static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    @Test
    public void testParse() throws IOException {
        String inTxtFile = "data/04-t-kokuji.txt";
        String outTxtFile = "data/04-t-kokuji-parsed.txt";
        Node root = new TKParser().parse(inTxtFile);
        try (PrintWriter w = new PrintWriter(outTxtFile)) {
			root.print(w::println);
        }
    }

}
