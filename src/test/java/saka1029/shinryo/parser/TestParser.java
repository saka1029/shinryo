package saka1029.shinryo.parser;

import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.Param;

public class TestParser {

    static final Logger logger = Common.logger(TestParser.class);
    
    static final Param param = Param.of("in", "debug/out", "04");

    @Test
    public void test調剤告示読み込み() throws IOException {
        logger.info(Common.methodName());
        String inTxtFile = param.txt("t", "ke");
        String outTxtFile = param.outDir("tk-tree.txt");
        Node root = new 調剤告示読み込み().parse(inTxtFile);
        root.summary(outTxtFile);
    }

    @Test
    public void test調剤通知読み込み() throws IOException {
        logger.info(Common.methodName());
        String inTxtFile = param.txt("t", "te");
        String outTxtFile = param.outDir("tt-tree.txt");
        Node root = new 調剤通知読み込み().parse(inTxtFile);
        root.summary(outTxtFile);
    }

    @Test
    public void test医科告示読み込み() throws IOException {
        logger.info(Common.methodName());
        String inTxtFile = param.txt("i", "ke");
        String outTxtFile = param.outDir("ik-tree.txt");
        Node root = new 医科告示読み込み().parse(inTxtFile);
        root.summary(outTxtFile);
    }

    @Test
    public void test医科通知読み込み() throws IOException {
        logger.info(Common.methodName());
        String inTxtFile = param.txt("i", "te");
        String outTxtFile = param.outDir("it-tree.txt");
        Node root = new 医科通知読み込み().parse(inTxtFile);
        root.summary(outTxtFile);
    }
}
