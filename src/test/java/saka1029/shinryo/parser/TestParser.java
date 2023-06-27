package saka1029.shinryo.parser;

import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.Param;

public class TestParser {

    static final Logger logger = Common.logger(TestParser.class);
    
    static final Param param = Param.of("in", "debug/out", "02");

//    @Test
    public void test調剤告示読込() throws IOException {
        logger.info(Common.methodName());
        String inTxtFile = param.txt("t", "ke");
        String outTxtFile = param.outDir("tk-tree.txt");
        Node root = new 調剤告示読込().parse(inTxtFile);
        root.summary(outTxtFile);
    }

//    @Test
    public void test調剤通知読込() throws IOException {
        logger.info(Common.methodName());
        String inTxtFile = param.txt("t", "te");
        String outTxtFile = param.outDir("tt-tree.txt");
        Node root = new 調剤通知読込().parse(inTxtFile);
        root.summary(outTxtFile);
    }

//    @Test
    public void test医科告示読込() throws IOException {
        logger.info(Common.methodName());
        String inTxtFile = param.txt("i", "ke");
        String outTxtFile = param.outDir("ik-tree.txt");
        Node root = new 医科告示読込().parse(inTxtFile);
        root.summary(outTxtFile);
    }

//    @Test
    public void test医科通知読込() throws IOException {
        logger.info(Common.methodName());
        String inTxtFile = param.txt("i", "te");
        String outTxtFile = param.outDir("it-tree.txt");
        Node root = new 医科通知読込().parse(inTxtFile);
        root.summary(outTxtFile);
    }

//    @Test
    public void test歯科告示読込() throws IOException {
        logger.info(Common.methodName());
        String inTxtFile = param.txt("s", "ke");
        String outTxtFile = param.outDir("sk-tree.txt");
        Node root = new 歯科告示読込().parse(inTxtFile);
        root.summary(outTxtFile);
    }

//    @Test
    public void test歯科通知読込() throws IOException {
        logger.info(Common.methodName());
        String inTxtFile = param.txt("s", "te");
        String outTxtFile = param.outDir("st-tree.txt");
        Node root = new 歯科通知読込().parse(inTxtFile);
        root.summary(outTxtFile);
    }

//    @Test
    public void test施設基準告示読込() throws IOException {
        logger.info(Common.methodName());
        String inTxtFile = param.txt("k", "ke");
        String outTxtFile = param.outDir("kk-tree.txt");
        Node root = new 施設基準告示読込().parse(inTxtFile);
        root.summary(outTxtFile);
    }

    @Test
    public void test施設基準通知読込() throws IOException {
        logger.info(Common.methodName());
        String inTxtFile = param.txt("k", "te");
        String outTxtFile = param.outDir("kt-tree.txt");
        Node root = new 施設基準通知読込().parse(inTxtFile);
        root.summary(outTxtFile);
    }
}
