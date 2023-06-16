package saka1029.shinryo.renderer;

import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.Param;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.医科告示読み込み;
import saka1029.shinryo.parser.歯科告示読み込み;
import saka1029.shinryo.parser.調剤告示読み込み;

public class Test区分番号一覧 {

    static final Logger logger = Common.logger(Test区分番号一覧.class);

    static final Param param = Param.of("in", "debug/html", "04");

    @Test
    public void test医科() throws IOException {
        String 点数表 = "i";
        String inTxtFile = param.txt(点数表, "ke");
        String outHtmlFile = param.outFile(点数表, "kubun.html");
        String title = param.title(点数表);
        Node root = new 医科告示読み込み().parse(inTxtFile);
        new 区分番号一覧().render(root, title, outHtmlFile);
    }

    @Test
    public void test歯科() throws IOException {
        String 点数表 = "s";
        String inTxtFile = param.txt(点数表, "ke");
        String outHtmlFile = param.outFile(点数表, "kubun.html");
        String title = param.title(点数表);
        Node root = new 歯科告示読み込み().parse(inTxtFile);
        new 区分番号一覧().render(root, title, outHtmlFile);
    }

    @Test
    public void test調剤() throws IOException {
        String 点数表 = "t";
        String inTxtFile = param.txt(点数表, "ke");
        String outHtmlFile = param.outFile(点数表, "kubun.html");
        String title = param.title(点数表);
        Node root = new 調剤告示読み込み().parse(inTxtFile);
        new 区分番号一覧().render(root, title, outHtmlFile);
    }

}
