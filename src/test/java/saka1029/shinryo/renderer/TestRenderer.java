package saka1029.shinryo.renderer;

import java.io.IOException;
import java.util.stream.Collectors;

import org.junit.Test;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.IKParser;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Token;

public class TestRenderer {

    static void render(Node node, String outHtmlFile) throws IOException {
        try (TextWriter w = new TextWriter(outHtmlFile)) {
            new Object() {

                String style(int indent, String number) {
                    float width = (number.codePoints()
                        .map(c -> c < 256 ? 1 : 2)
                        .sum() + 1) / 2.0F;
                    return "style='margin-left:%sem;text-indent:%sem'".formatted(
                        indent * 2 + width, -width);
                }

                void line(Node node, int indent) {
                    if (!node.isRoot()) {
                        Token t = node.token;
                        w.println("<p %s>%s %s%s%s</p>",
                            style(indent, t.number),
                            t.number, t.header,
                            t.body.size() > 0 ? "<br>" : "",
                            t.body.stream().collect(Collectors.joining()));
                    }
                    for (Node child : node.children)
                        line(child, indent + 1);
                }

                void render(Node node) {
                    w.println("<!DOCTYPE html>");
                    w.println("<html lang='ja'>");
                    w.println("<head>");
                    w.println("  <meta charset='utf-8'>");
                    w.println("  <title>Test</title>");
                    w.println("</head>");
//                    w.println("<body style='font-family:MS Gothic'>");
                    w.println("<body>");
                    line(node, 0);
                    w.println("</body>");
                    w.println("</html>");
                }
            }.render(node);
        }
    }

    @Test
    public void test() throws IOException {
        String inTxtFile = "data/04ik.txt";
        String outHtmlFile = "data/04ik.html";
        Node root = new IKParser().parse(inTxtFile);
//        root.summary(outTxtFile);
        render(root, outHtmlFile);
    }

}
