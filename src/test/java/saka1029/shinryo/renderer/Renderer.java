package saka1029.shinryo.renderer;

import java.io.IOException;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;

public class Renderer {
    
    public void render(Node top, String outHtmlFile) throws IOException {
        try (TextWriter w = new TextWriter(outHtmlFile)) {
            w.println("<!DOCTYPE html>");
            w.println("<html lang='ja'>");
            w.println("<head>");
            w.println("  <meta charset='utf-8'>");
            w.println("  <title>Test</title>");
            w.println("</head>");
            w.println("<body>");
            w.println("</body>");
            w.println("</html>");
        }
    }

}
