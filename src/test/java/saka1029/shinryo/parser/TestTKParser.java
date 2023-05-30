package saka1029.shinryo.parser;

import java.io.IOException;
import java.io.PrintStream;
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
        String inTxtFile = "data/04tk.txt";
        String outTxtFile = "data/04tk-tree.txt";
        Node root = new TKParser().parse(inTxtFile);
        root.summary(outTxtFile);
    }
    
    static void print(Node node) {
        logger.info(node.level + "  ".repeat(node.level) + node.token.number + " " + node.token.header);
    }

    @Test
    public void test目次() throws IOException {
        String inTxtFile = "data/04tk.txt";
        logger.info("04調剤目次: " + inTxtFile);
        Node root = new TKParser().parse(inTxtFile);
        for (Node node : root.children) {
            print(node);
            if (node.children.stream().anyMatch(e -> e.token.type == TKParser.区分))
                for (Node child : node.children) {
                    print(child);
                    for (Node grandChild : child.children)
                        print(grandChild);
                }
        }
    }
}
