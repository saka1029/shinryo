package saka1029.shinryo.parser;

import java.util.List;
import java.util.logging.Logger;

public class TKParser extends Parser {
    static final Logger logger = Logger.getLogger(TKParser.class.getName());

	public static final TokenType 通則 = new TokenType("通則", "\\s*(?<N>通則)(?<H>)");
	public static final TokenType 数字 = new TokenType("数字", "\\s*(?<N>[0-9０-９]+)\\s+(?<H>.*)");
	public static final TokenType 節 = new TokenType("節", "\\s*(?<N>第[0-9０-９]節+)\\s+(?<H>.*)");
	public static final TokenType 区分 = new TokenType("区分", "\\s*(?<N>区分)(?<H>)");
	public static final TokenType 区分番号 = new TokenType("区分番号", "\\s*(?<N>[０-９]{2}(の[０-９]+)*)\\s+(?<H>.*)");
	public static final TokenType カナ = new TokenType("カナ", "\\s*(?<N>[" + TokenType.イロハ + "])\\s+(?<H>.*)");
	public static final TokenType 注１ = new TokenType("注１", "\\s*(?<N>注１)\\s+(?<H>.*)");
	public static final TokenType 注 = new TokenType("注", "\\s*(?<N>注)\\s+(?<H>.*)");
	public static final TokenType 括弧数字 = new TokenType("括弧数字", "\\s*(?<N>[(（][0-9０-９]+[)）])\\s+(?<H>.*)");

	public static final List<TokenType> TYPES = List.of(通則, 区分番号, 数字, 節, 区分, カナ, 注１, 注, 括弧数字);

    void カナ(Node parent) {
        while (eat(カナ)) {
            Node n = add(parent, eaten);
            while (eat(括弧数字)) {
                add(n, eaten);
            }
        }
    }

    void 注(Node parent) {
        if (eatChild(parent, 注)) {
            Node n = add(parent, eaten);
            カナ(n);
        } else if (eatChild(parent, 注１)) {
            Node n = add(parent, eaten);
            カナ(n);
            数字(parent);
        }
    }

	void 数字(Node parent) {
	    while (eat(数字)) {
	        Node n = add(parent, eaten);
	        カナ(n);
	        注(n);
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
	    if (eat(通則)) {
	        Node n = add(parent, eaten);
	        数字(n);
	    }
	    節(parent);
	    if (token != null)
            logger.warning("未処理のトークン: " + tokens.get(index - 1));
	}

}
