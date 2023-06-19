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
import saka1029.shinryo.parser.Pat;
import saka1029.shinryo.parser.医科告示読み込み;
import saka1029.shinryo.parser.医科通知読み込み;
import saka1029.shinryo.parser.歯科告示読み込み;
import saka1029.shinryo.parser.歯科通知読み込み;
import saka1029.shinryo.parser.調剤告示読み込み;
import saka1029.shinryo.parser.調剤通知読み込み;

public class Test本文 {

    static final Logger logger = Common.logger(Test本文.class);
    
    static final Param param = Param.of("in", "debug/html", "04");

//    @Test
    public void test医科() throws IOException {
    	logger.info(Common.methodName());
        String 点数表 = "i";
        String kTxt = param.txt(点数表, "ke");
        String tTxt = param.txt(点数表, "te");
        String outDir = param.outDir(点数表);
        String title = param.title(点数表);
        String outHtmlFile = "index.html";
        Common.copyTree(param.inHomeDir(), param.outHomeDir());
        if (Files.exists(Path.of(param.inDir(点数表, "img"))))
            Common.copyTree(param.inDir(点数表, "img"), param.outDir(点数表, "img"));
        Node kRoot = Parser.parse(new 医科告示読み込み(), false, kTxt);
        Node tRoot = Parser.parse(new 医科通知読み込み(), false, tTxt);
        Merger.merge(kRoot, tRoot);
        new 本文(outDir, Pat.医科リンク).render(kRoot, title, outHtmlFile);
    }

//    @Test
    public void test歯科() throws IOException {
    	logger.info(Common.methodName());
        String 点数表 = "s";
        String kTxt = param.txt(点数表, "ke");
        String tTxt = param.txt(点数表, "te");
        String outDir = param.outDir(点数表);
        String title = param.title(点数表);
        String outHtmlFile = "index.html";
        Common.copyTree(param.inHomeDir(), param.outHomeDir());
        if (Files.exists(Path.of(param.inDir(点数表, "img"))))
            Common.copyTree(param.inDir(点数表, "img"), param.outDir(点数表, "img"));
        Node kRoot = Parser.parse(new 歯科告示読み込み(), false, kTxt);
        Node tRoot = Parser.parse(new 歯科通知読み込み(), false, tTxt);
        Merger.merge(kRoot, tRoot);
        new 本文(outDir, Pat.医科リンク).render(kRoot, title, outHtmlFile);
    }

//    @Test
    public void test調剤() throws IOException {
    	logger.info(Common.methodName());
        String 点数表 = "t";
        String kTxt = param.txt(点数表, "ke");
        String tTxt = param.txt(点数表, "te");
        String outDir = param.outDir(点数表);
        String title = param.title(点数表);
        String outHtmlFile = "index.html";
        Common.copyTree(param.inHomeDir(), param.outHomeDir());
        if (Files.exists(Path.of(param.inDir(点数表, "img"))))
            Common.copyTree(param.inDir(点数表, "img"), param.outDir(点数表, "img"));
        Node kRoot = Parser.parse(new 調剤告示読み込み(), false, kTxt);
        Node tRoot = Parser.parse(new 調剤通知読み込み(), false, tTxt);
        Merger.merge(kRoot, tRoot);
        new 本文(outDir, Pat.調剤リンク).render(kRoot, title, outHtmlFile);
    }
}
