package saka1029.shinryo.parser;

import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Logging;

public class TestTKParser {

    static { Logging.init(); } 
    static final Logger logger = Logger.getLogger(TestTKParser.class.getSimpleName());

    static void print(Node parent, int level) {
        if (parent.token() == null)
            logger.info("ROOT");
        else
            logger.info("  ".repeat(level) + parent.token());
        for (Node child : parent.children())
            print(child, level + 1);
    }

    @Test
    public void testParse() throws IOException {
        String inTxtFile = "data/04-t-kokuji.txt";
        Node root = new TKParser().parse(inTxtFile);
        print(root, 0);
    }

}
