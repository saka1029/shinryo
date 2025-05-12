package saka1029.shinryo.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.Param;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Parser;
import saka1029.shinryo.parser.Pat;
import saka1029.shinryo.parser.医科告示読込;
import saka1029.shinryo.parser.医科通知読込;
import saka1029.shinryo.parser.施設基準告示読込;
import saka1029.shinryo.parser.施設基準通知読込;
import saka1029.shinryo.parser.歯科告示読込;
import saka1029.shinryo.parser.歯科通知読込;
import saka1029.shinryo.parser.調剤告示読込;
import saka1029.shinryo.parser.調剤通知読込;
import saka1029.shinryo.pdf.PDF;
import saka1029.shinryo.pdf.様式;
import saka1029.shinryo.renderer.Merger;
import saka1029.shinryo.renderer.Sitemap;
import saka1029.shinryo.renderer.区分番号一覧;
import saka1029.shinryo.renderer.施設基準告示本文;
import saka1029.shinryo.renderer.施設基準通知本文;
import saka1029.shinryo.renderer.本文;
import saka1029.shinryo.renderer.様式一覧;

public class Main {

    static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    static final String DEFAULT_IN_DIR = "in";
    static final String DEFAULT_OUT_DIR = "debug/html";

    static RuntimeException usage(String message) {
        System.err.println("usage: java saka1029.shinryo.main.Main [-i inDir] [-o outDir] [-b baseUrl] 年度 STEP...");
        System.err.printf("inDir: 入力ディレクトリ(省略時は'%s')%n", DEFAULT_IN_DIR);
        System.err.printf("outDir: 出力ディレクトリ(省略時は'%s')%n", DEFAULT_OUT_DIR);
        System.err.println("baseUrl: サイトマップ作成時のベースURL(省略時はサイトマップを作成しません)");
        System.err.println("STEP: ([istk][012]) ...");
        System.err.println("   i:医科, s:歯科, t:調剤, k:施設基準, 0:PDF変換, 1:様式生成, 2:HTML生成");
        System.err.println("   ex) i1 i2 s1 s2");
        System.err.println("       (医科および歯科の様式およびHTML生成)");
        return new IllegalArgumentException(message);
    }

    static void イメージコピー(Param param, String 点数表) throws IOException {
        if (Files.exists(Path.of(param.inDir(点数表, "img")))) {
            LOGGER.info(param.title(点数表) + "イメージコピー");
            Common.copyTree(param.inDir(点数表, "img"), param.outDir(点数表, "img"));
        }
    }

    static void PDF変換(Param param, String 点数表) throws IOException {
        LOGGER.info(param.title(点数表) + "PDF変換");
        LOGGER.info("告示PDF変換");
        new PDF(true).テキスト変換(param.txt(点数表, "k"), param.pdf(点数表, "k"));
        LOGGER.info("通知PDF変換");
        new PDF(true).テキスト変換(param.txt(点数表, "t"), param.pdf(点数表, "t"));
        LOGGER.info("様式PDF変換");
        様式.様式一覧変換(param.txt(点数表, "y"), param.pdf(点数表, "y"));
    }

    static void 施設基準PDF変換(Param param, String 点数表) throws IOException {
        LOGGER.info(param.title(点数表) + "PDF変換");
        LOGGER.info("告示PDF変換");
        // 縦書き
        new PDF(false).テキスト変換(param.txt(点数表, "k"), param.pdf(点数表, "k"));
        LOGGER.info("通知PDF変換");
        new PDF(true).テキスト変換(param.txt(点数表, "t"), param.pdf(点数表, "t"));
        LOGGER.info("様式PDF変換");
        様式.施設基準様式一覧変換(param.txt(点数表, "y"), param.pdf(点数表, "y"));
    }

    static void 様式一覧生成(Param param, String 点数表) throws IOException {
        LOGGER.info(param.title(点数表) + "様式一覧生成");
        new 様式一覧(param.outDir(点数表), 点数表, false).render(param.txt(点数表, "ye"), param.title(点数表), "yoshiki.html");
        new 様式一覧(param.outDir(点数表), 点数表, true).render(param.txt(点数表, "ye"), param.title(点数表), "yoshiki-single.html");
    }

