package saka1029.shinryo.renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Token;

public class 区分番号一覧 extends Html {

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
        String fullTitle = title + " 区分番号一覧";
        Files.createDirectories(Path.of(outHtmlFile).getParent());
        List<Node> list = listKubun(root);
        try (TextWriter writer = new TextWriter(outHtmlFile)) {
            head(fullTitle, root, writer);
            writer.println("<body>");
			// パンくずリスト
			writer.println("<div id='breadcrumb'>");
			writer.println("<a href='../../index.html'>トップ</a>");
			menu(writer);
			writer.println("</div>");
            writer.println("<h1 class='title'>%s</h1>", fullTitle);
            writer.println("<ul>");
            for (Node kubun : list) {
                Token token = kubun.token;
                if (token.header.equals("削除"))
                    writer.println("<li>%s %s</li>", token.number, token.header0());
                else
                    writer.println("<li><a href='%s.html'>%s %s</a></li>", kubun.id, token.number, token.header0());
            }
            writer.println("</ul>");
            writer.println("</body>");
            writer.println("</html>");
        }
    }
}
