package saka1029.shinryo.parser;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Logging;

public class TestTTParser {

    static { Logging.init(); } 
    static final Logger logger = Logger.getLogger(TestTTParser.class.getSimpleName());
    
    static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    @Test
    public void testParse() throws IOException {
        String inTxtFile = "data/04tt.txt";
        String outTxtFile = "data/04tt-tree.txt";
        Node root = new TTParser().parse(inTxtFile);
        root.summary(outTxtFile);
    }
    
    @Test
    public void testTKNodeStream() throws IOException {
        String inTxtFile = "data/04tk.txt";
        logger.info("04調剤告示: " + inTxtFile);
        Node root = new TKParser().parse(inTxtFile);
        root.stream()
            .filter(e -> e.node.token != null)
            .filter(e -> e.node.token.type == TKParser.区分番号)
            .forEach(e -> logger.info(e.level + " " + e.node.token.number + " " + e.node.token.header));
    }
    
    @Test
    public void testTTNodeStream() throws IOException {
        String inTxtFile = "data/04tt.txt";
        logger.info("04調剤通知: " + inTxtFile);
        Node root = new TTParser().parse(inTxtFile);
        root.stream()
            .filter(e -> e.node.token != null)
            .filter(e -> e.node.token.type == TTParser.区分番号)
            .forEach(e -> logger.info(e.level + " " + e.node.token.number + " " + e.node.token.header));
    }

}
