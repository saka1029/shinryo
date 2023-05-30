package saka1029.shinryo.parser;

import static org.junit.Assert.assertEquals;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class TestNode {

    static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    static final TokenType T = new TokenType("item", "", Pat.固定値id(""));

    static final Token token(String n) {
        return new Token(T, n, "", "", 0, 0, 0);
    }
    
    static String number(Node node) {
    	return node.token == null ? "root" : node.token.number;
    }

    @Test
    public void testIterable() {
        Node root = Node.root();
        Node n1 = root.addChild(token("1"));
        Node n11 = n1.addChild(token("1.1"));
        Node n12 = n1.addChild(token("1.2"));
        Node n121 = n12.addChild(token("1.2.1"));
        Node n13 = n1.addChild(token("1.3"));
        Node n2 = root.addChild(token("2"));
        Iterator<Node> it = root.iterator();
        assertEquals(0, it.next().level);
        assertEquals(1, it.next().level);
        assertEquals(2, it.next().level);
        assertEquals(2, it.next().level);
        assertEquals(3, it.next().level);
        assertEquals(2, it.next().level);
        assertEquals(1, it.next().level);
        Iterator<Node> iu = root.iterator();
        assertEquals("root", number(iu.next()));
        assertEquals("1", number(iu.next()));
        assertEquals("1.1", number(iu.next()));
        assertEquals("1.2", number(iu.next()));
        assertEquals("1.2.1", number(iu.next()));
        assertEquals("1.3", number(iu.next()));
        assertEquals("2", number(iu.next()));
    }

    @Test
    public void testStream() {
        Node root = Node.root();
        Node n1 = root.addChild(token("1"));
        Node n11 = n1.addChild(token("1.1"));
        Node n12 = n1.addChild(token("1.2"));
        Node n121 = n12.addChild(token("1.2.1"));
        Node n13 = n1.addChild(token("1.3"));
        Node n2 = root.addChild(token("2"));
        List<String> result = root.stream()
        	.map(node -> node.level + ":" + number(node))
        	.toList();
        List<String> expected = List.of("0:root", "1:1", "2:1.1", "2:1.2", "3:1.2.1", "2:1.3", "1:2");
        assertEquals(expected, result);
    }
}
