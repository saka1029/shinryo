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
	
	record Token(TokenType type, int indent, String number, String header) { 
		public static final Token EOT = new Token(null, 0, null, null);

		@Override
		public String toString() {
			return "Token(%s, %d, %s, %s)".formatted(type.name, indent, number, header);
		}
	}

	record TokenType(String name, Pattern pattern) {
		TokenType(String name, String pattern) {
			this(name, Pattern.compile(pattern));
		}
		
		Token match(String line) {
			Matcher m = pattern.matcher(line);
			if (!m.matches())
				return null;
			int i = 0, length = line.length();
			while (i < length && line.charAt(i) == ' ')
				++i;
			return new Token(this, i, m.group("N"), m.group("H"));
		}
	}
	
	record Node(Token token, List<Node> children) {

		public static Node root(String name) {
			return new Node(new Token(new TokenType(name, ""), 0, "", name));
		}

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

	static final TokenType 通則 = new TokenType("通則", "\\s*(?<N>通則)(?<H>)");
	static final TokenType 数字 = new TokenType("数字", "\\s*(?<N>[0-9０-９]+)\\s+(?<H>.*)");
	static final TokenType 節 = new TokenType("節", "\\s*(?<N>第[0-9０-９]節+)\\s+(?<H>.*)");
	static final TokenType 区分 = new TokenType("区分", "\\s*(?<N>区分)(?<H>)");
	static final TokenType 区分番号 = new TokenType("区分番号", "\\s*(?<N>[０-９]{2}(の[０-９]+)*)\\s+(?<H>.*)");
	static final TokenType カナ = new TokenType("カナ", "\\s*(?<N>[" + イロハ + "])\\s+(?<H>.*)");
	static final TokenType 注１ = new TokenType("注１", "\\s*(?<N>注１)\\s+(?<H>.*)");
	static final TokenType 注 = new TokenType("注", "\\s*(?<N>注)\\s+(?<H>.*)");
	static final TokenType 括弧数字 = new TokenType("括弧数字", "\\s*(?<N>[(（][0-9０-９]+[)）])\\s+(?<H>.*)");

	static List<TokenType> types = List.of(通則, 区分番号, 数字, 節, 区分, カナ, 注１, 注, 括弧数字);

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
	
	static class ParseException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public ParseException(String message) {
			super(message);
		}
	}
	
	static abstract class Parser {
		final List<Token> tokens;
		final int max;
		final String docName;

		int index = 0;
		Token token, eaten = null;

		public Parser(String docName, List<Token> tokens) {
			this.tokens = tokens;
			this.max = tokens.size();
			this.docName = docName;
			this.token = get();
		}
		
		ParseException error(String format, Object... args) {
			return new ParseException(format.formatted(args));
		}
		
		Token get() {
			return index < max ? tokens.get(index++) : Token.EOT;
		}
		
		boolean eat(TokenType expected) {
			if (token == Token.EOT)
				return false;
			if (token.type.equals(expected)) {
				eaten = token;
				token = get();
				return true;
			}
			return false;
		}
		
		boolean eatInner(Node parent, TokenType expected) {
			if (token == Token.EOT)
				return false;
			if (token.indent > parent.token.indent && token.type.equals(expected)) {
				eaten = token;
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
		
		public abstract void parse(Node parent);
		
		public Node root() {
			Node root = Node.root(docName);
			parse(root);
			return root;
		}
	}

	static Node parse(String docName, List<Token> tokens) {
	    return new Parser(docName, tokens) {
	        void 注(Node parent) {
	            if (eatInner(parent, 注)) {
	                Node n = add(parent, eaten);
	                カナ(n);
	            } else if (eatInner(parent, 注１)) {
	                Node n = add(parent, eaten);
	                カナ(n);
	                数字(parent);
	            }
	        }

	        void カナ(Node parent) {
                while (eat(カナ)) {
	                Node n = add(parent, eaten);
                    while (eat(括弧数字)) {
                        add(n, eaten);
                    }
                }
	        }

	        void 数字(Node parent) {
	            while (eat(数字)) {
	                Node n = add(parent, eaten);
	                カナ(n);
	                注(n);
	            }
	        }

	        void 通則(Node parent) {
	            if (eat(通則)) {
                    Node n = add(parent, eaten);
                    数字(n);
	            }
	        }
	        
	        void 区分番号(Node parent) {
	            while (eat(区分番号)) {
	                Node n = add(parent, eaten);
                    数字(n);
                    注(n);
	            }
	        }

	        void 節(Node parent) {
	            while (eat(節)) {
	                Node n = add(parent, eaten);
	                if (eat(区分)) {
	                    Node k = add(n, eaten);
	                    区分番号(k);
	                } else {
	                    数字(n);
	                }
	            }
	        }

	        @Override
	        public void parse(Node parent) {
	            通則(parent);
	            節(parent);
	            if (token != Token.EOT)
	                throw error("未処理のトークン: %s", tokens.subList(index - 1, max));
	        }
	    }.root();
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
		Node root = parse("令和4年調剤診療報酬点数表", tokens);
		print(root, 0);
	}

}
