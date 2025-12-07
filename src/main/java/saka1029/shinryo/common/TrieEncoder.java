package saka1029.shinryo.common;

import java.util.HashMap;
import java.util.Map;

public class TrieEncoder<V> {

    static class Node<V> {
        V data = null; 
        final Map<Integer, Node<V>> children = new HashMap<>();

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (data != null)
                sb.append(data);
            sb.append("{");
            for (Map.Entry<Integer, Node<V>> e : children.entrySet())
                sb.appendCodePoint(e.getKey()).append(":").append(e.getValue());
            sb.append("}");
            return sb.toString();
        }
    }

    final Node<V> root = new Node<>();

    public void put(String word, V data) {
        Node<V> node = root;
        for (int c : word.codePoints().toArray())
            node = node.children.computeIfAbsent(c, k -> new Node<>());
        node.data = data;
    } 

    public V get(String word) {
        Node<V> node = root;
        for (int c : word.codePoints().toArray()) {
            node = node.children.get(c);
            if (node == null)
                return null;
        }
        return node.data;
    }

    @Override
    public String toString() {
        return String.format("TrieEncoder(%s)", root);
    }
}
