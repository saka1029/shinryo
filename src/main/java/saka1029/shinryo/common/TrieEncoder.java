package saka1029.shinryo.common;

import java.util.HashMap;
import java.util.Map;

public class TrieEncoder<V> {

    static class Node<V> {
        V data; 
        final Map<Integer, Node<V>> children = new HashMap<>();

        public Node(V data) {
            this.data = data;
        }

        public Node<V> addChild(int c, Node<V> child) {
            children.put(c, child);
            return child;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (data != null)
                sb.append(data.toString());
            sb.append("{");
            for (Map.Entry<Integer, Node<V>> e : children.entrySet())
                sb.appendCodePoint(e.getKey()).append(":").append(e.getValue());
            sb.append("}");
            return sb.toString();
        }
    }

    final Node<V> root = new Node<>(null);

    public void put(String word, V data) {
        Node<V> node = root;
        for (int c : word.codePoints().toArray())
            node = node.addChild(c, new Node<V>(null));
        node.data = data;
    } 

    @Override
    public String toString() {
        return String.format("TrieEncoder(%s)", root);
    }
}
