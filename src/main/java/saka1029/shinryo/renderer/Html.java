package saka1029.shinryo.renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Token;

public class Html {

    record Link(String url, String title) {
    }

    public final String outDir;
    
    public Html(String outDir) throws IOException {
        this.outDir = outDir;
        Files.createDirectories(Path.of(outDir));
    }
    
	static String indent(int indent, String number) {
		float width = (number.codePoints().map(c -> c < 256 ? 1 : 2).sum() + 1) / 2.0F;
		return "style='margin-left:%sem;text-indent:%sem'".formatted(indent * 2 + width, -width);
	}
	
	static String lineDirective(Token token) {
	    return token == null ? "<!-- -->"
	        : "<!-- %s:%d %s:%d -->".formatted(token.pdfFileName, token.pageNo, token.txtFileName, token.lineNo);
	}

	static void beginTuti(TextWriter writer) {
        writer.println("<div id='tuti'>");
        writer.println("<p><b>通知</b></p>");
	}
	
	static void endTuti(TextWriter writer) {
        writer.println("</div>");
	}
	
	static String paths(Node node) {
	    Deque<String> list = new LinkedList<>();
	    for (Node p = node.parent; p != null && p.token != null; p = p.parent)
	        list.addFirst(p.token.number + " " + p.token.header0());
	    return list.stream().collect(Collectors.joining(" &gt; "));
	}

    public void link(Node node, int level, TextWriter writer, Deque<Link> links, boolean bodyOnly) throws IOException {
        Token token = node.token;
        String title = "%s %s".formatted(token.number, token.header0());
        String url = "%s.html".formatted(token.type.name.equals("区分番号") ? node.id : node.path);
        writer.println("%s<p %s><a href='%s'>%s</a></p>",
            lineDirective(token), indent(level, token.number), url, title);
        file(node, title, url, links, bodyOnly);
        if (bodyOnly)
			for (Node child : node.children)
				node(child, level + 1, writer, links);
    }

    public void text(Node node, int level, TextWriter writer, Deque<Link> links) throws IOException {
        Token token = node.token;
        writer.println("%s<p %s>%s %s%s%s</p>",
            lineDirective(token), indent(level, token.number), token.number, token.header,
            token.body.size() > 0 ? "<br>" : "", token.body.stream().collect(Collectors.joining()));
        for (Node child : node.children)
            node(child, level + 1, writer, links);
    }

    static final Set<String> MAIN_NODES = Set.of("章", "部", "節", "款", "通則", "区分番号");
    static final Set<String> MAIN_TREE_NODES = Set.of("章", "部", "節", "款", "通則");

    public void node(Node node, int level, TextWriter writer, Deque<Link> links) throws IOException {
        Token token = node.token;
        if (token.type.name.equals("区分番号") && !token.header.equals("削除"))
            link(node, level, writer, links, false);
        else if (MAIN_NODES.contains(token.type.name)) {
            if (node.children.stream().anyMatch(c -> !MAIN_TREE_NODES.contains(c.token.type.name)))
                link(node, level, writer, links, false);
            else if (node.token.body.size() > 0)
                link(node, level, writer, links, true);
            else
                text(node, level, writer, links);
        } else
            text(node, level, writer, links);
    }

    public void file(Node node, String title, String outHtmlFile, Deque<Link> links, boolean bodyOnly) throws IOException {
        try (TextWriter writer = new TextWriter(Path.of(outDir, outHtmlFile))) {
            writer.println("<!DOCTYPE html>");
            writer.println("<html lang='ja_JP'>");
            writer.println("<head>");
			writer.println("<meta charset='utf-8'>");
			writer.println("<meta name='viewport' content='initial-scale=1.0'>");
            writer.println("<link rel='stylesheet' type='text/css' href='../../all.css'>");
			writer.println("<title>%s</title>", title);
            writer.println(lineDirective(node.token));
            writer.println("</head>");
			writer.println("<body>");
			// パンくずリスト
			writer.println("<div id='breadcrumb'>");
			String sep = "";
			for (Link link : (Iterable<Link>) () -> links.descendingIterator()) {
			    writer.println("%s<a href='%s'>%s</a>", sep, link.url, link.title);
			    sep = "&gt; ";
			}
			writer.println("</div>");
			writer.println("<p class='title'>%s</p>", paths(node));
			writer.println("<h1 class='title'>%s</h1>", title);
			if (node.isTuti)
			    beginTuti(writer);
			if (node.token != null) {
			    // headerの後半とbodyの出力
                Token token = node.token;
                if (!token.header1().isEmpty())
                    writer.println("<p><b>%s</b></p>", token.header1());
                if (token.body.size() > 0)
                    writer.println("<p>%s</p>", token.body.stream().collect(Collectors.joining()));
			}
			links.push(new Link(outHtmlFile, title));
			if (!bodyOnly) {
				// 子ノードのレンダリング
				for (Node child : node.children)
					node(child, 0, writer, links);
			}
			// 通知ノードのレンダリング
			if (!node.isTuti && node.tuti != null
				&& (!node.tuti.token.body.stream().allMatch(String::isBlank) || node.tuti.children.size() > 0)) {
			    beginTuti(writer);
                writer.println("<p>%s</p>", node.tuti.token.body.stream().collect(Collectors.joining()));
//			    node(node.tuti, 0, writer, links);
			    for (Node child : node.tuti.children)
                    node(child, 0, writer, links);
			    endTuti(writer);
			}
			if (node.isTuti)
			    endTuti(writer);
            writer.println("</body>");
            writer.println("</html>");
			links.pop();
        }
    }

    public void render(Node node, String title, String outHtmlFile) throws IOException {
        Deque<Link> links = new LinkedList<>();
        links.add(new Link("../../index.html", "ホームページ"));
        file(node, title, outHtmlFile, links, false);
    }
}
