package saka1029.shinryo.renderer;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Token;

public class Html {

	static String lineDirective(Token token) {
	    return token == null ? "<!-- -->"
	        : "<!-- %s:%d %s:%d -->".formatted(token.pdfFileName, token.pageNo, token.txtFileName, token.lineNo);
	}

    static void head(String title, Node node, TextWriter writer) {
        writer.println("<!DOCTYPE html>");
        writer.println("<html lang='ja_JP'>");
        writer.println("<head>");
        writer.println("<meta charset='utf-8'>");
        writer.println("<meta name='viewport' content='initial-scale=1.0'>");
        writer.println("<link rel='stylesheet' type='text/css' href='../../all.css'>");
        writer.println("<title>%s</title>", title);
        if (node.token != null)
            writer.println(lineDirective(node.token));
        writer.println("</head>");
    }
}
