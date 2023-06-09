package saka1029.shinryo.renderer;

import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.Param;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Parser;
import saka1029.shinryo.parser.調剤告示読み込み;
import saka1029.shinryo.parser.調剤通知読み込み;

public class TestHtml {

    static final Logger logger = Common.logger(TestHtml.class);
    
    static final Param param = Param.of("in", "debug/html", "04");

    @Test
    public void test調剤() throws IOException {
        String 点数表 = "t";
        String kTxt = param.txt(点数表, "ke");
        String tTxt = param.txt(点数表, "te");
        String outDir = param.outDir(点数表);
        String title = param.title(点数表);
        String outHtmlFile = "index.html";
        Common.copyTree(param.inHomeDir(), param.outHomeDir());
        Node kRoot = Parser.parse(new 調剤告示読み込み(), false, kTxt);
        Node tRoot = Parser.parse(new 調剤通知読み込み(), false, tTxt);
        Merger.merge(kRoot, tRoot);
        new Html(outDir).render(kRoot, title, outHtmlFile);
    }
}
