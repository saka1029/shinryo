package saka1029.shinryo.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * メモリを節約するためにノードごとにマップを持たないトライ木の実装です。
 * マップ(Map<Long, Node>)のキーは上位32ビットがノードの一連番号で、
 * 下位32ビットが子ノードの先頭文字(Character)です。
 *
 * @param <V> 単語に関連付けて記憶するオブジェクトの型を指定します。
 */
public class Trie<V> {

    static final long INC_NODE_NO = 1L << Integer.SIZE;

    private long nextNodeNo = 0;
    private Node root = new Node();
    private final Map<Long, Node> nodes = new HashMap<>();

    public int size() {
        return nodes.size();
    }

    /**
     * 単語とそれに対応する値を追加します。
     */
    public void put(String word, V data) {
        Node node = root;
        for (int i = 0, len = word.length(); i < len; ++i)
            node = node.put(word.charAt(i));
        node.data = data;
    }

    /**
     * 単語に対応する値を返します。
     */
    public V get(String s) {
        Node node = root;
        for (int i = 0, length = s.length(); i < length; ++i)
            if ((node = node.get(s.charAt(i))) == null)
                return null;
        return node.data;
    }

    /**
     * 文字列textのstart位置から一致する単語をすべて見つけます。
     *
     * @return 一致した単語に対応する値のリストを返します。
     */
    public List<V> findPrefix(String text, int start) {
        List<V> result = new ArrayList<>();
        Node node = root;
        for (int i = start, length = text.length(); i < length; ++i) {
            if ((node = node.get(text.charAt(i))) == null)
                break;
            V v = node.data;
            if (v != null)
                result.add(v);
        }
        return result;
    }

    /**
     * 文字列textの先頭に一致する単語をすべて見つけます。
     *
     * @return 見つかった単語に対応する値のリストを返します。
     */
    public List<V> findPrefix(String text) {
        return findPrefix(text, 0);
    }

    /**
     * 文字列textに含まれるすべての単語を見つけます。
     *
     * @return 見つかった先頭位置と見つかった単語に対応する値のリストのマップを返します。
     */
    public Map<Integer, List<V>> findAll(String text) {
        Map<Integer, List<V>> result = new HashMap<>();
        for (int i = 0, length = text.length(); i < length; ++i)
            for (V v : findPrefix(text, i))
                result.computeIfAbsent(i, k -> new ArrayList<>()).add(v);
        return result;
    }

    class Node {
        final long no;
        V data;

        Node() {
            this.no = nextNodeNo;
            nextNodeNo += INC_NODE_NO;
        }

        Node get(int key) {
            return nodes.get(no | key);
        }

        Node put(int key) {
            return nodes.computeIfAbsent(no | key, k -> new Node());
        }
    }

}