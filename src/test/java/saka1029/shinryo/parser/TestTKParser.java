package saka1029.shinryo.parser;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Logging;

public class TestTKParser {

    static { Logging.init(); } 
    static final Logger logger = Logger.getLogger(TestTKParser.class.getSimpleName());

    static void print(Node parent, int level) {
        logger.info("  ".repeat(level) + parent.token());
        for (Node child : parent.children())
            print(child, level + 1);
    }

    @Test
    public void testParse() throws IOException {
        String inTxtFile = "data/04-t-kokuji.txt";
        List<Token> tokens = TokenReader.read(TKParser.TYPES, inTxtFile);
        Node root = new TKParser().parse("令和4年調剤告示", tokens);
        print(root, 0);
    }

}
