package saka1029.shinryo.pdf;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import saka1029.shinryo.parser.Pat;

/**
 * 様式名から様式IDへの変換規則は以下の通りです。
 * 施設基準の場合は「別添xの様式yのz」のような形式になります。
 * <pre>
 * 別紙様式aのbのc → SYa-b-c
 * 別添aのbのc → Ta-b-c
 * 別紙aのbのc → Sa-b-c
 * 様式aのbのc → Ya-b-c
 * 別添6の別紙aのbのc → * T6-Sa-b-c
 * 別添7の様式aのbのc → T7-Ya-b-c
 * 別添2の様式aのbのc → T2-Ya-b-c
 * </pre>
 * @param name 様式名。ex.「別紙様式2の3」
 * @param id 様式ID。ex.「SY2_3」
 * @param startPage 開始ページ。ex.「123」
 * @param endPage 終了ページ。ex.「124」
 * @param title 様式タイトル。ex.「退院証明書」
 */
public class 様式 {
	static final Logger logger = Logger.getLogger(様式.class.getName());
    static final int 様式名出現最大行 = 3;

    public final String file, name, id;
    public final int startPage, endPage;
    public final String title;
    
    public 様式(String file, String name, String id, int startPage, int endPage, String title) {
        this.file = file;
        this.name = name;
        this.id = id;
        this.startPage = startPage;
        this.endPage = endPage;
        this.title = title;
    }

    public 様式(String line) {
        String[] f = line.split(",", 6);
        this.file = f[0];
        this.name = f[1];
        this.id = f[2];
        this.startPage = Integer.parseInt(f[3]);
        this.endPage = Integer.parseInt(f[4]);
        this.title = f[5];
    }

    static final Pattern 施設基準様式名パターン = Pattern.compile("\\s*[(（]?\\s*(" // 様式名 (group1)
        + "\\s*(?:別\\s*添|別\\s*紙|様\\s*式)"
        + "\\s*(?:(?:\\d|\\s+)+)" // 様式番号1
        + "\\s*(?:の\\s*(?:(?:\\d|\\s+)+))?" // 様式番号2
        + "\\s*(?:の\\s*(?:(?:\\d|\\s+)+))?" // 様式番号3
        + ")\\s*[)）]?(?:\\s+(.+))?"); // 様式タイトル (group2)

    static String standardPath(String path) {
        return path.replaceAll("\\\\", "/");
    }

    /**
     * 施設基準の様式PDFファイルから様式一覧を抽出してテキストファイルに出力します。
     * <p>
     * 〇施設基準の様式に関わる文書構成(令和4年度)
     * <p>
     * 
     * <pre>
     * 施設基準告示
     *      基本診療料(000907989.pdf)
     *          別添1 初・再診料の施設基準                          本文
     *          別添2 入院基本料等の施設基準                        本文
     *          別添3 入院基本料等加算の施設基準                    本文
     *          別添4 特定入院料の施設基準                          本文
     *          別添5 短期滞在手術等基本料の施設基準                本文
     *          別添6 診療等に要する書面等                          本文
     *              別紙1, 2, ...                                   診療等に要する様式
     *          別添7 基本診療料の施設基準等に係る届出書            届出書様式
     *          別添7の2 基本診療料の施設基準等に係る届出書         届出書様式
     *              様式1, 1の2, ...                                届出書様式
     *      特掲診療料(000907862.pdf)
     *          別添1 特掲診療料等の施設基準                        本文
     *          別添2 特掲診療料の施設基準に係る届出書              届出書様式
     *              様式1, 1の2, ...                                届出書様式
     *          別添2の2 特掲診療料の施設基準等に係る届出書         届出書様式
     * </pre>
     * 
     * 基本診療料の様式は別紙nと様式nの2種類がある。 特掲診療料の様式は様式nのみである。 別添nは基本的に本文の見出しである。
     * ただし、基本診療料の別添7および別添7の2、 特掲診療料の別添2および別添2の2はそれぞれ独立した様式である。
     * <p>
     * 〇PDFファイル名
     * <p>
     * 
     */
    public static void 施設基準様式一覧変換(String outTxtFile, String... inPdfFiles) throws IOException {
        Files.createDirectories(Path.of(outTxtFile).getParent());
        try (PrintWriter out = new PrintWriter(outTxtFile, StandardCharsets.UTF_8)) {
            for (String inPdfFile : inPdfFiles) {
//                out.printf("#file %s\n", inPdfFile);
                List<List<String>> pageLines = new PDF(true).read(inPdfFile);
                String betten = ""; // 直前の「別添n」
                int startPage = -1;
                String name = null, id = null, title = null;
                int i = 0;
                for (int pageCount = pageLines.size(); i < pageCount; ++i) {
                    List<String> lines = pageLines.get(i);
                    for (int j = 0, lineCount = Math.min(様式名出現最大行, lines.size()); j < lineCount; ++j) {
                        String norm = Normalizer.normalize(lines.get(j), Form.NFKD);
                        Matcher m = 施設基準様式名パターン.matcher(norm);
                        if (m.matches()) {
                            if (name != null)
                                out.printf("%s,%s,%s,%d,%d,%s\n",
                                    standardPath(inPdfFile), name, id, startPage, i, title);
                            startPage = i + 1;
                            name = m.group(1).replaceAll("\\s+", "");
                            if (name.matches("別添\\d+"))
                                betten = name;
                            title = m.group(2);
                            if (title == null && j + 1 < lines.size())
                                title = lines.get(j + 1);
                            title = title.replaceAll("\\s+", "");
                            id = Pat.正規化(name.startsWith("別添") ? name : betten + "の" + name);
                        }
                    }
                }
                if (name != null)
                    out.printf("%s,%s,%s,%d,%d,%s\n", standardPath(inPdfFile), name, id, startPage, i, title);
            }
        }
    }

