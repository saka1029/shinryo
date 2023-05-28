package saka1029.shinryo.parser;

import java.util.List;
import java.util.logging.Logger;

/**
 * 文法:
 * 調剤告示 = [ "通則" 数字 ] 節
 * 節       = { "節" ( "区分" 区分番号 | 数字 ) }
 * 数字     = { "数字" カナ 注 }
 * カナ     = { "カナ" { "括弧数字" } }
 * 区分番号 = { "区分番号" 数字 注 }
 * 注       = "注" カナ | "注１" カナ 数字
 * OR
 * 文法:
 * 調剤告示 = [ "通則" 数字 ] { "節" ( "区分" { "区分番号" 数字 注 } | 数字 ) }
 * 数字     = { "数字" カナ 注 }
 * カナ     = { "カナ" { "括弧数字" } }
 * 注       = "注" カナ | "注１" カナ 数字
 * 
 * 「注」は単一の場合には「"注" カナ」であるが、
 * 複数の注が連続する場合には「"注１" カナ」、「"２" カナ」、「"３" カナ」...となる。
 */
public class TKParser extends Parser {
    static final Logger logger = Logger.getLogger(TKParser.class.getName());

	static final TokenType 通則 = new TokenType("通則", Pat.number("通則"));
	static final TokenType 数字 = new TokenType("数字", Pat.numberHeader(Pat.数字));
	static final TokenType 節 = new TokenType("節", Pat.numberHeader("第" + Pat.数字 + "節"));
	static final TokenType 区分 = new TokenType("区分", Pat.number("区分"));
	static final TokenType 区分番号 = new TokenType("区分番号", Pat.numberHeader(Pat.repeat("[０-９]{2}", "の", Pat.数字)));
	static final TokenType カナ = new TokenType("カナ", Pat.numberHeader(Pat.カナ));
	static final TokenType 注１ = new TokenType("注１", Pat.numberHeader("注１"));
	static final TokenType 注 = new TokenType("注", Pat.numberHeader("注"));
	static final TokenType 括弧数字 = new TokenType("括弧数字", Pat.numberHeader(Pat.括弧数字));

	static final List<TokenType> TYPES = List.of(通則, 区分番号, 数字, 節, 区分, カナ, 注１, 注, 括弧数字);
	
	@Override
    public List<TokenType> types() {
        return TYPES;
    }

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
