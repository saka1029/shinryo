package saka1029.shinryo.renderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Token;

public class HTML {

    static final String GOOGLE_ANALYTICS_PATH = "/GoogleAnalytics.txt";
    static final String GOOGLE_ANALYTICS;
    static {
        try (InputStream is = HTML.class.getResourceAsStream(GOOGLE_ANALYTICS_PATH);
            BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            GOOGLE_ANALYTICS = br.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    static class Link {
        final String url;
        final String title;
        
        Link(String url, String title) {
            this.url = url;
            this.title = title;
        }
    }

    final String outDir;
    final String 点数表;
    String mainTitle = "";
    
    HTML(String outDir, String 点数表) throws IOException {
        this.outDir = outDir;
        this.点数表 = 点数表;
        Files.createDirectories(Path.of(outDir));
    }

	static String lineDirective(Token token) {
	    return token == null ? "<!-- -->"
	        : "<!-- %s:%d %s:%d -->".formatted(token.pdfFileName, token.pageNo, token.txtFileName, token.lineNo);
	}

	static String indent(int indent, String number) {
		float width = (number.codePoints().map(c -> c < 256 ? 1 : 2).sum() + 1) / 2.0F;
		return "style='margin-left:%sem;text-indent:%sem'".formatted(indent * 2 + width, -width);
	}
	
	void menu(TextWriter writer) {
		writer.println("<div id='menu'></div>");
		writer.println("<script type='text/javascript' src='../../menu.js'></script>");
//		writer.println("<a href='../../index.html'>トップ</a>");
//		if (点数表.equals("k")) {
//            writer.println("<a href='index.html'>告示</a>");
//            writer.println("<a href='tuti.html'>通知</a>");
//		} else {
//            writer.println("<a href='index.html'>本文</a>");
//            writer.println("<a href='kubun.html'>区分番号一覧</a>");
//		}
//		writer.println("<a href='yoshiki.html'>様式一覧</a>");
	}
	
    static void head(String title, Node node, TextWriter writer) {
        writer.println("<!DOCTYPE html>");
        writer.println("<html lang='ja_JP'>");
        writer.println("<head>");
        writer.println(GOOGLE_ANALYTICS);
        writer.println("<meta charset='utf-8'>");
        writer.println("<meta name='viewport' content='initial-scale=1.0'>");
        writer.println("<link rel='icon' href='../../favicon.ico' />");
        writer.println("<link rel='stylesheet' type='text/css' href='../../all.css' />");
        writer.println("<title>%s</title>", title);
        if (node != null && node.token != null)
            writer.println(lineDirective(node.token));
        writer.println("</head>");
    }
	
	String paths(Node node) {
	    Deque<String> list = new LinkedList<>();
	    for (Node p = node.parent; p != null && p.token != null; p = p.parent)
	        list.addFirst(p.token.number + " " + p.token.header0());
	    if (!list.isEmpty())
	        list.addFirst(mainTitle);
	    return list.stream().collect(Collectors.joining(" / "));
	}
}
