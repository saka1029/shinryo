package saka1029.shinryo.renderer;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Logging;
import saka1029.shinryo.common.Param;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.調剤告示読み込み;
import saka1029.shinryo.parser.調剤通知読み込み;

public class Testマージ {

    static { Logging.init(); } 
    static final Logger logger = Logger.getLogger(Testマージ.class.getSimpleName());
    static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    
    static final Param param = Param.of("in", "debug/out", "04");

    static final List<String> MERGE_NODE_NAME = List.of("章", "部", "節", "款", "通則");

    static void visit(Node node, BiConsumer<String, Node> visitor) {
		if (node.isRoot()) {
			/* do nothing */;
		} else if (node.token.type.name.equals("区分番号")) {
			if (!node.token.header.equals("削除"))
				visitor.accept(node.id, node);
			return;
		} else if (MERGE_NODE_NAME.contains(node.token.type.name)) {
			visitor.accept(node.path, node);
		}
		for (Node child : node.children)
			visit(child, visitor);
    }

    static Map<String, Node> map(Node root) {
    	Map<String, Node> map = new LinkedHashMap<>();
    	visit(root, (key, node) -> map.put(key, node));
    	return map;
    }
    
    static void merge(Map<String, Node> map, Node root) {
    	visit(root, (key, node) -> {
    		Node knode = map.get(key);
    		if (knode == null)
    			logger.warning("マージ先なし: " + key);
    		else
				knode.tuti = node;
    	});
    }
    
    static void print(Node node) {
    	if (!node.isRoot())
    		if (node.tuti != null)
    			logger.info(node.path + ":" + node.tuti.path);
    	for (Node child : node.children)
    		print(child);
    }

	@Test
	public void test調剤マージ() throws IOException {
        logger.info(Logging.methodName());
        Node kroot = new 調剤告示読み込み().parse(param.txt("t", "ke"));
        Map<String, Node> kmap = map(kroot);
        logger.info("*** 調剤告示マップ:");
        for (Entry<String, Node> e : kmap.entrySet())
        	logger.info(e.getValue().token.type.name + " " + e.getKey());
        Node troot = new 調剤通知読み込み().parse(param.txt("t", "te"));
        merge(kmap, troot);
//        Map<String, Node> tmap = map(troot);
//        logger.info("*** 調剤通知マップ:");
//        for (Entry<String, Node> e : tmap.entrySet())
//        	logger.info(e.getValue().token.type.name + " " + e.getKey());
        logger.info("*** 調剤マージ:");
        print(kroot);
	}

}
