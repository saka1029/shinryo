package saka1029.shinryo.renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.TKParser;
import saka1029.shinryo.parser.Token;

public class KRenderer {
        final String outDir;

        public KRenderer(String outDir) throws IOException {
            Files.createDirectories(Path.of(outDir));
            this.outDir = outDir;
        }

        String style(int indent, String number) {
            float width = (number.codePoints()
                .map(c -> c < 256 ? 1 : 2)
                .sum() + 1) / 2.0F;
            return "style='margin-left:%sem;text-indent:%sem'".formatted(
                indent * 2 + width, -width);
        }

        void link(Node node, int level, TextWriter w) throws IOException {
            Token t = node.token;
            String fileName = node.id + ".html";
            w.println("<p %s><a href='%s'>%s %s</a>%s%s</p>",
                style(level, t.number), fileName,
                t.number, t.header,
                t.body.size() > 0 ? "<br>" : "",
                t.body.stream().collect(Collectors.joining()));
            render(node, t.number + " " + t.header, fileName);
        }

        void line(Node node, int level, TextWriter w) throws IOException {
            Token t = node.token;
            w.println("<p %s>%s %s%s%s</p>",
                style(level, t.number),
                t.number, t.header,
                t.body.size() > 0 ? "<br>" : "",
                t.body.stream().collect(Collectors.joining()));
            for (Node child : node.children)
                render(child, level + 1, w);
        }

        void render(Node node, int level, TextWriter w) throws IOException {
            if (node.token.type == TKParser.区分番号 && node.children.size() > 0)
                link(node, level, w);
            else
                line(node, level, w);
        }

        public void render(Node root, String title, String fileName) throws IOException {
            try (TextWriter w = new TextWriter(Path.of(outDir, fileName))) {
                w.println("<!DOCTYPE html>");
                w.println("<html lang='ja'>");
                w.println("<head>");
                w.println("<meta charset='utf-8'>");
                w.println("<title>%s</title>", title);
                w.println("</head>");
                w.println("<body style='font-family:monospace'>");
//                        w.println("<body>");
                w.println("<h1>%s</h1>", title);
                for (Node child : root.children)
                    render(child, 0, w);
                w.println("</body>");
                w.println("</html>");
            }
        }

}
