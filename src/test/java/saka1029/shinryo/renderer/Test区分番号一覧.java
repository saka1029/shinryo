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
import saka1029.shinryo.parser.医科告示読み込み;
import saka1029.shinryo.parser.歯科告示読み込み;
import saka1029.shinryo.parser.調剤告示読み込み;

public class Test区分番号一覧 {

    static final Logger logger = Common.logger(Test区分番号一覧.class);

    static final Param param = Param.of("in", "debug/html", "04");

//    @Test
    public void test歯科() throws IOException {
        String 点数表 = "s";
        Param prev = param.previous();
        String outTxtFile = param.outFile(点数表, "kubun.html");
        Files.createDirectories(Path.of(outTxtFile).getParent());
        Node newRoot = Parser.parse(new 歯科告示読み込み(), false, param.txt(点数表, "ke"));
        Node oldRoot = Files.exists(Path.of(prev.txt(点数表, "ke"))) ?
            Parser.parse(new 歯科告示読み込み(), false, prev.txt(点数表, "ke")) : null;
        new 区分番号一覧().render(oldRoot, newRoot, param.title(点数表), 点数表, param.年度, prev.年度, outTxtFile);
    }

//    @Test
    public void test調剤() throws IOException {
        String 点数表 = "t";
        Param prev = param.previous();
        String outTxtFile = param.outFile(点数表, "kubun.html");
        Files.createDirectories(Path.of(outTxtFile).getParent());
        Node newRoot = Parser.parse(new 調剤告示読み込み(), false, param.txt(点数表, "ke"));
        Node oldRoot = Files.exists(Path.of(prev.txt(点数表, "ke"))) ?
            Parser.parse(new 調剤告示読み込み(), false, prev.txt(点数表, "ke")) : null;
        new 区分番号一覧().render(oldRoot, newRoot, param.title(点数表), 点数表, param.年度, prev.年度, outTxtFile);
    }
    
    @Test
    public void test医科() throws IOException {
        String 点数表 = "i";
        Param prev = param.previous();
        String outTxtFile = param.outFile(点数表, "kubun.html");
        Files.createDirectories(Path.of(outTxtFile).getParent());
        Node newRoot = Parser.parse(new 医科告示読み込み(), false, param.txt(点数表, "ke"));
        Node oldRoot = Files.exists(Path.of(prev.txt(点数表, "ke"))) ?
            Parser.parse(new 医科告示読み込み(), false, prev.txt(点数表, "ke")) : null;
        new 区分番号一覧().render(oldRoot, newRoot, param.title(点数表), 点数表, param.年度, prev.年度, outTxtFile);
    }

}
