package saka1029.shinryo.pdf;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.Param;

public class TestPDF {

    static final Logger logger = Common.logger(TestPDF.class);
    static final Param param = Param.of("in", "debug/out", "04");

    @Test
    public void test調剤() throws IOException {
        String 点数表 = "t";
        String[] inPdfFiles = param.pdf(点数表, "y");
        String outTextFile = param.outFile("t-yoshiki.txt");
        様式.様式一覧変換(outTextFile, inPdfFiles);
    }

}
