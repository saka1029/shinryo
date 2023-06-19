package saka1029.shinryo.pdf;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.Param;

public class Test様式 {

    static final Logger logger = Common.logger(Test様式.class);
    static final Param param = Param.of("in", "debug/out", "04");

    @Test
    public void testStandardPath() {
    	assertEquals("a/b/c", 様式.standardPath("a\\b\\c"));
    }

//    @Test
    public void test医科() throws IOException {
        String 点数表 = "i";
        String[] inPdfFiles = param.pdf(点数表, "y");
        String outTextFile = param.txt(点数表, "y");
        様式.様式一覧変換(outTextFile, inPdfFiles);
    }

//    @Test
    public void test歯科() throws IOException {
        String 点数表 = "s";
        String[] inPdfFiles = param.pdf(点数表, "y");
        String outTextFile = param.txt(点数表, "y");
        様式.様式一覧変換(outTextFile, inPdfFiles);
    }

//    @Test
    public void test調剤() throws IOException {
        String 点数表 = "t";
        String[] inPdfFiles = param.pdf(点数表, "y");
        String outTextFile = param.txt(点数表, "y");
        様式.様式一覧変換(outTextFile, inPdfFiles);
    }

}
