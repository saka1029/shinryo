package saka1029.shinryo.parser;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
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
        Node root =
            new Node(token("root"), List.of(
                new Node(token("1"), List.of(
                    new Node(token("1.1")),
                    new Node(token("1.2"), List.of(
                        new Node(token("1.2.1")))),
                    new Node(token("1.3")))),
                new Node(token("2"))));
        OUT.println(root);
        for (NodeLevel e : root)
            OUT.println(e.level() + ":" + e.node().token().number());
    }

    @Test
    public void testStream() {
        Node root =
            new Node(token("root"), List.of(
                new Node(token("1"), List.of(
                    new Node(token("1.1")),
                    new Node(token("1.2"), List.of(
                        new Node(token("1.2.1")))),
                    new Node(token("1.3")))),
                new Node(token("2"))));
        OUT.println(root);
        root.stream().forEach(e ->
            OUT.println(e.level() + ":" + e.node().token().number()));
    }

}
