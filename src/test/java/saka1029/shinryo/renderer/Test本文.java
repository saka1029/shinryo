package saka1029.shinryo.renderer;

import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.Param;
import saka1029.shinryo.parser.医科告示読み込み;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.調剤告示読み込み;

public class Test本文 {

    static final Logger logger = Common.logger(Test本文.class);
    
    static final Param param = Param.of("in", "debug/out", "04");

    @Test
    public void testIRenderer() throws IOException {
        String inTxtFile = param.txt("i", "ke");
        String outDir = param.outDir("i");
        String title = "令和04年医科点数表";
        String outHtmlFile = "index.html";
        Node root = new 医科告示読み込み().parse(inTxtFile);
        new 医科本文(outDir).render(root, title, outHtmlFile);
    }

    @Test
    public void testKRenderer() throws IOException {
        String inTxtFile = param.txt("t", "ke");
        String outDir = param.outDir("t");
        String title = "令和04年調剤点数表";
        String outHtmlFile = "index.html";
        Node root = new 調剤告示読み込み().parse(inTxtFile);
        new 調剤本文(outDir).render(root, title, outHtmlFile);
    }

}
