package saka1029.shinryo.renderer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Pat;
import saka1029.shinryo.parser.Token;

public class 疑義解釈本文 extends HTML {

    record NameNode(String name, Node node) {

        @Override
        public final boolean equals(Object arg0) {
            return arg0 instanceof NameNode right
                && Objects.equals(name, right.name);
        }

        @Override
        public final int hashCode() {
            return name.hashCode();
        }
    }

    final Map<NameNode, Map<NameNode, List<Node>>> 疑義解釈;

    public 疑義解釈本文(String outDir, Node root) throws IOException {
        super(outDir, "g");
        this.疑義解釈 = 分類(root);
    }
	
    /**
     * 疑義のツリーを
     * 分類および名称でソートします。
     */
    static Map<NameNode, Map<NameNode, List<Node>>> 分類(Node root) {
        Map<NameNode, Map<NameNode, List<Node>>> result = new LinkedHashMap<>();
        Consumer<Node> visitor = new Consumer<>() {
            Map<NameNode, List<Node>> 分類 = null;
            List<Node> 名称 = null;
            @Override
            public void accept(Node node) {
                if (node.token == null) return;
                NameNode nn = new NameNode(node.token.number, node);
                switch (node.token.type.name) {
                    case "分類":
                        分類 = result.computeIfAbsent(nn, k -> new LinkedHashMap<>());
                        break;
                    case "名称":
                        名称 = 分類.computeIfAbsent(nn, k -> new ArrayList<>());
                        break;
                    case "問":
                        名称.add(node);
                        break;
                }
            }
        };
        root.visit(visitor);
        return result;
    }

	String text(String text) {
	    return Pat.全半角間空白削除(text) + "<br>";
	}

	String bodyText(Token token) {
	    return token.body.stream()
            .map(line -> Pat.全半角間空白削除(line) + "<br>")
            .collect(Collectors.joining());
	}
	

    void file(String title, int b, int n, List<Node> 問リスト) throws IOException {
        try (TextWriter writer = new TextWriter(Path.of(outDir, "b%d_n%d.html".formatted(b, n)))) {
            head(title, null, writer);
			writer.println("<body>");
            writer.println("<div id='all'>");
            menu(writer);
            // writer.println("<p class='title'>%s</p>", paths(node));
            writer.println("<h1 class='title'>%s</h1>", title);
			writer.println("<div id='content'>");
				// 問のレンダリング
                for (Node 問ノード : 問リスト) {
                    Token 問 = 問ノード.token;
                    writer.println("%s<div class='rbox'><p %s>%s %s%s</p></div>",
                        lineDirective(問), indent(0, 問.number), 問.number, text(問.header),
                        bodyText(問));
                    Token 答 = 問ノード.children.get(0).token;
                    writer.println("%s<p %s>%s %s%s</p>",
                        lineDirective(答), indent(0, 答.number), 答.number, text(答.header),
                        bodyText(答));
                }
			writer.println("</div>"); // id='content'
            writer.println("</div>"); // id='all'
            writer.println("</body>");
            writer.println("</html>");
        }
    }

    public void file(String title, String outHtmlFile, boolean isSingle) throws IOException {
        try (TextWriter writer = new TextWriter(Path.of(outDir, outHtmlFile))) {
            head(title, null, writer);
			writer.println("<body>");
            writer.println("<div id='all'>");
            if (!isSingle) {
                menu(writer);
                // writer.println("<p class='title'>%s</p>", paths(node));
                writer.println("<h1 class='title'>%s</h1>", title);
            }
			writer.println("<div id='content'>");
            if (isSingle) {
                writer.println("<div id='left-frame'>");
                menu(writer);
                writer.println("<h1 class='title'>%s</h1>", title);
            }
            // 子ノードのレンダリング
            int b = 0;
            for (Entry<NameNode, Map<NameNode, List<Node>>> 分類 : 疑義解釈.entrySet()) {
                ++b;
                writer.println("%s<p %s>%s</p>",
                    lineDirective(分類.getKey().node.token), indent(0, ""), 分類.getKey().name);
                int n = 0;
                for (Entry<NameNode, List<Node>> 名称 : 分類.getValue().entrySet()) {
                    ++n;
                    writer.println("%s<p %s><a%s href='b%d_n%d.html'>%s</a></p>",
                        lineDirective(名称.getKey().node.token), indent(1, ""),
                        isSingle ? " target='inner-frame'" : "", b, n, 名称.getKey().name);
                    if (!isSingle)
                        file(分類.getKey().name + "<br>" + 名称.getKey().name, b, n, 名称.getValue());
                }
            }
            if (isSingle) {
                writer.println("</div>"); // id='left-frame'
                writer.println("<div id='right-frame'>");
                writer.println("<iframe id='inner-frame' name='inner-frame' src='../../iframe-default.html' frameborder='0'>");
                writer.println("</iframe>");
                writer.println("</div>"); // id='right-frame'
            }
			writer.println("</div>"); // id='content'
            writer.println("</div>"); // id='all'
            writer.println("</body>");
            writer.println("</html>");
        }
    }

    public void render(String title, String outHtmlFile, boolean isSingle) throws IOException {
        this.mainTitle = title;
        file(title, outHtmlFile, isSingle);
    }

}
