package saka1029.shinryo.renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Pat;
import saka1029.shinryo.parser.Token;

public class 本文 extends HTML {

	static final Logger LOGGER = Logger.getLogger(本文.class.getName());

    final Map<String, String> kubunMap;
    final Function<String, String> linker;
    
    /**
     * 
     * @param outDir
     * @param kubunMap 歯科の場合、医科の区分名称マップを指定します。
     * 				   医科、調剤の場合はnullを指定します。
     * @param linker
     * @throws IOException
     */
    public 本文(String outDir, Map<String, String> kubunMap, Function<String, String> linker) throws IOException {
        super(outDir);
        this.kubunMap = kubunMap;
        Files.createDirectories(Path.of(outDir));
        this.linker = linker;
    }
    
    static final String 区分末尾の括弧 = "[(（][^()（）]*[)）]$";

    public static void 区分名称マップ(Node node, Map<String, String> map) {
    	if (node.token != null && node.token.type.name.equals("区分番号"))
    		map.put(node.token.header0().replaceFirst(区分末尾の括弧, ""), node.token.number);
    	for (Node child : node.children)
        区分名称マップ(child, map);
    }

    /**
     * 歯科の区分番号から医科の区分番号へのリンクを実現するために、
     * 医科の区分名称から医科の区分番号へのマップを作製します。
     */
    public static Map<String, String> 区分名称マップ(Node root) {
        Map<String, String> map = new HashMap<>();
        区分名称マップ(root, map);
        return map;
    }
	
	String linkText(String text) {
	    return linker.apply(text);
	}

	String linkBodyText(Token token) {
	    return linkText(token.body.stream().collect(Collectors.joining()));
	}
	
	static void beginTuti(TextWriter writer) {
        writer.println("<div id='tuti'>");
        writer.println("<h2 class='title'>通知</h2>");
	}
	
	static void endTuti(TextWriter writer) {
        writer.println("</div>");
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
            lineDirective(token), indent(level, token.number), token.number, linkText(token.header),
            token.body.size() > 0 ? "<br>" : "", linkBodyText(token));
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
			menu("i", writer);
			writer.println("</div>"); // id='breadcrumb'
			writer.println("<p class='title'>%s</p>", paths(node));
			writer.println("<h1 class='title'>%s</h1>", title);
			writer.println("<div id='content'>");
			if (node.isTuti)
			    beginTuti(writer);
			if (node.token != null) {
			    // headerの後半とbodyの出力
                Token token = node.token;
                if (!token.header1().isEmpty())
                    writer.println("<p><b>%s</b></p>", linkText(token.header1()));
                if (token.body.size() > 0)
                    writer.println("<p>%s</p>", linkBodyText(token));
			}
			links.push(new Link(outHtmlFile, title));
			// 歯科の区分番号で記述が空の場合、医科の区分番号へリンクする
			if (kubunMap != null && node.token != null && node.token.type.name.equals("区分番号")
				&& node.token.header1().isBlank() && node.token.body.isEmpty() && node.children.isEmpty()) {
				String name = node.token.header0().replaceFirst(区分末尾の括弧, "");
				String ikaId = kubunMap.get(name);
				if (ikaId == null)
					LOGGER.warning("区分「" + node.token.number + " " + name + "」は医科にありません");
				else
					writer.println("<p>医科点数表 区分 <a href='../i/%s.html'>%s %s</a></p>", Pat.正規化(ikaId), ikaId, name);
			}
			if (!bodyOnly) {
				// 子ノードのレンダリング
				for (Node child : node.children)
					node(child, 0, writer, links);
			}
			// 通知ノードのレンダリング
			if (!node.isTuti && node.tuti != null
				&& (!node.tuti.token.body.stream().allMatch(String::isBlank) || node.tuti.children.size() > 0)) {
			    beginTuti(writer);
                writer.println("<p>%s</p>", linkBodyText(node.tuti.token));
			    for (Node child : node.tuti.children)
                    node(child, 0, writer, links);
			    endTuti(writer);
			}
			if (node.isTuti)
			    endTuti(writer);
			writer.println("</div>"); // id='content'
			writer.println("</div>"); // id='all'
            writer.println("</body>");
            writer.println("</html>");
			links.pop();
        }
    }

    public void render(Node node, String title, String outHtmlFile) throws IOException {
        Deque<Link> links = new LinkedList<>();
        links.add(new Link("../../index.html", "トップ"));
        file(node, title, outHtmlFile, links, false);
    }
}
