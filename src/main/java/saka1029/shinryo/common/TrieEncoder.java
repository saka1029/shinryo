package saka1029.shinryo.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

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

    public static class Entry<V> {
        public final int start;
        public final int end;
        public final V data;

        public Entry(int start, int end, V data) {
            this.start = start;
            this.end = end;
            this.data = data;
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, end, data);
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof Entry<?> e &&
                start == e.start && end == e.end && Objects.equals(data, e.data);
        }

        @Override
        public String toString() {
            return String.format("Entry(%d, %d, %s)", start, end, data);
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

    public List<List<Entry<V>>> encode(String text, Predicate<List<Entry<V>>> filter) {
        int[] cps = text.codePoints().toArray();
        int length = cps.length;
        List<List<Entry<V>>> result = new ArrayList<>();
        List<Entry<V>> sequence = new ArrayList<>();
        new Object() {
            List<Entry<V>> getFrom(int start) {
                List<Entry<V>> entries = new ArrayList<>();
                Node<V> node = root;
                for (int i = start; i < length; i++) {
                    if ((node = node.children.get(cps[i])) == null)
                        break;
                    V data = node.data;
                    if (data != null)
                        entries.add(new Entry<>(start, i + 1, data));
                }
                return entries;
            }
            void search(int index) {
                if (index >= length && filter.test(sequence))
                    result.add(new ArrayList<>(sequence));
                else
                    for (Entry<V> entry : getFrom(index)) {
                        sequence.add(entry);
                        search(entry.end);
                        sequence.removeLast();
                    }
            }
        }.search(0);
        return result;
    }

    public List<List<Entry<V>>> encode(String text) {
        return encode(text, e -> true);
    }

    @Override
    public String toString() {
        return String.format("TrieEncoder(%s)", root);
    }
}
