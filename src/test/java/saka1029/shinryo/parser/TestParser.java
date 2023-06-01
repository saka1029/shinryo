package saka1029.shinryo.parser;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Logging;

public class TestParser {

    static { Logging.init(); } 
    static final Logger logger = Logger.getLogger(TestParser.class.getSimpleName());
    
    static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    @Test
    public void testTKParser() throws IOException {
        String inTxtFile = "in/04/t/txt/ke.txt";
        String outTxtFile = "data/out/04/t/k-tree.txt";
        Node root = new TKParser().parse(inTxtFile);
        root.summary(outTxtFile);
    }

    @Test
    public void testTTParser() throws IOException {
        String inTxtFile = "in/04/t/txt/te.txt";
        String outTxtFile = "data/out/04/t/t-tree.txt";
        Node root = new TTParser().parse(inTxtFile);
        root.summary(outTxtFile);
    }

    @Test
    public void testIKParser() throws IOException {
        String inTxtFile = "in/04/i/txt/ke.txt";
        String outTxtFile = "data/out/04/i/k-tree.txt";
        Node root = new IKParser().parse(inTxtFile);
        root.summary(outTxtFile);
    }
    
    static void print(Node node) {
        logger.info(node.level + "  ".repeat(node.level) + node.token.number + " " + node.token.header);
    }

    @Test
    public void test目次() throws IOException {
        String inTxtFile = "in/04/t/txt/ke.txt";
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
