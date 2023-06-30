package saka1029.shinryo.common;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.Test;

public class TestTrie {

    static final Logger LOGGER = Common.logger(TestTrie.class);

    @Test
    public void testTrie() {
        LOGGER.info(Common.methodName());
        String[] words = {"A", "to", "tea", "ted", "ten", "i", "in", "inn"};
        Trie<String> trie = new Trie<>();
        for (String s : words)
            trie.put(s, s);
        for (String s : words)
            assertEquals(s, trie.get(s));
        assertEquals(null, trie.get("NO DATA"));
        assertEquals(List.of("i", "in", "inn"), trie.findPrefix("inn"));
        assertEquals(10, trie.size());
        Map<Integer, List<String>> map = trie.findAll("tea inn ted");
        assertEquals(Map.of(0, List.of("tea"), 4, List.of("i", "in", "inn"), 8, List.of("ted")), map);
    }
}
