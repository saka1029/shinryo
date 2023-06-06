package saka1029.shinryo.parser;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node {
	public final Node parent;
    public final Token token;
    /**
     * id, pathはパース後にユニークにするための更新を行うためfinalではありません。
     */
    public String id;
    public String path;
    public final int level;
    public Node tuti;
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
    	// id, pathはパース後にユニーク化するために更新する点に注意する。
    	String childId = token.id;
    	String childPath = isRoot() ? childId : path + Pat.パス区切り + childId;
    	Node child = new Node(this, token, childId, childPath, level + 1);
    	children.add(child);
    	return child;
    }
    
    void summary(PrintWriter w) {
		if (!isRoot() && token != null) {
			Token t = token;
			w.printf("%s%s%s %s : %s:%d:%d:%d:%d%n", path, "  ".repeat(level),
				t.number, t.header, t.fileName, t.pageNo, t.lineNo, t.indent, t.body.size());
		}
		for (Node child : children)
			child.summary(w);
    }

    public void summary(String outTxtFile) throws IOException {
        Files.createDirectories(Path.of(outTxtFile).getParent());
        try (PrintWriter w = new PrintWriter(outTxtFile)) {
        	summary(w);
        }
    }
    
    @Override
    public String toString() {
        return "Node[path=%s, token=%s]".formatted(path, token);
    }
}
