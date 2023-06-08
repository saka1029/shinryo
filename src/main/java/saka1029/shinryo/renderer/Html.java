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
        @Override
        public String toString() {
            return "<a href='%s'>%s</a>".formatted(url, title);
        }
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

    public void text(Node node, int level, TextWriter writer, Deque<Link> links) throws IOException {
        Token token = node.token;
        writer.println("<p %s>%s %s%s%s</p><!-- %s:%d:%d -->",
            indent(level, token.number), token.number, token.header,
            token.body.size() > 0 ? "<br>" : "", token.body.stream().collect(Collectors.joining()),
            token.fileName, token.pageNo, token.lineNo);
        for (Node child : node.children)
            render(child, level + 1, writer, links);
    }

    public void render(Node node, int level, TextWriter writer, Deque<Link> links) throws IOException {
        text(node, level, writer, links);
    }

    public void render(Node node, String title, String outHtmlFile, Deque<Link> links) throws IOException {
        try (TextWriter writer = new TextWriter(Path.of(outDir, outHtmlFile))) {
            writer.println("<!DOCTYPE html>");
            writer.println("<html lang='ja_JP'>");
            writer.println("<head>");
			writer.println("<meta charset='utf-8'>");
			writer.println("<title>%s</title>", title);
            writer.println("</head>");
			writer.println("<body style='font-family:monospace'>");
			writer.println("<div id='breadcrumb'>");
			String sep = "";
			for (Link link : (Iterable<Link>) () -> links.descendingIterator()) {
			    writer.println("%s<a href='%s'>%s</a>", sep, link.url, link.title);
			    sep = "&gt; ";
			}
			writer.println("</div>");
			writer.println("<h1>%s</h1>", title);
			links.push(new Link(outHtmlFile, title));
			for (Node child : node.children)
			    render(child, 0, writer, links);
			links.pop();
            writer.println("</body>");
            writer.println("</html>");
        }
    }

    public void render(Node node, String title, String outHtmlFile) throws IOException {
        render(node, title, outHtmlFile, new LinkedList<>());
    }
}
