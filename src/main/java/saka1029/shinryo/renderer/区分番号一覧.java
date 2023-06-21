package saka1029.shinryo.renderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;

public class 区分番号一覧 extends HTML {

    List<Node> listKubun(Node root) {
        List<Node> list = new ArrayList<>();
        if (root != null)
            root.visit(node -> {
                if (node.token != null && node.token.type.name.equals("区分番号"))
                    list.add(node);
            });
        return list;
    }

    public void render(Node oldRoot, Node newRoot, String title, String 点数表, String 年度, String 旧年度, String outHtmlFile) throws IOException {
		String fullTitle = title + " 区分番号一覧";
		List<Node> oldList = listKubun(oldRoot);
		List<Node> newList = listKubun(newRoot);
		try (TextWriter writer = new TextWriter(outHtmlFile)) {
		    new Object() {
                <T> T next(Iterator<T> iterator) {
                    return iterator.hasNext() ? iterator.next() : null;
                }

		        void print(Node oldNode, Node newNode) {
                    String oldStr = "旧";
                    String newStr = "新";
                    String compStr = "比較";
                    String name = newNode.token.number + " " + newNode.token.header0();
                    if (oldNode == null || oldNode.token.header0().equals("削除"))
                        oldStr = "－";
                    if (newNode == null || newNode.token.header0().equals("削除"))
                        newStr = "－";
                    if (oldStr.equals("－") || newStr.equals("－"))
                        compStr = "－－";
                    if (!oldStr.equals("－") && newStr.equals("－"))
                        name = oldNode.token.number + " " + oldNode.token.header0();
                    String oldLink;
                    if (!oldStr.equals("－"))
                        oldLink = "%s<a href='../../%s/%s/%s.html'>%s</a>".formatted(lineDirective(oldNode.token), 旧年度, 点数表, oldNode.id, oldStr);
                    else
                        oldLink = oldStr;
                    String newLink;
                    if (!newStr.equals("－"))
                        newLink = "%s<a href='%s.html'>%s</a>".formatted(lineDirective(newNode.token), newNode.id, newStr);
                    else
                        newLink = newStr;
                    String compLink;
                    if (!compStr.equals("－－"))
                        compLink = "<a href='../../hikaku.html?l=%s/%s/%s.html&r=%s/%s/%s.html'>%s</a>".formatted(
                            旧年度, 点数表, oldNode.id, 年度, 点数表, newNode.id, compStr);
                    else
                        compLink = compStr;
                    writer.println("<p style='margin-left:5.5em;text-indent:-5.5em'>%s %s %s %s</p>", oldLink, newLink, compLink, name);
		        }

		        void matching() {
                    Iterator<Node> oi = oldList.iterator();
                    Iterator<Node> ni = newList.iterator();
                    Node o = next(oi);
                    Node n = next(ni);
                    while (o != null && n != null) {
                        int c = o.id.compareTo(n.id);
                        if (c == 0) {
                            print(o, n);
                            o = next(oi);
                            n = next(ni);
                        } else if (c < 0) {
                            print(o, null);
                            o = next(oi);
                        } else {
                            print(null, n);
                            n = next(ni);
                        }
                    }
                    for (; o != null; o = next(oi))
                        print(o, null);
                    for (; n != null; n = next(ni))
                        print(null, n);
		        }

		        void render() {
                    head(fullTitle, newRoot, writer);
                    writer.println("<body>");
                    // パンくずリスト
                    writer.println("<div id='breadcrumb'>");
                    writer.println("<a href='../../index.html'>トップ</a>");
                    menu(writer);
                    writer.println("</div>");
                    writer.println("<h1 class='title'>%s</h1>", fullTitle);
//                    writer.println("<ul>");
                    matching();
//                    writer.println("</ul>");
                    writer.println("</body>");
                    writer.println("</html>");
		        }
		    }.render();
		}
	}
}
