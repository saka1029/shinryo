package saka1029.shinryo.parser;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Node implements Iterable<NodeLevel> {
	public final Node parent;
    public final Token token;
    public final List<Node> children;

    private Node(Node parent, Token token) {
    	this.parent = parent;
    	this.token = token;
        this.children = new ArrayList<>();
    }
    
    public static Node root() {
    	return new Node(null, null);
    }
    
    public Node addChild(Token token) {
    	Objects.requireNonNull(token, "token");
    	Node child = new Node(this, token);
    	children.add(child);
    	return child;
    }
    
    public String id() {
        return token == null ? null : token.id;
    }
    
    public String path() {
    	return parent == null ?  null : parent.parent == null ? id() : parent.path() + "." + id();
    }
    
    /**
     * 自分自身とすべての子（さらにその子も含む）を深さ優先探索で返します。
     */
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
                for (int i = next.node.children.size() - 1; i >= 0; --i)
                    stack.push(new NodeLevel(next.node.children.get(i), next.level + 1));
                return next;
            }
        };
    }
    
    public Stream<NodeLevel> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), 0), false);
    }
    
    public void summary(String outTxtFile) throws IOException {
        try (PrintWriter w = new PrintWriter(outTxtFile)) {
            for (NodeLevel e : this)
				if (e.node.token != null) {
					Token t = e.node.token;
					w.printf("%s%s%s %s : %s:%d:%d:%d%n", e.node.path(), "  ".repeat(e.level),
					    t.number, t.header, t.fileName, t.pageNo, t.lineNo, t.indent);
				}
        }
    }
}
