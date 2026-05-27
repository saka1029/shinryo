package saka1029.shinryo.renderer;

import java.io.IOException;
import java.util.logging.Logger;

// import org.junit.Test;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.Param;

public class Test様式一覧 {

    static final Logger logger = Common.logger(Test様式一覧.class);

    static final Param param = Param.of("in", "debug/html", "04");

//    @Test
    public void test医科() throws IOException {
        String 点数表 = "i";
        String yeTxt = param.inFile(点数表, "txt/ye.txt");
        String ysTxt = param.inFileOrNull(点数表, "txt/ys.txt");
        String title = param.title(点数表);
        String outDir = param.outDir(点数表);
        new 様式一覧(outDir, 点数表, false).render(yeTxt, ysTxt, title, "yoshiki.html");
    }

//    @Test
    public void test歯科() throws IOException {
        String 点数表 = "s";
        String yeTxt = param.inFile(点数表, "txt/ye.txt");
        String ysTxt = param.inFileOrNull(点数表, "txt/ys.txt");
        String title = param.title(点数表);
        String outDir = param.outDir(点数表);
        new 様式一覧(outDir, 点数表, false).render(yeTxt, ysTxt, title, "yoshiki.html");
    }

//    @Test
    public void test調剤() throws IOException {
        String 点数表 = "t";
        String yeTxt = param.inFile(点数表, "txt/ye.txt");
        String ysTxt = param.inFileOrNull(点数表, "txt/ys.txt");
        String title = param.title(点数表);
        String outDir = param.outDir(点数表);
        new 様式一覧(outDir, 点数表, false).render(yeTxt, ysTxt, title, "yoshiki.html");
    }

}
