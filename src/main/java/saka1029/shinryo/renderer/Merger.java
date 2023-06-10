package saka1029.shinryo.renderer;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import saka1029.shinryo.parser.Node;

public class Merger {

    static final Logger logger = Logger.getLogger(Merger.class.getSimpleName());

    static final Set<String> MERGE_NODE_NAME = Set.of("章", "部", "節", "款", "通則", "区分番号");
    static final Set<String> BASE_NODE_NAME = Set.of("章", "部", "節", "款");

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
     * 2_8_3   kMap("2_8")  kMap("2_8_2")の後
     * 3_t     kMap("3")    0
     * </pre>
     */
    static void add(Map<String, Node> kMap, String id, Node tNode) {
        if (tNode.token.type.name.equals("区分番号")) {
            logger.severe("通知の区分番号「" + tNode.token.header + "」が告示にありません");
            return;
        }
        logger.info("通知パス「" + tNode.path + "」に対応する告示Nodeを追加します");
        Node tParent = tNode.parent;
        Node kParent = tParent.isRoot() ? kMap.get("") : kMap.get(tParent.path);
        if (kParent == null) {
            logger.warning("通知パス「" + tNode.path + "」に対応する告示の親が見つかりません");
            return;
        }
        // 告示の親Nodeに子を追加します。
//        Node kNode = kParent.addChild(tNode.id, tNode.path, tNode);
        Node kNode = tNode;
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
        // 親の適切な位置に子を追加します。
        kParent.children.add(index, kNode);
    }

    static void referCopy(Node kNode, Node node) {
        Node copy = node.copy();
        copy.isTuti = true;
        for (Iterator<Node> it = copy.children.iterator(); it.hasNext(); )
            if (MERGE_NODE_NAME.contains(it.next().token.type.name))
                it.remove();
        if (copy.token.body.size() > 0 || copy.children.size() > 0)
            kNode.tuti = copy;
    }

    static void refer(Node kNode, Node node) {
        if (!BASE_NODE_NAME.contains(node.token.type.name))
            kNode.tuti = node;
        else
            referCopy(kNode, node);
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
                            add(kMap, k, node);
                        else
                            refer(kn, node);
                    }
                } else
                    add(kMap, id, node);
            } else
                refer(kNode, node);
        });
    }

//    public static void print(Node node, Consumer<String> visitor) {
//        if (!node.isRoot() && node.tuti != null)
//            visitor.accept(node.tuti.token.type.name + " " + node.path + ":" + node.tuti.path);
//        for (Node child : node.children)
//            print(child, visitor);
//    }

    public static void merge(Node kRoot, Node tRoot) {
        Map<String, Node> kMap = map(kRoot);
        merge(kMap, tRoot);
    }

}
