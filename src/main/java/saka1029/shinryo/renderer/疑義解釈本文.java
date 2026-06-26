package saka1029.shinryo.renderer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Collectors;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Pat;
import saka1029.shinryo.parser.Token;

public class 疑義解釈本文 extends HTML {

    boolean isSingle;

    疑義解釈本文(String outDir, String 点数表, boolean isSingle) throws IOException {
        super(outDir, 点数表);
        this.isSingle = isSingle;
    }
	
	String linkText(String text) {
	    return Pat.全半角間空白削除(text);
	}

	String linkBodyText(Token token) {
	    return linkText(token.body.stream().collect(Collectors.joining()));
	}

    String target() {
        return isSingle ? " target='inner-frame'" : "";
    }

    public void link(Node node, int level, TextWriter writer, boolean bodyOnly) throws IOException {
        Token token = node.token;
        String title = "%s %s".formatted(token.number, token.header0());
        String url = "%s.html".formatted(token.type.name.equals("区分番号") ? node.id : node.path);
        writer.println("%s<p %s><a href='%s'%s>%s</a></p>",
            lineDirective(token), indent(level, token.number), url, target(), title);
        if (!isSingle)
            file(node, title, url, bodyOnly);
        if (bodyOnly)
			for (Node child : node.children)
				node(child, level + 1, writer);
    }

    public void text(Node node, int level, TextWriter writer) throws IOException {
        Token token = node.token;
        writer.println("%s<p %s>%s %s%s%s</p>",
            lineDirective(token), indent(level, token.number), token.number, linkText(token.header),
            token.body.size() > 0 ? "<br>" : "", linkBodyText(token));
        for (Node child : node.children)
            node(child, level + 1, writer);
    }

    public void node(Node node, int level, TextWriter writer) throws IOException {
        Token token = node.token;
        if (token.type.name.equals("区分番号") && !token.header.equals("削除"))
            link(node, level, writer, false);
        else if (MAIN_NODES.contains(token.type.name)) {
            if (node.children.stream().anyMatch(c -> !MAIN_TREE_NODES.contains(c.token.type.name)))
                link(node, level, writer, false);
            else if (node.token.body.size() > 0)
                link(node, level, writer, true);
            else
                text(node, level, writer);
        } else
            text(node, level, writer);
    }

    public void file(Node node, String title, String outHtmlFile, boolean bodyOnly) throws IOException {
        try (TextWriter writer = new TextWriter(Path.of(outDir, outHtmlFile))) {
            head(title, node, writer);
			writer.println("<body>");
            writer.println("<div id='all'>");
            if (!isSingle) {
                menu(writer);
                writer.println("<p class='title'>%s</p>", paths(node));
                writer.println("<h1 class='title'>%s</h1>", title);
            }
			writer.println("<div id='content'>");
            if (isSingle) {
                writer.println("<div id='left-frame'>");
                menu(writer);
                writer.println("<h1 class='title'>%s</h1>", title);
            }
			if (node.token != null) {
			    // headerの後半とbodyの出力
                Token token = node.token;
                if (!token.header1().isEmpty())
                    writer.println("<p><b>%s</b></p>", linkText(token.header1()));
                if (token.body.size() > 0)
                    writer.println("<p>%s</p>", linkBodyText(token));
			}
			if (!bodyOnly) {
				// 子ノードのレンダリング
				for (Node child : node.children)
					node(child, 0, writer);
			}
            if (isSingle) {
                writer.println("</div>"); // id='left-frame'
                writer.println("<div id='right-frame'>");
                writer.println("<iframe id='inner-frame' name='inner-frame' frameborder='0'>");
                writer.println("</iframe>");
                writer.println("</div>"); // id='right-frame'
            }
			writer.println("</div>"); // id='content'
            writer.println("</div>"); // id='all'
            writer.println("</body>");
            writer.println("</html>");
        }
    }

    public void render(Node node, String title, String outHtmlFile) throws IOException {
        this.mainTitle = title;
        file(node, title, outHtmlFile, false);
    }

}
