package saka1029.shinryo.pdf;

import java.io.IOException;
import java.util.logging.Logger;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.Param;

public class TestPDF {

    static final Logger logger = Common.logger(TestPDF.class);
    static final Param param = Param.of("in", "debug/out", "04");

//    @Test
    public void test医科告示() throws IOException {
        String 点数表 = "i";
        String[] inPdfFiles = param.pdf(点数表, "k");
        String outTxtFile = param.txt(点数表, "k");
        new PDF(true).テキスト変換(outTxtFile, inPdfFiles);
    }

//    @Test
    public void test医科通知() throws IOException {
        String 点数表 = "i";
        String[] inPdfFiles = param.pdf(点数表, "t");
        String outTxtFile = param.txt(点数表, "t");
        new PDF(true).テキスト変換(outTxtFile, inPdfFiles);
    }

//    @Test
    public void test歯科告示() throws IOException {
        String 点数表 = "s";
        String[] inPdfFiles = param.pdf(点数表, "k");
        String outTxtFile = param.txt(点数表, "k");
        new PDF(true).テキスト変換(outTxtFile, inPdfFiles);
    }

//    @Test
    public void test歯科通知() throws IOException {
        String 点数表 = "s";
        String[] inPdfFiles = param.pdf(点数表, "t");
        String outTxtFile = param.txt(点数表, "t");
        new PDF(true).テキスト変換(outTxtFile, inPdfFiles);
    }

//    @Test
    public void test調剤告示() throws IOException {
        String 点数表 = "t";
        String[] inPdfFiles = param.pdf(点数表, "k");
        String outTxtFile = param.txt(点数表, "k");
        new PDF(true).テキスト変換(outTxtFile, inPdfFiles);
    }

//    @Test
    public void test調剤通知() throws IOException {
        String 点数表 = "t";
        String[] inPdfFiles = param.pdf(点数表, "t");
        String outTxtFile = param.txt(点数表, "t");
        new PDF(true).テキスト変換(outTxtFile, inPdfFiles);
    }

}
