package saka1029.shinryo.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public record Node(
    Token token,
    List<Node> children) {

    public Node(Token token) {
        this(token, new ArrayList<>());
    }

    public void print(Consumer<String> callback) {
        new Object() {
            void print(Node node, int level) {
                if (node.token() == null)
                    callback.accept("ROOT");
                else
                    callback.accept("  ".repeat(level) + node.token());
                for (Node child : node.children())
                    print(child, level + 1);
            }
        }.print(this, 0);
    }
}
