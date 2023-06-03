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
    
    static final String IN_DIR = "in/04/";
    static final String OUT_DIR = "debug/out/04/";

    @Test
    public void test調剤告示読み込み() throws IOException {
        String inTxtFile = IN_DIR + "t/txt/ke.txt";
        String outTxtFile = OUT_DIR + "t/k-tree.txt";
        Node root = new 調剤告示読み込み().parse(inTxtFile);
        root.summary(outTxtFile);
    }

    @Test
    public void test調剤通知読み込み() throws IOException {
        String inTxtFile = IN_DIR + "t/txt/te.txt";
        String outTxtFile = OUT_DIR + "t/t-tree.txt";
        Node root = new 調剤通知読み込み().parse(inTxtFile);
        root.summary(outTxtFile);
    }

    @Test
    public void test医科告示読み込み() throws IOException {
        String inTxtFile = IN_DIR + "i/txt/ke.txt";
        String outTxtFile = OUT_DIR + "i/k-tree.txt";
        Node root = new 医科告示読み込み().parse(inTxtFile);
        root.summary(outTxtFile);
    }

    @Test
    public void test医科通知読み込み() throws IOException {
        String inTxtFile = IN_DIR + "i/txt/te.txt";
        String outTxtFile = OUT_DIR + "i/t-tree.txt";
        Node root = new 医科通知読み込み().parse(inTxtFile);
        root.summary(outTxtFile);
    }
}
