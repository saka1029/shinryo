package saka1029.shinryo.renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Token;

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

	static void print(Node oldNode, Node newNode, String 旧年度, TextWriter writer) throws IOException {
		String oldStr = "旧";
		String newStr = "新";
		String compStr = "比較";
		String name = newNode.token.number + " " + newNode.token.header0();
		if (oldNode == null || oldNode.token.header0().equals("削除"))
			oldStr = "ー";
		if (newNode == null || newNode.token.header0().equals("削除"))
			newStr = "ー";
		if (oldStr.equals("ー") || newStr.equals("ー"))
			compStr = "ーー";
		if (!oldStr.equals("ー") && newStr.equals("ー"))
			name = oldNode.token.number + " " + oldNode.token.header0();
		String oldLink = oldStr;
		writer.println("%s %s %s %s", oldStr, newStr, compStr, name);
	}

	static <T> T next(Iterator<T> iterator) {
		return iterator.hasNext() ? iterator.next() : null;
	}

	static void matching(List<Node> oldList, List<Node> newList, String 旧年度, TextWriter writer) throws IOException {
		Iterator<Node> oi = oldList.iterator();
		Iterator<Node> ni = newList.iterator();
		Node o = next(oi);
		Node n = next(ni);
		while (o != null && n != null) {
			int c = o.id.compareTo(n.id);
			if (c == 0) {
				print(o, n, 旧年度, writer);
				o = next(oi);
				n = next(ni);
			} else if (c < 0) {
				print(o, null, 旧年度, writer);
				o = next(oi);
			} else {
				print(null, n, 旧年度, writer);
				n = next(ni);
			}
		}
		for (; o != null; o = next(oi))
			print(o, null, 旧年度, writer);
		for (; n != null; n = next(ni))
			print(null, n, 旧年度, writer);
	}

	public void render(Node oldRoot, Node newRoot, String title, String 旧年度, String outHtmlFile) throws IOException {
		String fullTitle = title + " 区分番号一覧";
		List<Node> oldList = listKubun(oldRoot);
		List<Node> newList = listKubun(newRoot);
		try (TextWriter writer = new TextWriter(outHtmlFile)) {
			head(fullTitle, newRoot, writer);
			writer.println("<body>");
			// パンくずリスト
			writer.println("<div id='breadcrumb'>");
			writer.println("<a href='../../index.html'>トップ</a>");
			menu(writer);
			writer.println("</div>");
			writer.println("<h1 class='title'>%s</h1>", fullTitle);
			writer.println("<ul>");
			matching(oldList, newList, 旧年度, writer);
			writer.println("</ul>");
			writer.println("</body>");
			writer.println("</html>");
		}
	}

	public void render(Node root, String title, String outHtmlFile) throws IOException {
		String fullTitle = title + " 区分番号一覧";
		Files.createDirectories(Path.of(outHtmlFile).getParent());
		List<Node> list = listKubun(root);
		try (TextWriter writer = new TextWriter(outHtmlFile)) {
			head(fullTitle, root, writer);
			writer.println("<body>");
			// パンくずリスト
			writer.println("<div id='breadcrumb'>");
			writer.println("<a href='../../index.html'>トップ</a>");
			menu(writer);
			writer.println("</div>");
			writer.println("<h1 class='title'>%s</h1>", fullTitle);
			writer.println("<ul>");
			for (Node kubun : list) {
				Token token = kubun.token;
				if (token.header.equals("削除"))
					writer.println("%s<li>%s %s</li>", lineDirective(token), token.number, token.header0());
				else
					writer.println("%s<li><a href='%s.html'>%s %s</a></li>", lineDirective(token), kubun.id,
							token.number, token.header0());
			}
			writer.println("</ul>");
			writer.println("</body>");
			writer.println("</html>");
		}
	}
}
