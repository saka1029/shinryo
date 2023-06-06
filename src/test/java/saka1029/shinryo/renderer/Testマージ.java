package saka1029.shinryo.renderer;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.Param;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Parser;
import saka1029.shinryo.parser.医科告示読み込み;
import saka1029.shinryo.parser.医科通知読み込み;
import saka1029.shinryo.parser.調剤告示読み込み;
import saka1029.shinryo.parser.調剤通知読み込み;

public class Testマージ {

    static final Logger logger = Common.logger(Testマージ.class);
    
    static final Param param = Param.of("in", "debug/out", "04");

    static final List<String> MERGE_NODE_NAME = List.of("章", "部", "節", "款", "通則", "区分番号");

    /**
     * Nodeを再帰的にトラバースし、MERGE_NODE_NAMEに含まれるNodeを
     * visitorに渡します。 
     */
    static void visit(Node node, BiConsumer<String, Node> visitor) {
		if (!node.isRoot() && MERGE_NODE_NAME.contains(node.token.type.name) && !node.token.header.equals("削除"))
			if (node.token.type.name.equals("区分番号")) {
				visitor.accept(node.id, node);	// 区分番号の時はpathではなくidで処理する。
				return;	// 区分番号のときは下位ノードを検索しない。
			} else
				visitor.accept(node.path, node);
		for (Node child : node.children)
			visit(child, visitor);
    }

    /**
     * Nodeを再帰的にトラバースし、MERGE_NODE_NAMEに含まれるNodeの
     * マップを作成します。
     */
    static Map<String, Node> map(Node root) {
    	Map<String, Node> map = new LinkedHashMap<>();
    	visit(root, (key, node) -> map.put(key, node));
    	return map;
    }
    
    /**
     * Nodeを再帰的にトラバースし、MERGE_NODE_NAMEに含まれるNodeを
     * マップの対応するノードに通知としてマージします。
     */
    static void merge(Map<String, Node> map, Node root) {
    	visit(root, (key, node) -> {
    		Node knode = map.get(key);
    		if (knode == null) {
    			if (key.contains("x")) {
    				String[] keys = key.split("x");
    				for (String k : keys) {
    					Node kn = map.get(k);
    					if (kn == null)
							logger.warning("マージ先なし: " + kn);
    					else
    						kn.tuti = node;
    				}
    			} else
					logger.warning("マージ先なし: " + key);
    		} else
				knode.tuti = node;
    	});
    }
    
    static void print(Node node, Consumer<String> visitor) {
    	if (!node.isRoot() && node.tuti != null)
			visitor.accept(node.token.type.name + " " + node.path + ":" + node.tuti.path);
    	for (Node child : node.children)
    		print(child, visitor);
    }

    /**
     * 2023-06-06 16:48:25.183 Testマージ INFO: test医科マージ
     * 2023-06-06 16:48:26.741 Testマージ WARNING: マージ先なし: t        ルートの子供の先頭
     * 2023-06-06 16:48:26.744 Testマージ WARNING: マージ先なし: 2_t      第２章の子供の先頭
     * 2023-06-06 16:48:26.749 Testマージ WARNING: マージ先なし: 2_8_3    第２章第８部第２節の次
     * 2023-06-06 16:48:26.751 Testマージ WARNING: マージ先なし: 3_t      第３章の子供の先頭
     */
	@Test
	public void test医科マージ() throws IOException {
        logger.info(Common.methodName());
        Node kroot = Parser.parse(new 医科告示読み込み(), false, param.txt("i", "ke"));
        Map<String, Node> kmap = map(kroot);
//        logger.info("*** 医科告示マップ:");
//        for (Entry<String, Node> e : kmap.entrySet())
//        	logger.info(e.getValue().token.type.name + " " + e.getKey());
        Node troot = Parser.parse(new 医科通知読み込み(), false, param.txt("i", "te"));
        merge(kmap, troot);
        try (PrintWriter writer = new PrintWriter(param.outFile("i-merge.txt"), StandardCharsets.UTF_8)) {
			print(kroot, writer::println);
        }
	}

	@Test
	public void test調剤マージ() throws IOException {
        logger.info(Common.methodName());
        Node kroot = Parser.parse(new 調剤告示読み込み(), false, param.txt("t", "ke"));
        Map<String, Node> kmap = map(kroot);
//        logger.info("*** 調剤告示マップ:");
//        for (Entry<String, Node> e : kmap.entrySet())
//        	logger.info(e.getValue().token.type.name + " " + e.getKey());
        Node troot = Parser.parse(new 調剤通知読み込み(), false, param.txt("t", "te"));
        merge(kmap, troot);
        try (PrintWriter writer = new PrintWriter(param.outFile("t-merge.txt"), StandardCharsets.UTF_8)) {
			print(kroot, writer::println);
        }
	}

}
