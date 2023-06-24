package saka1029.shinryo.renderer;

import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.Param;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Parser;
import saka1029.shinryo.parser.医科告示読込;
import saka1029.shinryo.parser.医科通知読込;
import saka1029.shinryo.parser.歯科告示読込;
import saka1029.shinryo.parser.歯科通知読込;
import saka1029.shinryo.parser.調剤告示読込;
import saka1029.shinryo.parser.調剤通知読込;

public class TestMerger {

    static final Logger logger = Common.logger(TestMerger.class);
    
    static final Param param = Param.of("in", "debug/out", "04");

    /**
     * 2023-06-06 16:48:25.183 Testマージ INFO: test医科マージ            マージ先
     * 2023-06-06 16:48:26.741 Testマージ WARNING: マージ先なし: t        ルートの子供の先頭
     * 2023-06-06 16:48:26.744 Testマージ WARNING: マージ先なし: 2_t      第２章の子供の先頭
     * 2023-06-06 16:48:26.749 Testマージ WARNING: マージ先なし: 2_8_3    第２章第８部第２節の次
     * 2023-06-06 16:48:26.751 Testマージ WARNING: マージ先なし: 3_t      第３章の子供の先頭
     * 2_8_2は薬剤料
     * 2_8_3は経過措置
     */
//	@Test
	public void test医科() throws IOException {
        logger.info(Common.methodName());
        Node kRoot = Parser.parse(new 医科告示読込(), false, param.txt("i", "ke"));
        Node tRoot = Parser.parse(new 医科通知読込(), false, param.txt("i", "te"));
        Merger.merge(kRoot, tRoot);
        kRoot.summary(param.outFile("i-tree-merged.txt"));
	}

//	@Test
	public void test歯科() throws IOException {
        logger.info(Common.methodName());
        Node kRoot = Parser.parse(new 歯科告示読込(), false, param.txt("s", "ke"));
        Node tRoot = Parser.parse(new 歯科通知読込(), false, param.txt("s", "te"));
        Merger.merge(kRoot, tRoot);
        kRoot.summary(param.outFile("s-tree-merged.txt"));
	}

//	@Test
	public void test調剤() throws IOException {
        logger.info(Common.methodName());
        Node kRoot = Parser.parse(new 調剤告示読込(), false, param.txt("t", "ke"));
        Node tRoot = Parser.parse(new 調剤通知読込(), false, param.txt("t", "te"));
        Merger.merge(kRoot, tRoot);
        kRoot.summary(param.outFile("t-tree-merged.txt"));
	}
}
