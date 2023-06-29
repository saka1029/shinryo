package saka1029.shinryo.renderer;

import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Token;

public class HTML {

    static class Link {
        final String url;
        final String title;
        
        Link(String url, String title) {
            this.url = url;
            this.title = title;
        }
    }

	static String lineDirective(Token token) {
	    return token == null ? "<!-- -->"
	        : "<!-- %s:%d %s:%d -->".formatted(token.pdfFileName, token.pageNo, token.txtFileName, token.lineNo);
	}

	static String indent(int indent, String number) {
		float width = (number.codePoints().map(c -> c < 256 ? 1 : 2).sum() + 1) / 2.0F;
		return "style='margin-left:%sem;text-indent:%sem'".formatted(indent * 2 + width, -width);
	}
	
	static void menu(TextWriter writer) {
		writer.println("<hr>");
		writer.println("<a href='index.html'>本文</a>");
		writer.println("<a href='kubun.html'>区分番号一覧</a>");
		writer.println("<a href='yoshiki.html'>様式一覧</a>");
	}

    static void head(String title, Node node, TextWriter writer) {
        writer.println("<!DOCTYPE html>");
        writer.println("<html lang='ja_JP'>");
        writer.println("<head>");
        writer.println("<meta charset='utf-8'>");
        writer.println("<meta name='viewport' content='initial-scale=1.0'>");
        writer.println("<link rel='icon' href='../../favicon.ico' />");
        writer.println("<link rel='stylesheet' type='text/css' href='../../all.css' />");
        writer.println("<title>%s</title>", title);
        if (node != null && node.token != null)
            writer.println(lineDirective(node.token));
        writer.println("</head>");
    }
	
	static String paths(Node node) {
	    Deque<String> list = new LinkedList<>();
	    for (Node p = node.parent; p != null && p.token != null; p = p.parent)
	        list.addFirst(p.token.number + " " + p.token.header0());
	    return list.stream().collect(Collectors.joining(" / "));
	}
}
