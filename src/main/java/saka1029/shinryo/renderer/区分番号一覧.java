package saka1029.shinryo.renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Token;

public class 区分番号一覧 {

    void listKubun(Node node, List<Node> list) {
        if (!node.isRoot())
            if (node.token.type.name.equals("区分番号"))
                list.add(node);
        for (Node child : node.children)
            listKubun(child, list);
    }

    List<Node> listKubun(Node root) {
        List<Node> list = new ArrayList<>();
        listKubun(root, list);
        return list;
    }

    public void render(Node root, String title, String outHtmlFile) throws IOException {
        Files.createDirectories(Path.of(outHtmlFile).getParent());
        List<Node> list = listKubun(root);
        try (TextWriter writer = new TextWriter(outHtmlFile)) {
            writer.println("<!DOCTYPE html>");
            writer.println("<html lang='ja'>");
            writer.println("<head>");
            writer.println("<meta charset='utf-8'>");
            writer.println("<title>%s</title>", title);
            writer.println("</head>");
            writer.println("<body style='font-family:monospace'>");
            writer.println("<h1>%s</h1>", title);
            writer.println("<ul>");
            for (Node kubun : list) {
                Token t = kubun.token;
                String header = t.header.replaceFirst("\\s+.*", "");
                if (header.equals("削除"))
                    writer.println("<li>%s %s</li>", t.number, header);
                else
                    writer.println("<li><a href='%s.html'>%s %s</a></li>", kubun.id, t.number, header);
            }
            writer.println("</ul>");
            writer.println("</body>");
            writer.println("</html>");
        }
    }
}
