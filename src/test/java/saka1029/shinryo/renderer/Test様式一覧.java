package saka1029.shinryo.renderer;

import java.io.IOException;
import java.util.logging.Logger;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.Param;

public class Test様式一覧 {

    static final Logger logger = Common.logger(Test様式一覧.class);

    static final Param param = Param.of("in", "debug/html", "04");

//    @Test
    public void test医科() throws IOException {
        String 点数表 = "i";
        String inCsvFile = param.txt(点数表, "ye");
        String title = param.title(点数表);
        new 様式一覧(param.outDir(点数表), 点数表, false).render(inCsvFile, title, "yoshiki.html");
    }

//    @Test
    public void test歯科() throws IOException {
        String 点数表 = "s";
        String inCsvFile = param.txt(点数表, "ye");
        String title = param.title(点数表);
        new 様式一覧(param.outDir(点数表), 点数表, false).render(inCsvFile, title, "yoshiki.html");
    }

//    @Test
    public void test調剤() throws IOException {
        String 点数表 = "t";
        String inCsvFile = param.txt(点数表, "ye");
        String title = param.title(点数表);
        new 様式一覧(param.outDir(点数表), 点数表, false).render(inCsvFile, title, "yoshiki.html");
    }

}
