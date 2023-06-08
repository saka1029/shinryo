package saka1029.shinryo.renderer;

import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.Param;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.調剤告示読み込み;

public class TestHtml {

    static final Logger logger = Common.logger(TestHtml.class);
    
    static final Param param = Param.of("in", "debug/out/html", "04");

    @Test
    public void test調剤() throws IOException {
        String inTxtFile = param.txt("t", "ke");
        String outDir = param.outDir("t");
        String title = "令和04年調剤点数表";
        String outHtmlFile = "index.html";
        Node root = new 調剤告示読み込み().parse(inTxtFile);
        new Html(outDir).render(root, title, outHtmlFile);
    }

}
