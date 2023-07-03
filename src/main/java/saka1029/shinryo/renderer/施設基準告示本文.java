package saka1029.shinryo.renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.common.Trie;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Token;

public class 施設基準告示本文 extends HTML {

    static final String PATH_PREFIX = "k";

    final String outDir;
    final Trie<Node> tRef;
    
    public 施設基準告示本文(String outDir, Trie<Node> tRef) throws IOException {
        Files.createDirectories(Path.of(outDir));
        this.outDir = outDir;
        this.tRef = tRef;
    }

    public void link(Node node, int level, TextWriter writer, Deque<Link> links) throws IOException {
        Token token = node.token;
        String title = "%s %s".formatted(token.number, token.header0());
        String url = "%s%s.html".formatted(PATH_PREFIX, node.path);
        writer.println("%s<p %s><a href='%s'>%s</a></p>",
            lineDirective(token), indent(level, token.number), url, title);
        file(node, title, url, links);
    }
    
    public void text(Node node, int level, TextWriter writer, Deque<Link> links, Set<Node> tRefSet) throws IOException {
        Token token = node.token;
        String body = token.body.stream().collect(Collectors.joining());
        writer.println("%s<p %s>%s %s%s%s</p>",
            lineDirective(token), indent(level, token.number), token.number, token.header,
            token.body.size() > 0 ? "<br>" : "", body);
        Map<Integer, List<Node>> ref = tRef.findAll(token.header + body);
        for (List<Node> e : ref.values())
            tRefSet.addAll(e);
        for (Node child : node.children)
            node(child, level + 1, writer, links, tRefSet);
    }
    
    static final List<String> LINKS = List.of("第漢数字", "別表", "別表第");

    void node(Node node, int level, TextWriter writer, Deque<Link> links, Set<Node> tRefSet) throws IOException {
        if (LINKS.contains(node.token.type.name))
            link(node, level, writer, links);
        else
            text(node, level, writer, links, tRefSet);
    }

    void file(Node node, String title, String outHtmlFile, Deque<Link> links) throws IOException {
        try (TextWriter writer = new TextWriter(Path.of(outDir, outHtmlFile))) {
            head(title, node, writer);
			writer.println("<body>");
			writer.println("<div id='all'>");
			// パンくずリスト
			writer.println("<div id='breadcrumb'>");
			String sep = "";
			for (Link link : (Iterable<Link>) () -> links.descendingIterator()) {
			    writer.println("%s<a href='%s'>%s</a>", sep, link.url, link.title);
			    sep = "&gt; ";
			}
			menu("k", writer);
			writer.println("</div>"); // id='breadcrumb'
			writer.println("<p class='title'>%s</p>", paths(node));
			writer.println("<h1 class='title'>%s</h1>", title);
			writer.println("<div id='content'>");
			links.push(new Link(outHtmlFile, title));
			Set<Node> tRefSet = new LinkedHashSet<>();
            // 子ノードのレンダリング
            for (Node child : node.children)
                node(child, 0, writer, links, tRefSet);
            if (!tRefSet.isEmpty()) {
                writer.println("<div id='tuti'>");
                writer.println("<p><b>通知<b></p>");
                for (Node n : tRefSet)
                    writer.println("<p><a href='t%s.html'>%s %s</p>", n.path, n.token.number, n.token.header);
                writer.println("</div>"); // id='tuti'
            }
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
