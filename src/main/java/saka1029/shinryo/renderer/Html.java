package saka1029.shinryo.renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Deque;
import java.util.LinkedList;
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
    
	String indent(int indent, String number) {
		float width = (number.codePoints().map(c -> c < 256 ? 1 : 2).sum() + 1) / 2.0F;
		return "style='margin-left:%sem;text-indent:%sem'".formatted(indent * 2 + width, -width);
	}
	
	String lineDirective(Token token) {
	    return token == null ? "<!-- -->"
	        : "<!-- %s:%d %s:%d -->".formatted(token.pdfFileName, token.pageNo, token.txtFileName, token.lineNo);
	}

    public void link(Node node, int level, TextWriter writer, Deque<Link> links) throws IOException {
        Token token = node.token;
        String title = "%s %s".formatted(token.number, token.header0());
        String url = "%s.html".formatted(node.id);
        writer.println("<p %s><a href='%s'>%s</a></p>%s",
            indent(level, token.number), url, title, lineDirective(token));
        render(node, title, url, links);
    }

    public void text(Node node, int level, TextWriter writer, Deque<Link> links) throws IOException {
        Token token = node.token;
        writer.println("<p %s>%s %s%s%s</p>%s",
            indent(level, token.number), token.number, token.header,
            token.body.size() > 0 ? "<br>" : "", token.body.stream().collect(Collectors.joining()),
            lineDirective(token));
        for (Node child : node.children)
            render(child, level + 1, writer, links);
    }

    public void render(Node node, int level, TextWriter writer, Deque<Link> links) throws IOException {
        Token token = node.token;
        if (token.type.name.equals("区分番号") && !token.header.equals("削除"))
            link(node, level, writer, links);
        else
            text(node, level, writer, links);
    }

    public void render(Node node, String title, String outHtmlFile, Deque<Link> links) throws IOException {
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
			writer.println("<h1>%s</h1>", title);
			if (node.token != null) {
			    Token token = node.token;
                if (!token.header1().isEmpty())
                    writer.println("<p>%s</p>", token.header1());
                if (token.body.size() > 0)
                    writer.println("<p>%s</p>", token.body.stream().collect(Collectors.joining()));
			}
			links.push(new Link(outHtmlFile, title));
			// 子ノードのレンダリング
			for (Node child : node.children)
			    render(child, 0, writer, links);
			// 通知ノードのレンダリング
			if (node.tuti != null) {
			    writer.println("<div id='tuti'>");
			    writer.println("<p><b>通知</b></p>");
			    for (Node child : node.tuti.children)
                    render(child, 0, writer, links);
			    writer.println("</div>");
			}
			links.pop();
            writer.println("</body>");
            writer.println("</html>");
        }
    }

    public void render(Node node, String title, String outHtmlFile) throws IOException {
        Deque<Link> links = new LinkedList<>();
        links.add(new Link("../../index.html", "ホームページ"));
        render(node, title, outHtmlFile, links);
    }
}
