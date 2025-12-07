package saka1029.shinryo.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class HashEncoder<V> {

    final Map<String, V> map = new HashMap<>();

    public void put(String key, V value) {
        map.put(key, value);
    }

    public V get(String key) {
        return map.get(key);
    }

    public record Entry<V>(int start, int end, V data) {
    }

    public List<List<Entry<V>>> encode(String text, Predicate<List<Entry<V>>> filter) {
        int length = text.length();
        List<List<Entry<V>>> result = new ArrayList<>();
        List<Entry<V>> sequence = new ArrayList<>();
        new Object() {
            void search(int start) {
                System.out.printf("start=%d%n", start);
                if (start >= length) {
                    if(filter.test(sequence))
                        result.add(new ArrayList<>(sequence));
                } else
                    for (int i = start + 1; i <= length; ++i) {
                        V data = map.get(text.substring(start, i));
                        // System.out.printf("start=%d,i=%d,word=%s, data=%s%n", start, i, word, data);
                        if (data == null)
                            continue;
                        sequence.add(new Entry<V>(start, i, data));
                        search(i);
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
        return String.format("HashEncoder%s", map);
    }

}
