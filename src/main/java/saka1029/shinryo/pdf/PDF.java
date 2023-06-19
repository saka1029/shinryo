package saka1029.shinryo.pdf;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import saka1029.shinryo.common.Common;

public class PDF {

    static final Logger LOGGER = Common.logger(PDF.class);
    {
        // PDFBox関連のログを抑制します。
        Logger logger = Logger.getLogger("org.apache");
        logger.setLevel(Level.SEVERE);
//        Logger.getLogger("org.apache").setLevel(Level.SEVERE);
    }
    static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    static final Charset 出力文字セット = StandardCharsets.UTF_8;
    static final String 改行文字 = "\n";

    /**
     * A4のポイントサイズ 横約8.27 × 縦約11.69 インチ 595.44 x 841.68 ポイント
     */
    public static final float PAGE_WIDTH = 596F, PAGE_HEIGHT = 842F;

    public final boolean horizontal;

    public float 行併合範囲割合 = 0.6F;
    public float ルビ割合 = 0.6F;
    public float 行高さ規定値 = 10F;
    public float 行間隔規定値 = 14F;
    public Pattern ルビパターン = Pattern.compile("\\p{IsHiragana}*");
    public Pattern ページ番号パターン = Pattern.compile("^\\s*\\S*\\s*-\\s*\\d+\\s*-\\s*$");
    public DebugElement debugElement = null;

    public PDF(boolean horizontal) {
        this.horizontal = horizontal;
    }

    public record Element(float x, float y, float fontSize, String text) {
        @Override
        public String toString() {
            return "%sx%s:%s:%s".formatted(x, y, fontSize, text);
        }
    }

    public interface DebugElement {
        void element(String path, int pageNo, int lineNo, 文書属性 attr, TreeSet<Element> elements);
    }

    static final Comparator<Element> 行内ソート = Comparator.comparing(Element::x)
        .thenComparing(Comparator.comparing(Element::y).reversed());

    List<TreeMap<Float, List<Element>>> 行分割(List<List<Element>> fileElements) {
        List<TreeMap<Float, List<Element>>> result = new ArrayList<>();
        for (List<Element> pageElements : fileElements) {
            TreeMap<Float, List<Element>> lineElements = new TreeMap<>();
            result.add(lineElements);
            for (Element e : pageElements)
                lineElements.computeIfAbsent(e.y, k -> new ArrayList<>()).add(e);
        }
        return result;
    }

    public record 文書属性(boolean 横書き, float 左余白, float 行間隔, float 行高さ, float 行併合範囲, float ルビ高さ) {
    }

    文書属性 文書属性(List<TreeMap<Float, List<Element>>> fileLines) {
        float 左余白 = Float.MAX_VALUE;
        Map<Float, Integer> 行間隔度数分布 = new HashMap<>();
        Map<Float, Integer> 行高さ度数分布 = new HashMap<>();
        for (TreeMap<Float, List<Element>> pageElements : fileLines) {
            float prevY = Float.MIN_VALUE;
            for (Entry<Float, List<Element>> line : pageElements.entrySet()) {
                左余白 = Math.min(左余白, line.getValue().get(0).x);
                float y = line.getKey();
                if (prevY != Float.MIN_VALUE)
                    行間隔度数分布.compute(y - prevY, (k, v) -> v == null ? 1 : v + 1);
                prevY = y;
                for (Element e : line.getValue())
                    行高さ度数分布.compute(e.fontSize, (k, v) -> (v == null ? 0 : v) + e.text.length());
            }
        }
        if (左余白 == Float.MAX_VALUE)
            左余白 = 0;
        float 行間隔 = 行間隔度数分布.entrySet().stream()
            .max(Entry.comparingByValue())
            .map(Entry::getKey)
            .orElse(行間隔規定値);
        float 行高さ = 行高さ度数分布.entrySet().stream()
            .max(Entry.comparingByValue())
            .map(Entry::getKey)
            .orElse(行高さ規定値);
        float 行併合範囲 = 行間隔 * 行併合範囲割合;
        float ルビ高さ = 行高さ * ルビ割合;
        return new 文書属性(horizontal, 左余白, 行間隔, 行高さ, 行併合範囲, ルビ高さ);
    }

    float round(float value) {
        return Math.round(value);
    }

    void parse(PDDocument doc, List<List<Element>> fileElements, int pageNo) throws IOException {
        List<Element> pageElements = new ArrayList<>();
        fileElements.add(pageElements);
        new PDFTextStripper() {
            {
                setStartPage(pageNo);
                setEndPage(pageNo);
                getText(doc);
            }

            @Override
            protected void processTextPosition(TextPosition text) {
                if (text.getUnicode().isBlank())
                    return;
                Element element = new Element(
                    round(horizontal ? text.getX() : text.getY()),
                    round(horizontal ? text.getY() : PAGE_WIDTH - text.getX()),
                    round(text.getXScale()),
                    text.getUnicode());
                pageElements.add(element);
            }
        };
    }

    String toString(TreeSet<Element> line, float leftMargin, float charWidth) {
        StringBuilder sb = new StringBuilder();
        float halfWidth = charWidth / 2;
        float start = leftMargin;
        for (Element e : line) {
            int spaces = Math.round((e.x - start) / halfWidth);
            for (int i = 0; i < spaces; ++i)
                sb.append(" ");
            sb.append(e.text);
            start = e.x + (float) e.text.codePoints().mapToDouble(c -> c < 256 ? halfWidth : charWidth).sum();
        }
        return sb.toString();
    }

