package saka1029.shinryo.renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.Param;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Parser;
import saka1029.shinryo.parser.施設基準通知読込;

public class Test施設基準通知本文 {

    static final Logger logger = Common.logger(Test施設基準通知本文.class);
    
    static final Param param = Param.of("in", "debug/html", "02");

    @Test
    public void testRender() throws IOException {
        logger.info(Common.methodName());
        String 点数表 = "k";
        String kTxt = param.txt(点数表, "te");
        String outDir = param.outDir(点数表);
        String title = param.title(点数表) + "(通知)";
        String outHtmlFile = "tuti.html";
        Common.copyTree(param.inHomeDir(), param.outHomeDir());
        if (Files.exists(Path.of(param.inDir(点数表, "img"))))
            Common.copyTree(param.inDir(点数表, "img"), param.outDir(点数表, "img"));
        Node kRoot = Parser.parse(new 施設基準通知読込(), false, kTxt);
        new 施設基準通知本文(outDir).render(kRoot, title, outHtmlFile);
    }

}
