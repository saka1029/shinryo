package saka1029.shinryo.parser;

import java.util.ArrayList;
import java.util.List;

public record Node(
    Token token,
    List<Node> children) {
    
    public Node(Token token) {
        this(token, new ArrayList<>());
    }
    
    public static Node root(String name) {
    	return new Node(Token.root(name));
    }

}
