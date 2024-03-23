package saka1029.shinryo.pdf;

import java.io.IOException;

import saka1029.shinryo.common.Param;

public class TestPDFSplitter {

    Param param = Param.of("in", "debug/pdf", "02");

//    @Test
    public void testSplit() throws IOException {
        String inPdfFile = param.pdf("i", "y")[0];
        try (PDFSplitter splitter = new PDFSplitter()) {
            splitter.split(inPdfFile, param.outFile("i-y-1.pdf"), 1, 1);
            splitter.split(inPdfFile, param.outFile("i-y-2.pdf"), 2, 4);
        }
    }

}