    /**
     * 医科、歯科、調剤の様式PDFファイルから様式一覧を抽出してテキストファイルに出力します。
     */
    static final Pattern 様式名パターン = Pattern.compile("\\s*[(（]?\\s*(" // 様式名 (group1)
        + "\\s*(?:別\\s*[紙添]\\s*様\\s*式)"
        + "\\s*(?:(?:\\d|\\s+)+)"  // 様式番号1
        + "\\s*(?:[の-]\\s*(?:(?:\\d|\\s+)+))?" // 様式番号2
        + "\\s*(?:[の-]\\s*(?:(?:\\d|\\s+)+))?" // 様式番号3
        + ")\\s*[)）]?(?:\\s+(.+))?"); // 様式タイトル (group2)

    public static void 様式一覧変換(String outTxtFile, String... inPdfFiles) throws IOException {
        Files.createDirectories(Path.of(outTxtFile).getParent());
        try (PrintWriter out = new PrintWriter(outTxtFile, StandardCharsets.UTF_8)) {
            for (String inPdfFile : inPdfFiles) {
//                out.printf("#file %s\n", inPdfFile);
                List<List<String>> pageLines = new PDF(true).read(inPdfFile);
                int startPage = -1;
                String name = null, id = null, title = null;
                int i = 0;
                for (int pageCount = pageLines.size(); i < pageCount; ++i) {
                    List<String> lines = pageLines.get(i);
                    for (int j = 0, lineCount = Math.min(様式名出現最大行, lines.size()); j < lineCount; ++j) {
                        String norm = Normalizer.normalize(lines.get(j), Form.NFKD);
                        Matcher m = 様式名パターン.matcher(norm);
                        if (m.matches()) {
                            if (name != null)
                                out.printf("%s,%s,%s,%d,%d,%s\n", standardPath(inPdfFile), name, id, startPage, i, title);
                            startPage = i + 1;
                            // 調剤は「別紙様式1」を間違えて「別添様式1」と記述している。(令和4年にはない)
                            name = m.group(1).replaceAll("\\s+", "").replaceAll("添", "紙");
                            title = m.group(2);
                            if (title == null && j + 1 < lines.size())
                                title = lines.get(j + 1);
                            title = title.replaceAll("\\s+", "");
                            id = Pat.正規化(name);
                        }
                    }
                }
                if (name != null)
                    out.printf("%s,%s,%s,%d,%d,%s\n", standardPath(inPdfFile), name, id, startPage, i, title);
            }
        }
    }
}
