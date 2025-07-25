package saka1029.shinryo.renderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.pdf.PDFSplitter;
import saka1029.shinryo.pdf.様式;

public class 様式一覧 extends HTML {

    final boolean isSingle;

    public 様式一覧(String outDir, String 点数表, boolean isSingle) throws IOException {
        super(outDir, 点数表);
        this.isSingle = isSingle;
    }

    String target() {
        return isSingle ? " target='inner-frame'" : "";
    }

    public void render(String inCsvFile, String title, String outHtmlFile) throws IOException {
        String fullTitle = title + " 様式一覧";
        try (BufferedReader reader = Files.newBufferedReader(Path.of(inCsvFile), StandardCharsets.UTF_8);
            TextWriter writer = new TextWriter(Path.of(outDir, outHtmlFile));
            PDFSplitter splitter = new PDFSplitter()) {
            head(fullTitle, null, writer);
            writer.println("<body>");
            writer.println("<div id='all'>");
			// パンくずリスト
			writer.println("<div id='breadcrumb'>");
//			writer.println("<a href='../../index.html'>トップ</a>");
            if (!isSingle)
                menu(writer);
			writer.println("</div>"); // id=breadcrumb
			writer.println("<div id='content'>");
            if (isSingle) {
                writer.println("<div id='left-frame'>");
                menu(writer);
            }
            writer.println("<h1 class='title'>%s</h1>", fullTitle);
            String fileName = Path.of(inCsvFile).getFileName().toString();
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                ++lineNo;
                if (line.isBlank() || line.matches("\\s*#.*"))
                    continue;
                様式 e = new 様式(line);
                writer.println("<!-- %s:%s %s:%s --><p><a href='pdf/%s.pdf'%s>%s %s</a></p>",
                    Path.of(e.file).getFileName(), e.startPage, fileName, lineNo,
                    e.id, target(), e.name, e.title);
                if (!isSingle)
                    splitter.split(e.file, Path.of(outDir, "pdf", e.id + ".pdf").toString(), e.startPage, e.endPage);
            }
            if (isSingle) {
                writer.println("</div>"); // id=left-frame
                writer.println("<div id='right-frame'>");
                writer.println("<iframe id='inner-frame' name='inner-frame' frameborder='0'>");
                writer.println("</ifreme>");
                writer.println("</div>"); // id=right-frame
            }
			writer.println("</div>"); // id=content
			writer.println("</div>"); // id=all
            writer.println("</body>");
            writer.println("</html>");
        }
    }
}
