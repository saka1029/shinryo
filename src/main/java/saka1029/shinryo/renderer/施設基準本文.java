package saka1029.shinryo.renderer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Token;

public abstract class 施設基準本文 extends HTML {

    final String pathPrefix;
    
	施設基準本文(String outDir, String pathPrefix) throws IOException {
		super(outDir, "k");
		this.pathPrefix = pathPrefix;
	}
	
	public abstract String anchor(String s, Node n);

    public void link(Node node, int level, TextWriter writer, Deque<Link> links, boolean bodyOnly) throws IOException {
        Token token = node.token;
        String title = "%s %s".formatted(token.number, token.header0());
        String url = "%s%s.html".formatted(pathPrefix, node.path);
        writer.println("%s<p %s><a href='%s'>%s</a></p>",
            lineDirective(token), indent(level, token.number), url, title);
        file(node, title, url, links);
        if (bodyOnly)
			for (Node child : node.children)
				node(child, level + 1, writer, links);
    }

    public void text(Node node, int level, TextWriter writer, Deque<Link> links) throws IOException {
        Token token = node.token;
        writer.println("%s<p %s>%s %s%s%s</p>",
            lineDirective(token), indent(level, token.number), token.number, anchor(token.header, node),
            token.body.size() > 0 ? "<br>" : "", anchor(token.body.stream().collect(Collectors.joining()), node));
        for (Node child : node.children)
            node(child, level + 1, writer, links);
    }

    abstract void node(Node node, int level, TextWriter writer, Deque<Link> links) throws IOException;
    
    void file(Node node, String title, String outHtmlFile, Deque<Link> links) throws IOException {
        try (TextWriter writer = new TextWriter(Path.of(outDir, outHtmlFile))) {
            head(title, node, writer);
			writer.println("<body>");
			writer.println("<div id='all'>");
			// パンくずリスト
			writer.println("<div id='breadcrumb'>");
//			String sep = "";
//			for (Link link : (Iterable<Link>) () -> links.descendingIterator()) {
//			    writer.println("%s<a href='%s'>%s</a>", sep, link.url, link.title);
//			    sep = "&gt; ";
//			}
			menu(writer);
			writer.println("</div>"); // id='breadcrumb'
			writer.println("<p class='title'>%s</p>", paths(node));
			writer.println("<h1 class='title'>%s</h1>", title);
			writer.println("<div id='content'>");
			if (node.token != null) {
			    // headerの後半とbodyの出力
                Token token = node.token;
                if (!token.header1().isEmpty())
                    writer.println("<p><b>%s</b></p>", anchor(token.header1(), node));
                if (token.body.size() > 0)
                    writer.println("<p>%s</p>", anchor(String.join("", token.body), node));
			}
			links.push(new Link(outHtmlFile, title));
            // 子ノードのレンダリング
            for (Node child : node.children)
                node(child, 0, writer, links);
			writer.println("</div>"); // id='content'
			writer.println("</div>"); // id='all'
            writer.println("</body>");
            writer.println("</html>");
			links.pop();
        }
    }

    public void render(Node root, String title, String outHtmlFile) throws IOException {
        Deque<Link> links = new LinkedList<>();
        links.add(new Link("../../index.html", "トップ"));
        file(root, title, outHtmlFile, links);
    }
}
