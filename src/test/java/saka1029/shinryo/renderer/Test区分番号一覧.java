package saka1029.shinryo.renderer;

import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.医科告示読み込み;
import saka1029.shinryo.parser.調剤告示読み込み;

public class Test区分番号一覧 {

    static final Logger logger = Common.logger(Test区分番号一覧.class);

    @Test
    public void test医科区分番号一覧() throws IOException {
        String inTxtFile = "in/04/i/txt/ke.txt";
        String outHtmlFile = "debug/out/04/i/kubun.html";
        String title = "令和04年医科区分番号一覧";
        Node root = new 医科告示読み込み().parse(inTxtFile);
        new 区分番号一覧().render(root, title, outHtmlFile);
    }

    @Test
    public void test調剤区分番号一覧() throws IOException {
        String inTxtFile = "in/04/t/txt/ke.txt";
        String outHtmlFile = "debug/out/04/t/kubun.html";
        String title = "令和04年調剤区分番号一覧";
        Node root = new 調剤告示読み込み().parse(inTxtFile);
        new 区分番号一覧().render(root, title, outHtmlFile);
    }

}
