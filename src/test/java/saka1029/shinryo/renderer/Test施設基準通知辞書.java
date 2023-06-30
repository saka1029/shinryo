package saka1029.shinryo.renderer;

import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.Param;
import saka1029.shinryo.common.Trie;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Parser;
import saka1029.shinryo.parser.施設基準通知読込;

public class Test施設基準通知辞書 {
    
    static final Logger LOGGER = Common.logger(Test施設基準通知辞書.class);
    
    Param param = Param.of("in", "debug/out", "02");

    @Test
    public void testCreate() throws IOException {
        LOGGER.info(Common.methodName());
        String 点数表 = "k";
        String kTxt = param.txt(点数表, "te");
        Node kRoot = Parser.parse(new 施設基準通知読込(), false, kTxt);
        Trie<Node> trie = 施設基準通知辞書.create(kRoot);
        LOGGER.info("size=" + trie.size());
    }

}
