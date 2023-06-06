package saka1029.shinryo.renderer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;

import saka1029.shinryo.parser.Node;

public class Merger {
	
	static final Logger logger = Logger.getLogger(Merger.class.getSimpleName());
	
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
    
    public static void print(Node node, Consumer<String> visitor) {
    	if (!node.isRoot() && node.tuti != null)
			visitor.accept(node.token.type.name + " " + node.path + ":" + node.tuti.path);
    	for (Node child : node.children)
    		print(child, visitor);
    }
    
    public static void merge(Node kRoot, Node tRoot) {
    	Map<String, Node> kMap = map(kRoot);
    	merge(kMap, tRoot);
    }

}
