package saka1029.shinryo.parser;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public record Node(
    Token token,
    List<Node> children) implements Iterable<NodeLevel> {

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

    @Override
    public Iterator<NodeLevel> iterator() {
        Deque<NodeLevel> stack = new LinkedList<>();
        stack.push(new NodeLevel(this, 0));
        return new Iterator<>() {

            @Override
            public boolean hasNext() {
                return !stack.isEmpty();
            }

            @Override
            public NodeLevel next() {
                NodeLevel next = stack.pop();
                for (int i = next.node().children().size() - 1; i >= 0; --i)
                    stack.push(new NodeLevel(next.node().children().get(i), next.level() + 1));
                return next;
            }
        };
    }
    
    public Stream<NodeLevel> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), 0), false);
    }
}
