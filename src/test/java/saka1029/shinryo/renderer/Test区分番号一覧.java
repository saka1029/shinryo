package saka1029.shinryo.renderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.Param;
import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Parser;
import saka1029.shinryo.parser.医科告示読み込み;
import saka1029.shinryo.parser.歯科告示読み込み;
import saka1029.shinryo.parser.調剤告示読み込み;

public class Test区分番号一覧 {

    static final Logger logger = Common.logger(Test区分番号一覧.class);

    static final Param param = Param.of("in", "debug/html", "04");

//    @Test
    public void test医科() throws IOException {
        String 点数表 = "i";
        String inTxtFile = param.txt(点数表, "ke");
        String outHtmlFile = param.outFile(点数表, "kubun.html");
        String title = param.title(点数表);
        Node root = Parser.parse(new 医科告示読み込み(), false, inTxtFile);
        new 区分番号一覧().render(root, title, outHtmlFile);
    }

//    @Test
    public void test歯科() throws IOException {
        String 点数表 = "s";
        String inTxtFile = param.txt(点数表, "ke");
        String outHtmlFile = param.outFile(点数表, "kubun.html");
        String title = param.title(点数表);
        Node root = Parser.parse(new 歯科告示読み込み(), false, inTxtFile);
        new 区分番号一覧().render(root, title, outHtmlFile);
    }

//    @Test
    public void test調剤() throws IOException {
        String 点数表 = "t";
        String inTxtFile = param.txt(点数表, "ke");
        String outHtmlFile = param.outFile(点数表, "kubun.html");
        String title = param.title(点数表);
        Node root = Parser.parse(new 調剤告示読み込み(), false, inTxtFile);
        new 区分番号一覧().render(root, title, outHtmlFile);
    }
    
    static List<Node> 区分リスト(Node root) {
    	List<Node> list = new ArrayList<>();
    	root.visit(node -> {
    		if (node.token != null && node.token.type.name.equals("区分番号"))
    			list.add(node);
    	});
    	return list;
    }

    static <T> T next(Iterator<T> iterator) {
    	return iterator.hasNext() ? iterator.next() : null;
    }
    
    static void print(Node oldNode, Node newNode, TextWriter writer) throws IOException {
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
    	writer.println("%s %s %s %s", oldStr, newStr, compStr, name);
    }

    static void matching(List<Node> oldList, List<Node> newList, String outTxtFile) throws IOException {
    	try (TextWriter writer = new TextWriter(outTxtFile)) {
    		Iterator<Node> oi = oldList.iterator();
    		Iterator<Node> ni = newList.iterator();
    		Node o = next(oi);
    		Node n = next(ni);
    		while (o != null && n != null) {
    			int c = o.id.compareTo(n.id);
    			if (c == 0) {
    				print(o, n, writer);
    				o = next(oi);
    				n = next(ni);
    			} else if (c < 0) {
    				print(o, null, writer);
    				o = next(oi);
    			} else {
    				print(null, n, writer);
    				n = next(ni);
    			}
    		}
    		for (; o != null; o = next(oi))
				print(o, null, writer);
    		for (; n != null; n = next(ni))
				print(null, n, writer);
    	}
    }

    @Test
    public void test医科比較() throws IOException {
        String 点数表 = "i";
        String outTxtFile = param.outFile("04-i-kubun.txt");
        Node newRoot = Parser.parse(new 医科告示読み込み(), false, param.txt(点数表, "ke"));
        Node oldRoot = Parser.parse(new 医科告示読み込み(), false, param.previous().txt(点数表, "ke"));
        List<Node> newList = 区分リスト(newRoot);
        List<Node> oldList = 区分リスト(oldRoot);
        matching(oldList, newList, outTxtFile);
//        matching(Collections.emptyList(), newList, outTxtFile);
    }

}
