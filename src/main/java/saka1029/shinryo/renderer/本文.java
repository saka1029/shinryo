package saka1029.shinryo.renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Token;
import saka1029.shinryo.parser.TokenType;

public abstract class 本文 {
	static final Logger logger = Logger.getLogger(本文.class.getName());
	final String outDir;
	final Set<String> fileNames = new HashSet<>();

	public 本文(String outDir) throws IOException {
		Files.createDirectories(Path.of(outDir));
		this.outDir = outDir;
	}

	String style(int indent, String number) {
		float width = (number.codePoints().map(c -> c < 256 ? 1 : 2).sum() + 1) / 2.0F;
		return "style='margin-left:%sem;text-indent:%sem'".formatted(indent * 2 + width, -width);
	}

	boolean isAny(Node node, TokenType... types) {
		return Stream.of(types).anyMatch(t -> node.token.type == t);
	}

	boolean childContainsAny(Node node, TokenType... types) {
		return node.children.stream().anyMatch(n -> Stream.of(types).anyMatch(t -> n.token.type == t));
	}

	void writeLink(Node node, int level, String fileName, TextWriter writer) throws IOException {
		Token t = node.token;
		writer.println("<p %s><a href='%s'>%s %s</a>%s%s</p>", style(level, t.number), fileName, t.number, t.header,
				t.body.size() > 0 ? "<br>" : "", t.body.stream().collect(Collectors.joining()));
		render(node, t.number + " " + t.header, fileName);
	}

	void writeLink(Node node, int level, TextWriter writer) throws IOException {
		writeLink(node, level, node.path + ".html", writer);
	}

	void writeLinkKubun(Node node, int level, TextWriter writer) throws IOException {
		writeLink(node, level, node.id + ".html", writer);
	}

	void writeLine(Node node, int level, TextWriter writer) throws IOException {
		Token t = node.token;
		writer.println("<p %s>%s %s%s%s</p>", style(level, t.number), t.number, t.header,
				t.body.size() > 0 ? "<br>" : "", t.body.stream().collect(Collectors.joining()));
		for (Node child : node.children)
			render(child, level + 1, writer);
	}

	abstract void render(Node node, int level, TextWriter writer) throws IOException;

	public void render(Node node, String title, String fileName) throws IOException {
		if (fileNames.contains(fileName))
			logger.severe("ファイル「" + fileName + "」が重複しています");
		fileNames.add(fileName);
		try (TextWriter writer = new TextWriter(Path.of(outDir, fileName))) {
			String[] titleSplit = title.split("\\s+", 3);
			String titleHead = titleSplit.length < 2 ? titleSplit[0] : titleSplit[0] + " " + titleSplit[1];
			String titleRest = titleSplit.length < 3 ? "" : titleSplit[2];
			writer.println("<!DOCTYPE html>");
			writer.println("<html lang='ja'>");
			writer.println("<head>");
			writer.println("<meta charset='utf-8'>");
			writer.println("<title>%s</title>", titleHead);
			if (!node.isRoot())
				writer.println("<!-- file:%s page:%d line:%d -->", node.token.fileName, node.token.pageNo,
						node.token.lineNo);
			writer.println("</head>");
			writer.println("<body style='font-family:monospace'>");
			writer.println("<h1>%s</h1>", titleHead);
			if (!titleRest.isEmpty())
				writer.println("<p>%s</p>", titleRest);
			if (!node.isRoot() && node.token.body.size() > 0)
				writer.println("<p>%s</p>", node.token.body.stream().collect(Collectors.joining()));
			// root自体はrender()しない点に注意する。
			for (Node child : node.children)
				render(child, 0, writer);
			writer.println("</body>");
			writer.println("</html>");
		}
	}
}
