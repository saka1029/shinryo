package saka1029.shinryo.renderer;

import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Logging;
import saka1029.shinryo.parser.医科告示読み込み;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.調剤告示読み込み;

public class Test本文 {
    static { Logging.init(); } 
    static final Logger logger = Logger.getLogger(Test本文.class.getSimpleName());

    @Test
    public void testIRenderer() throws IOException {
        String inTxtFile = "in/04/i/txt/ke.txt";
        String outDir = "debug/out/04/i";
        String title = "令和04年医科点数表";
        String outHtmlFile = "index.html";
        Node root = new 医科告示読み込み().parse(inTxtFile);
        new 医科本文(outDir).render(root, title, outHtmlFile);
    }

    @Test
    public void testKRenderer() throws IOException {
        String inTxtFile = "in/04/t/txt/ke.txt";
        String outDir = "debug/out/04/t";
        String title = "令和04年調剤点数表";
        String outHtmlFile = "index.html";
        Node root = new 調剤告示読み込み().parse(inTxtFile);
        new 調剤本文(outDir).render(root, title, outHtmlFile);
    }

}
