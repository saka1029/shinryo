package saka1029.shinryo.parser;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class TestSimpleParser {
	
	static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);
	
	static record Token(TokenType type, String number, String header) { 
		@Override
		public String toString() {
			return "Token(%s, %s, %s)".formatted(type.name, number, header);
		}
	}

	static class TokenType {
		final String name;
		final Pattern pattern;
		TokenType(String name, String pattern) {
			this.name = name;
			this.pattern = Pattern.compile(pattern);
		}
		
		Token match(String line) {
			Matcher m = pattern.matcher(line);
			if (!m.matches())
				return null;
			return new Token(this, m.group("N"), m.group("H"));
		}
	}
	
	record Node(Token token, List<Node> children) {
	    public Node(Token token) {
	        this(token, new ArrayList<>());
	    }
	}
	
	static final String イロハ =
	    "イロハニホヘトチリヌルヲ"
	    + "ワカヨタレソツネナラム"
	    + "ウヰノオクヤマケフコエテ"
	    + "アサキユメミシヱヒモセスン";
	
	static final Pattern COMMENT = Pattern.compile("\\s*#.*");

	static final Token ROOT = new Token(new TokenType("root", ""), "root", "root");
	static final Token EOT = new Token(null, null, null);

	static final TokenType 通則 = new TokenType("通則", "\\s*(?<N>通則)(?<H>)");
	static final TokenType 数字 = new TokenType("数字", "\\s*(?<N>[0-9０-９]+)\\s+(?<H>.*)");
	static final TokenType 節 = new TokenType("節", "\\s*(?<N>第[0-9０-９]節+)\\s+(?<H>.*)");
	static final TokenType 区分 = new TokenType("区分", "\\s*(?<N>区分)(?<H>)");
	static final TokenType 区分番号 = new TokenType("区分番号", "\\s*(?<N>[０-９]{2}(の[０-９]+)*)\\s+(?<H>.*)");
	static final TokenType カナ = new TokenType("カナ", "\\s*(?<N>[" + イロハ + "])\\s+(?<H>.*)");
	static final TokenType 注１ = new TokenType("注１", "\\s*(?<N>注１)\\s+(?<H>.*)");
	static final TokenType 注 = new TokenType("注", "\\s*(?<N>注)\\s+(?<H>.*)");
	static final TokenType 括弧数字 = new TokenType("括弧数字", "\\s*(?<N>[(（][0-9０-９]+[)）])\\s+(?<H>.*)");

	static List<TokenType> types = List.of(
		通則,
		区分番号,
		数字,
		節,
		区分,
		カナ,
		注１,
		注,
		括弧数字
	);

	static List<Token> tokenize(String inFile) throws IOException {
		List<String> lines = Files.readAllLines(Path.of(inFile));
		List<Token> tokens = new ArrayList<>();
		for (String line : lines) {
			if (COMMENT.matcher(line).matches())
				continue;
			for (TokenType tt : types) {
				Token t = tt.match(line);
				if (t != null) {
				    tokens.add(t);
					break;
				}
			}
		}
		return tokens;
	}

	static Node parse(List<Token> tokens) {
	    Node root = new Node(ROOT);
	    new Object() {
	        int max = tokens.size(), index = 0;
	        Token prev = null, token = get();
	        
	        RuntimeException error(String format, Object... args) {
	            return new RuntimeException(format.formatted(args));
	        }
	        
	        Token get() {
	            return index < max ? tokens.get(index++) : EOT;
	        }
	        
	        boolean eat(TokenType expected) {
	            if (token == EOT)
	                return false;
	            if (token.type.equals(expected)) {
	                prev = token;
	                token = get();
	                return true;
	            }
	            return false;
	        }
	        
	        Node add(Node parent, Token token) {
	            Node child = new Node(token);
	            parent.children.add(child);
	            return child;
	        }

	        void 注(Node parent) {
	            if (eat(注)) {
	                Node n = add(parent, prev);
	                カナ(n);
	            } else if (eat(注１)) {
	                Node n = add(parent, prev);
	                カナ(n);
	                数字(parent);
	            }
	        }

	        void カナ(Node parent) {
                while (eat(カナ)) {
	                Node n = add(parent, prev);
                    while (eat(括弧数字)) {
                        Node nn = add(n, prev);
                    }
                }
	        }

	        void 数字(Node parent) {
	            while (eat(数字)) {
	                Node n = add(parent, prev);
	                カナ(n);
	                注(n);
	            }
	        }

	        void 通則(Node parent) {
	            if (eat(通則)) {
                    Node n = add(parent, prev);
                    数字(n);
	            }
	        }
	        
	        void 区分番号(Node parent) {
	            while (eat(区分番号)) {
	                Node n = add(parent, prev);
                    数字(n);
                    注(n);
	            }
	        }

	        void 節(Node parent) {
	            while (eat(節)) {
	                Node n = add(parent, prev);
	                if (eat(区分)) {
	                    Node k = add(n, prev);
	                    区分番号(k);
	                } else {
	                    数字(n);
	                }
	            }
	        }

	        void parse(Node parent) {
	            通則(parent);
	            節(parent);
	            if (token != EOT)
	                throw error("未処理のトークン: %s", tokens.subList(index - 1, max));
	        }
	    }.parse(root);
	    return root;
	}

    void print(Node parent, int level) {
        OUT.println("  ".repeat(level) + parent.token);
        for (Node child : parent.children)
            print(child, level + 1);
    }

	@Test
	public void test() throws IOException {
		String inFile = "data/04-t-kokuji.txt";
		List<Token> tokens = tokenize(inFile);
		Node root = parse(tokens);
		print(root, 0);
	}

}
