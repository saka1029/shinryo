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
import saka1029.shinryo.parser.医科告示読込;
import saka1029.shinryo.parser.歯科告示読込;
import saka1029.shinryo.parser.調剤告示読込;

public class Test区分番号一覧 {

    static final Logger logger = Common.logger(Test区分番号一覧.class);

    static final Param param = Param.of("in", "debug/html", "04");

//    @Test
    public void test歯科() throws IOException {
        String 点数表 = "s";
        Param prev = param.previous();
        Node newRoot = Parser.parse(new 歯科告示読込(), false, param.txt(点数表, "ke"));
        Node oldRoot = Files.exists(Path.of(prev.txt(点数表, "ke"))) ?
            Parser.parse(new 歯科告示読込(), false, prev.txt(点数表, "ke")) : null;
        new 区分番号一覧(param.outDir(点数表), 点数表, false).render(oldRoot, newRoot, param.title(点数表), param.年度, prev.年度, "kubun.html");
    }

//    @Test
    public void test調剤() throws IOException {
        String 点数表 = "t";
        Param prev = param.previous();
        Node newRoot = Parser.parse(new 調剤告示読込(), false, param.txt(点数表, "ke"));
        Node oldRoot = Files.exists(Path.of(prev.txt(点数表, "ke"))) ?
            Parser.parse(new 調剤告示読込(), false, prev.txt(点数表, "ke")) : null;
        new 区分番号一覧(param.outDir(点数表), 点数表, false).render(oldRoot, newRoot, param.title(点数表), param.年度, prev.年度, "kubun.html");
    }
    
    @Test
    public void test医科() throws IOException {
        String 点数表 = "i";
        Param prev = param.previous();
        Node newRoot = Parser.parse(new 医科告示読込(), false, param.txt(点数表, "ke"));
        Node oldRoot = Files.exists(Path.of(prev.txt(点数表, "ke"))) ?
            Parser.parse(new 医科告示読込(), false, prev.txt(点数表, "ke")) : null;
        new 区分番号一覧(param.outDir(点数表), 点数表, false).render(oldRoot, newRoot, param.title(点数表), param.年度, prev.年度, "kubun.html");
    }

}
