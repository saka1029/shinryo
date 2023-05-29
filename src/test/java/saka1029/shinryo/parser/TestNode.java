package saka1029.shinryo.parser;

import static org.junit.Assert.assertEquals;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class TestNode {

    static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    static final TokenType T = new TokenType("item", "");

    static final Token token(String n) {
        return new Token(T, n, "", "", 0, 0, 0);
    }

    @Test
    public void testIterable() {
        Node root = new Node(token("root"), List.of(
            new Node(token("1"), List.of(
                new Node(token("1.1")),
                new Node(token("1.2"), List.of(
                    new Node(token("1.2.1")))),
                new Node(token("1.3")))),
            new Node(token("2"))));
        Iterator<NodeLevel> it = root.iterator();
        assertEquals(0, it.next().level());
        assertEquals(1, it.next().level());
        assertEquals(2, it.next().level());
        assertEquals(2, it.next().level());
        assertEquals(3, it.next().level());
        assertEquals(2, it.next().level());
        assertEquals(1, it.next().level());
        Iterator<NodeLevel> iu = root.iterator();
        assertEquals("root", iu.next().node().token().number());
        assertEquals("1", iu.next().node().token().number());
        assertEquals("1.1", iu.next().node().token().number());
        assertEquals("1.2", iu.next().node().token().number());
        assertEquals("1.2.1", iu.next().node().token().number());
        assertEquals("1.3", iu.next().node().token().number());
        assertEquals("2", iu.next().node().token().number());
    }

    @Test
    public void testStream() {
        Node root = new Node(token("root"), List.of(
            new Node(token("1"), List.of(
                new Node(token("1.1")),
                new Node(token("1.2"), List.of(
                    new Node(token("1.2.1")))),
                new Node(token("1.3")))),
            new Node(token("2"))));
        List<String> result = root.stream().map(e -> e.level() + ":" + e.node().token().number()).toList();
        List<String> expected = List.of("0:root", "1:1", "2:1.1", "2:1.2", "3:1.2.1", "2:1.3", "1:2");
        assertEquals(expected, result);
    }
}
