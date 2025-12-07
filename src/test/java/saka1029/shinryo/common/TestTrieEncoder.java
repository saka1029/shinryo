package saka1029.shinryo.common;

import org.junit.Test;

public class TestTrieEncoder {

    @Test
    public void testTrieEncoder() {
        TrieEncoder<String> trie = new TrieEncoder<>();
        String[] words = {"a", "b", "c", "ab", "bc", "abc"};
        for (String s : words)
            trie.put(s, s.toUpperCase());
        System.out.println(trie);
    }

}