    static void HTML生成(Param param, String 点数表, Parser kParser, Parser tParser, Function<String, String> link)
        throws IOException {
        LOGGER.info(param.title(点数表) + "HTML生成");
        String kTxt = param.txt(点数表, "ke");
        String tTxt = param.txt(点数表, "te");
        String outDir = param.outDir(点数表);
        String title = param.title(点数表);
        Map<String, String> kubunMap = null;
        // 歯科の場合は医科の区分番号のマップを作成する。歯科以外の場合はkubunMap = null
        if (点数表.equals("s")) {
            Node ikaRoot = Parser.parse(new 医科告示読込(), false, param.txt("i", "ke"));
            kubunMap = 本文.区分名称マップ(ikaRoot);
        }
        Node kRoot = Parser.parse(kParser, true, kTxt);
        Node tRoot = Parser.parse(tParser, true, tTxt);
        Merger.merge(kRoot, tRoot);
        new 本文(outDir, 点数表, kubunMap, link, false).render(kRoot, title, "index.html");
        // シングルページ本文生成
        new 本文(outDir, 点数表, kubunMap, link, true).render(kRoot, title, "index-single.html");
        Param prev = param.previous();
        Node oldRoot = Files.exists(Path.of(prev.txt(点数表, "ke")))
            ? Parser.parse(kParser, false, prev.txt(点数表, "ke")) : null;
        LOGGER.info(param.title(点数表) + "区分番号一覧生成");
        new 区分番号一覧(outDir, 点数表, false).render(oldRoot, kRoot, title, param.年度, prev.年度, "kubun.html");
        // シングルページ区分番号一覧生成
        new 区分番号一覧(outDir, 点数表, true).render(oldRoot, kRoot, title, param.年度, prev.年度, "kubun-single.html");
        イメージコピー(param, 点数表);
    }
    
    static void 施設基準HTML生成(Param param) throws IOException {
        String 点数表 = "k";
        LOGGER.info(param.title(点数表) + "HTML生成");
        String outDir = param.outDir(点数表);
        String title = param.title(点数表);
        String kTitle = title + "(告示)";
        String tTitle = title + "(通知)";
        String kTxt = param.txt(点数表, "ke");
        String tTxt = param.txt(点数表, "te");
        LOGGER.info(kTitle);
        Node kRoot = Parser.parse(new 施設基準告示読込(), true, kTxt);
        LOGGER.info(tTitle);
        Node tRoot = Parser.parse(new 施設基準通知読込(), true, tTxt);
        new 施設基準告示本文(outDir).render(kRoot, kTitle, "index.html");
        new 施設基準通知本文(outDir).render(tRoot, tTitle, "tuti.html");
        イメージコピー(param, 点数表);
    }
    
    static void 終了(Param param, String baseUrl) throws IOException {
        LOGGER.info("ホームファイルコピー");
        Common.copyTree(param.inHomeDir(), param.outHomeDir());
        if (baseUrl != null) {
            LOGGER.info("サイトマップ作成");
            Sitemap.render(param.outHomeDir(), baseUrl);
        }
        LOGGER.info("終了");
    }

    public static void main(String[] args) throws IOException {
        String inDir = DEFAULT_IN_DIR;
        String outDir = DEFAULT_OUT_DIR;
        String baseUrl = null;
        String 年度 = null;
        int i = 0, size = args.length;
        L: for (; i < size; ++i) {
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
                case "-b":
                    if (i + 1 >= size)
                        throw usage("-bの後にパラメータがありません");
                    baseUrl = args[++i];
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
        if (年度 == null)
            throw usage("年度の指定がありません");
//        if (i >= size)
//            throw usage("STEPの指定がありません");
        Param param = Param.of(inDir, outDir, 年度);
        for (; i < size; ++i)
            switch (args[i]) {
                case "i0": PDF変換(param, "i"); break;
                case "i1": 様式一覧生成(param, "i"); break;
                case "i2": HTML生成(param, "i", new 医科告示読込(), new 医科通知読込(), Pat.医科リンク); break;
                case "s0": PDF変換(param, "s"); break;
                case "s1": 様式一覧生成(param, "s"); break;
                case "s2": HTML生成(param, "s", new 歯科告示読込(), new 歯科通知読込(), Pat.医科リンク); break;
                case "t0": PDF変換(param, "t"); break;
                case "t1": 様式一覧生成(param, "t"); break;
                case "t2": HTML生成(param, "t", new 調剤告示読込(), new 調剤通知読込(), Pat.調剤リンク); break;
                case "k0": 施設基準PDF変換(param, "k"); break;
                case "k1": 様式一覧生成(param, "k"); break;
                case "k2": 施設基準HTML生成(param); break;
                default: throw usage("不明なSTEPです(" + args[i] + ")");
            }
        終了(param, baseUrl);
    }
}
