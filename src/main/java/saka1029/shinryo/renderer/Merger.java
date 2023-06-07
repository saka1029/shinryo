package saka1029.shinryo.renderer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import saka1029.shinryo.parser.Node;

public class Merger {

    static final Logger logger = Logger.getLogger(Merger.class.getSimpleName());

    static final List<String> MERGE_NODE_NAME = List.of("章", "部", "節", "款", "通則", "区分番号");

    /**
     * Nodeを再帰的にトラバースし、MERGE_NODE_NAMEに含まれるNodeを visitorに渡します。
     */
    static void visit(Node node, BiConsumer<String, Node> visitor) {
        if (!node.isRoot() && MERGE_NODE_NAME.contains(node.token.type.name) && !node.token.header.equals("削除"))
            if (node.token.type.name.equals("区分番号")) {
                visitor.accept(node.id, node); // 区分番号の時はpathではなくidで処理する。
                return; // 区分番号のときは下位ノードを検索しない。
            } else
                visitor.accept(node.path, node);
        for (Node child : node.children)
            visit(child, visitor);
    }

    /**
     * Nodeを再帰的にトラバースし、MERGE_NODE_NAMEに含まれるNodeの マップを作成します。
     */
    static Map<String, Node> map(Node root) {
        Map<String, Node> map = new LinkedHashMap<>();
        map.put("", root); // ルートNodeは""に関連付けます。
        visit(root, (key, node) -> map.put(key, node));
        return map;
    }

    /**
     * kMap(告示Map)にnode(通知Node)が存在しない場合に、 kMapに新たなNodeを追加します。
     * <pre>
     * tPath   追加先       追加位置
     * ------  -----------  --------------
     * t       kMap("")     0
     * 2_t     kMap("2")    0
     * 2_8_3   kMap("2_8")  kMap("2_8_2")
     * 3_t     kMap("3")    0
     * </pre>
     */
    static void newNode(Map<String, Node> kMap, String id, Node tNode) {
        Node tParent = tNode.parent;
        Node kParent = tParent.isRoot() ? kMap.get("") : kMap.get(tParent.path);
        if (kParent == null) {
            logger.warning("告示の親「" + tNode.path + "」が見つかりません");
            return;
        }
        // 告示の親Nodeに子を追加します。
        Node kNode = kParent.addChild(tNode.id, tNode.path, tNode);
        int index = 0;
        if (tNode.id.matches("\\d+")) {
            String prevId = Integer.toString(Integer.parseInt(tNode.id) - 1);
            OptionalInt prevIndex = IntStream.range(0, kParent.children.size())
                .filter(i -> kParent.children.get(i).id.equals(prevId))
                .findFirst();
            if (prevIndex.isPresent())
                index = prevIndex.getAsInt() + 1;
            else {
                logger.warning("告示の追加位置「" + tNode.path + "」が見つかりません");
                return;
            }
        }
        kParent.children.add(index, kNode);
//        logger.warning("マージ先なし: " + id);
    }

    /**
     * Nodeを再帰的にトラバースし、MERGE_NODE_NAMEに含まれるNodeを マップの対応するノードに通知としてマージします。
     */
    static void merge(Map<String, Node> kMap, Node tRoot) {
        visit(tRoot, (id, node) -> {
            Node kNode = kMap.get(id);
            if (kNode == null) {
                if (id.contains("x")) {
                    for (String k : id.split("x")) {
                        Node kn = kMap.get(k);
                        if (kn == null)
                            newNode(kMap, k, node);
//                                logger.warning("マージ先なし: " + k);
                        else
                            kn.tuti = node;
                    }
                } else
                    newNode(kMap, id, node);
//                        logger.warning("マージ先なし: " + id);
            } else
                kNode.tuti = node;
        });
    }

    public static void print(Node node, Consumer<String> visitor) {
        if (!node.isRoot() && node.tuti != null)
            visitor.accept(node.tuti.token.type.name + " " + node.path + ":" + node.tuti.path);
        for (Node child : node.children)
            print(child, visitor);
    }

    public static void merge(Node kRoot, Node tRoot) {
        Map<String, Node> kMap = map(kRoot);
        merge(kMap, tRoot);
    }

}
