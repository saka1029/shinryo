package saka1029.shinryo.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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

}
