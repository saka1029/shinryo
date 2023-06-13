package saka1029.shinryo.parser;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class Parser {
	static final Logger logger = Logger.getLogger(Parser.class.getSimpleName());

	final boolean isTuti;
	List<Token> tokens;
	int max;
	int index = 0;
	Token token, eaten = null;
	/**
	 * パース後にcheckSequence()を行うかどうかを指定します。
	 */
	public boolean check = true;
	
	/**
	 * @param isTuti 告示パーサの場合falseを、通知パーサの場合trueを指定します。
	 */
	public Parser(boolean isTuti) {
	    this.isTuti = isTuti;
	}
	
	public static Node parse(Parser parser, boolean check, String inTxtFile) throws IOException {
		parser.check = check;
		return parser.parse(inTxtFile);
	}

	ParseException error(String format, Object... args) {
		return new ParseException(format.formatted(args));
	}
	
	Token get() {
		return index < max ? tokens.get(index++) : null;
	}
	
	boolean is(TokenType expected) {
		if (token == null)
			return false;
		return token.type == expected;
	}
	
	boolean isChild(Node parent, TokenType expected) {
		if (token == null)
			return false;
		return token.indent > parent.token.indent && token.type == expected;
	}

	boolean eat(TokenType expected) {
		if (token == null)
			return false;
		if (token.type == expected) {
			eaten = token;
			token = get();
			return true;
		}
		return false;
	}
	
	Node add(Node parent, Token token) {
		return parent.addChild(token);
	}
	
	public abstract List<TokenType> types();
	public abstract void parse(Node parent);
	
	public Node parse(List<Token> tokens) {
		this.tokens = tokens;
		this.max = tokens.size();
		this.token = get();
		Node root = Node.root(isTuti);
		parse(root);
	    if (token != null)
            logger.warning("未処理のトークン: " + tokens.get(index - 1));
		return root;
	}
	
    static void makeUniqId(Node node) {
    	// 子のidで子をグループ化します。
    	Map<String, List<Node>> map = node.children.stream()
    	    .filter(n -> !n.id.matches("\\d+"))    // 数字のみのidは対象外です。
    		.collect(Collectors.groupingBy(n -> n.id));
    	// 同一idの子のidに連番を付与してユニークにします。
    	for (Entry<String, List<Node>> e : map.entrySet()) {
    		if (e.getValue().size() <= 1)
    			continue;
    		List<Node> list = e.getValue();
    		for (int i = 0, size = list.size(); i < size; ++i)
    			list.get(i).id += i + 1;
    	}
    	for (Node child : node.children)
    		makeUniqId(child);
    }
    
    static void makeUniqPath(Node node) {
    	for (Node child : node.children) {
    		child.path = node.isRoot() ? child.id : node.path + "_" + child.id;
    		makeUniqPath(child);
    	}
    }

    static final List<String> NO_CHECK_TYPES = List.of("区分番号", "注");

    void checkSequence(Node root) {
//        Node prevNode = null;
        int prevId = 0;
        for (Node child : root.children) {
            if (!NO_CHECK_TYPES.contains(child.token.type.name)) {
                String d = child.id.replaceAll("\\D", "");
                if (!d.isEmpty()) {
                    int id = Integer.parseInt(d);
                    if (id != prevId + 1)
                        logger.warning("順序誤り: " + child.path + " " + child.token.toString());
//                    prevNode = child;
                    prevId = id;
                }
            }
            checkSequence(child);
        }
    }
	public Node parse(String inTxtFile) throws IOException {
	    List<Token> tokens = TokenReader.read(types(), inTxtFile);
	    Node root = parse(tokens);
	    makeUniqId(root);	// Nodeのidをユニークにします。
	    makeUniqPath(root);	// Nodeのpathをユニークにします。
	    if (check)
			checkSequence(root);        // Nodeのidの順序をチェックします。
	    return root;
	}
}
