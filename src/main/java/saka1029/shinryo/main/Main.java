package saka1029.shinryo.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

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
import saka1029.shinryo.pdf.PDF;
import saka1029.shinryo.pdf.様式;
import saka1029.shinryo.renderer.Merger;
import saka1029.shinryo.renderer.区分番号一覧;
import saka1029.shinryo.renderer.本文;
import saka1029.shinryo.renderer.様式一覧;

public class Main {

    static RuntimeException usage(String message) {
        System.err.println("usage: java saka1029.shinryo.main.Main [-i inDir] [-o outDir] 年度 STEP...");
        return new IllegalArgumentException(message);
    }
    
    static void PDF変換(Param param, String 点数表) throws IOException {
        new PDF(true).テキスト変換(param.txt(点数表, "k"), param.pdf(点数表, "k"));
        new PDF(true).テキスト変換(param.txt(点数表, "t"), param.pdf(点数表, "t"));
        様式.様式一覧変換(param.txt(点数表, "y"), param.pdf(点数表, "y"));
    }

    static void 様式生成(Param param, String 点数表) throws IOException {
        new 様式一覧().render(param.txt(点数表, "ye"), param.title(点数表), param.outFile(点数表, "yoshiki.html"));
    }

    static void HTML生成(Param param, String 点数表, Parser kParser, Parser tParser, Function<String, String> link) throws IOException {
        String kTxt = param.txt(点数表, "ke");
        String tTxt = param.txt(点数表, "te");
        String outDir = param.outDir(点数表);
        String title = param.title(点数表);
        Common.copyTree(param.inHomeDir(), param.outHomeDir());
        if (Files.exists(Path.of(param.inDir(点数表, "img"))))
            Common.copyTree(param.inDir(点数表, "img"), param.outDir(点数表, "img"));
        Node kRoot = Parser.parse(kParser, false, kTxt);
        Node tRoot = Parser.parse(tParser, false, tTxt);
        Merger.merge(kRoot, tRoot);
        new 本文(outDir, link).render(kRoot, title, "index.html");
        new 区分番号一覧().render(kRoot, title, param.outFile(点数表, "kubun.html"));
    }

    public static void main(String[] args) throws IOException {
        String inDir = "in";
        String outDir = "debug/html";
        String 年度 = null;
        int i = 0, size = args.length;
        L: for ( ; i < size; ++i) {
            switch (args[i]) {
                case "-i":
                    if (i + 1 >= size)
                        throw usage("-iの後にパラメータがありません");
                    inDir = args[++i];
                    break;
                case "-o":
                    if (i + 1 >= size)
                        throw usage("-oの後にパラメータがありません");
                    outDir = args[++i];
                    break;
                default:
                    if (args[i].startsWith("-"))
                        throw usage("不明なオプション(" + args[i] + ")");
                    if (!args[i].matches("\\d{2}"))
                        throw usage("(" + args[i] + ")は不正な年度です");
                    年度 = args[i++];
                    break L;
            }
        }
        if (i >= size)
            throw usage("STEPの指定がありません");
        Param param = Param.of(inDir, outDir, 年度);
        for (; i < size; ++i)
            switch (args[i]) {
                case "i0": PDF変換(param, "i"); break;
                case "i1": 様式生成(param, "i"); break;
                case "i2": HTML生成(param, "i", new 医科告示読み込み(), new 医科通知読み込み(), Pat.医科リンク); break;
                case "s0": PDF変換(param, "s"); break;
                case "s1": 様式生成(param, "s"); break;
                case "s2": HTML生成(param, "s", new 歯科告示読み込み(), new 歯科通知読み込み(), Pat.医科リンク); break;
                case "t0": PDF変換(param, "t"); break;
                case "t1": 様式生成(param, "t"); break;
                case "t2": HTML生成(param, "t", new 調剤告示読み込み(), new 調剤通知読み込み(), Pat.調剤リンク); break;
                default:
                    throw usage("不明なSTEPです(" + args[i] + ")");
            }
    }
}
