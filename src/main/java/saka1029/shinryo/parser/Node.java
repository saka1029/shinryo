package saka1029.shinryo.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public record Node(
    Token token,
    List<Node> children) {

    public Node(Token token) {
        this(token, new ArrayList<>());
    }

    public void visit(BiConsumer<Node, Integer> callback) {
    	new Object() {
    		void visit(Node node, int level) {
    			callback.accept(node, level);
    			for (Node child : node.children)
    				visit(child, level + 1);
    		}
    	}.visit(this, 0);
    }
}