    void addLine(List<String> list, TreeSet<Element> sortedLine, String path, int pageNo, int lineNo, 文書属性 文書属性) {
        if (!sortedLine.isEmpty())
            list.add(toString(sortedLine, 文書属性.左余白, 文書属性.行高さ));
        if (debugElement != null)
            debugElement.element(path, pageNo, lineNo, 文書属性, sortedLine);
        sortedLine.clear();
    }

    public List<List<String>> read(String inPdfPath) throws IOException {
        List<List<String>> result = new ArrayList<>();
        PDDocument doc = PDDocument.load(new File(inPdfPath));
        int numberOfPages = doc.getNumberOfPages();
        List<List<Element>> fileElements = new ArrayList<>();
        try (Closeable c = () -> doc.close()) {
            for (int i = 0; i < numberOfPages; ++i)
                parse(doc, fileElements, i + 1);
        }
        List<TreeMap<Float, List<Element>>> fileLines = 行分割(fileElements);
        文書属性 文書属性 = 文書属性(fileLines);
        LOGGER.info(inPdfPath + ":" + 文書属性);
        int pageNo = 0;
        for (TreeMap<Float, List<Element>> lines : fileLines) {
            ++pageNo;
            List<String> linesString = new ArrayList<>();
            result.add(linesString);
//			float y = Float.MIN_VALUE;
            TreeSet<Element> sortedLine = new TreeSet<>(行内ソート);
            int lineNo = 0;
            for (Entry<Float, List<Element>> line : lines.entrySet()) {
                List<Element> lineElements = line.getValue();
                if (lineElements.stream().allMatch(e -> e.fontSize <= 文書属性.ルビ高さ && ルビパターン.matcher(e.text).matches()))
                    continue;
//				if (y != Float.MIN_VALUE && line.getKey() > y + 文書属性.行併合範囲)
                if (!sortedLine.isEmpty()
                    && line.getKey() > sortedLine.stream().mapToDouble(Element::y).average().getAsDouble() + 文書属性.行併合範囲)
                    addLine(linesString, sortedLine, inPdfPath, pageNo, ++lineNo, 文書属性);
                sortedLine.addAll(lineElements);
            }
            addLine(linesString, sortedLine, inPdfPath, pageNo, ++lineNo, 文書属性);
        }
        return result;
    }

    public void テキスト変換(String outTxtFile, String... inPdfFiles) throws IOException {
        Files.createDirectories(Path.of(outTxtFile).getParent());
        try (PrintWriter writer = new PrintWriter(new FileWriter(outTxtFile, 出力文字セット))) {
            for (String path : inPdfFiles) {
                List<List<String>> pages = read(path);
                for (int i = 0, pageSize = pages.size(); i < pageSize; ++i) {
                    writer.printf("# file: %s page: %d%s", Path.of(path).getFileName(), i + 1, 改行文字);
                    for (String line : pages.get(i))
                        writer.printf("%s%s", ページ番号パターン.matcher(line).replaceFirst("#$0"), 改行文字);
                }
            }
        }
    }

    public static void ページ分割(String inPdfFile, String outPdfFile, int startPage, int endPage) throws IOException {
        Files.createDirectories(Path.of(outPdfFile).getParent());
        try (PDDocument inPdf = PDDocument.load(new File(inPdfFile))) {
                Splitter splitter = new Splitter();
                splitter.setStartPage(startPage);
                splitter.setEndPage(endPage);
                splitter.setSplitAtPage(inPdf.getNumberOfPages());
                List<PDDocument> splitted = splitter.split(inPdf);
                try {
                    splitted.get(0).save(outPdfFile);
                } finally {
                    for (PDDocument d : splitted)
                        d.close();
                }
        }
    }

//    public static List<様式> ページ分割(String inTextFile, String outDir) throws IOException {
//        Files.createDirectories(Path.of(outDir));
//        List<様式> result = new ArrayList<>();
//        try (BufferedReader reader = Files.newBufferedReader(Path.of(inTextFile), 出力文字セット)) {
//            PDDocument doc = null;
//            String line, curFile = null;
//            while ((line = reader.readLine()) != null) {
//                if (line.isBlank() || line.startsWith("\\s*#"))
//                    continue;
//                様式 e = new 様式(line);
//                String outFile = e.id + ".pdf";
//                result.add(e);
//                if (!e.file.equals(curFile)) {
//                    if (doc != null)
//                        doc.close();
//                    doc = PDDocument.load(new File(e.file));
//                    curFile = e.file;
//                }
//                Splitter splitter = new Splitter();
//                splitter.setStartPage(e.startPage);
//                splitter.setEndPage(e.endPage);
//                splitter.setSplitAtPage(doc.getNumberOfPages());
//                List<PDDocument> splitted = splitter.split(doc);
//                splitted.get(0).save(outDir + "/" + outFile);
//                for (PDDocument d : splitted)
//                    d.close();
//            }
//            if (doc != null)
//                doc.close();
//        }
//        return result;
//    }
}
