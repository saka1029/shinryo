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

public class Node implements Iterable<Node> {
	public final Node parent;
    public final Token token;
    public final String id;
    public final String path;
    public final int level;
    public final List<Node> children;

    private Node(Node parent, Token token, String id, String path, int level) {
    	this.parent = parent;
    	this.token = token;
    	this.id = id;
    	this.path = path;
    	this.level = level;
        this.children = new ArrayList<>();
    }
    
    public static Node root() {
    	return new Node(null, null, null, null, 0);
    }
    
    public boolean isRoot() {
        return parent == null;
    }
    
    public Node addChild(Token token) {
    	Objects.requireNonNull(token, "token");
    	String childId = token.id;
    	String childPath = isRoot() ? childId : path + Pat.パス区切り + childId;
    	Node child = new Node(this, token, childId, childPath, level + 1);
    	children.add(child);
    	return child;
    }
    
    /**
     * 自分自身とすべての子（さらにその子も含む）を深さ優先探索で返します。
     */
    @Override
    public Iterator<Node> iterator() {
        Deque<Node> stack = new LinkedList<>();
        stack.push(this);
        return new Iterator<>() {

            @Override
            public boolean hasNext() {
                return !stack.isEmpty();
            }

            @Override
            public Node next() {
                Node next = stack.pop();
                for (int i = next.children.size() - 1; i >= 0; --i)
                    stack.push(next.children.get(i));
                return next;
            }
        };
    }
    
    public Stream<Node> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), 0), false);
    }
    
    public void summary(String outTxtFile) throws IOException {
        try (PrintWriter w = new PrintWriter(outTxtFile)) {
            for (Node node : this)
				if (!node.isRoot()) {
					Token t = node.token;
					w.printf("%s%s%s %s : %s:%d:%d:%d:%d%n", node.path, "  ".repeat(node.level),
					    t.number, t.header, t.fileName, t.pageNo, t.lineNo, t.indent, t.body.size());
				}
        }
    }
}
