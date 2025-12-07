package saka1029.shinryo.common;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import saka1029.shinryo.common.TrieEncoder.Entry;

public class TestTrieEncoder {

    TrieEncoder<String> trie = new TrieEncoder<>();
    {
        String[] words = {"a", "b", "c", "ab", "bc", "abc"};
        for (String s : words)
            trie.put(s, s.toUpperCase());
    }

    @Test
    public void testTrieEncoder() {
        assertEquals("TrieEncoder({a:A{b:AB{c:ABC{}}}b:B{c:BC{}}c:C{}})", trie.toString());
    }

    @Test
    public void testGet() {
        assertEquals("A", trie.get("a"));
        assertEquals("AB", trie.get("ab"));
        assertEquals("ABC", trie.get("abc"));
        assertEquals("B", trie.get("b"));
        assertEquals("BC", trie.get("bc"));
        assertEquals("C", trie.get("c"));
        assertEquals(null, trie.get("NO DATA"));
    }

    @Test
    public void testEncode() {
        String text = "abc";
        List<List<Entry<String>>> result = trie.encode(text);
        assertEquals(
            List.of(
                List.of(
                    new Entry<String>(0, 1, "A"),
                    new Entry<String>(1, 2, "B"),
                    new Entry<String>(2, 3, "C")),
                List.of(
                    new Entry<String>(0, 1, "A"),
                    new Entry<String>(1, 3, "BC")),
                List.of(
                    new Entry<String>(0, 2, "AB"),
                    new Entry<String>(2, 3, "C")),
                List.of(
                    new Entry<>(0, 3, "ABC"))
            ),
        result);
    }
}
