package saka1029.shinryo.parser;

import java.util.List;
import java.util.logging.Logger;

/**
 * 調剤通知用パーサ。
 * <pre>
 * 文法:
 * 調剤通知    = [ "通則" 数字 ] 節
 * 節          = { "節" 数字 区分番号 }
 * 区分番号    = { "区分番号" ( 数字 | 括弧数字 ) } 
 * 数字        = { "数字" ( 括弧数字 | カナ ) }
 * 括弧数字    = { "括弧数字" ( カナ | 括弧カナ ) }
 * カナ        = { "カナ" 括弧カナ }
 * 括弧カナ    = { "括弧カナ" { "丸数字" } }
 * </pre>
 * 
 * 「数字」は基本的に「{ "数字" { "括弧数字" { "カナ" { "括弧カナ" { "丸数字" } } } } }」であるが、
 * 途中のレベルを中抜きしていることがあるので、このような文法となる。
 */
public class TTParser extends Parser {
    static final Logger logger = Logger.getLogger(TTParser.class.getName());

	public static final TokenType 通則 = new TokenType("通則", Pat.number("＜通則＞"));
	public static final TokenType 節 = new TokenType("節", Pat.numberHeader("第" + Pat.数字 + "節"));
	public static final TokenType 区分番号 = new TokenType("区分番号", Pat.numberHeader(Pat.調剤通知区分番号));
	public static final TokenType 数字 = new TokenType("数字", Pat.numberHeader(Pat.数字));
	public static final TokenType 括弧数字 = new TokenType("括弧数字", Pat.numberHeader(Pat.括弧数字));
	public static final TokenType カナ = new TokenType("カナ", Pat.numberHeader(Pat.カナ));
	public static final TokenType 括弧カナ = new TokenType("括弧カナ", Pat.numberHeader(Pat.括弧カナ));
	public static final TokenType 丸数字 = new TokenType("丸数字", Pat.numberHeader(Pat.丸数字));

	static final List<TokenType> TYPES = List.of(通則, 節, 区分番号, 数字, 括弧数字, カナ, 括弧カナ, 丸数字);
	
	@Override
    public List<TokenType> types() {
        return TYPES;
    }

	void 括弧カナ(Node parent) {
		while (eat(括弧カナ)) {
			Node a = add(parent, eaten);
			while (eat(丸数字)) {
				add(a, eaten);
			}
		}
	}

    void カナ(Node parent) {
    	while (eat(カナ)) {
    		Node a = add(parent, eaten);
    		括弧カナ(a);
    	}
    }

    void 括弧数字(Node parent) {
    	while (eat(括弧数字)) {
    		Node a = add(parent, eaten);
    		if (is(カナ))
    			カナ(a);
    		else if (is(括弧カナ))
    			括弧カナ(a);
    		else
            	error("括弧数字の次に不明な要素: %s", token);
    	}
    }

	void 数字(Node parent) {
	    while (eat(数字)) {
	        Node a = add(parent, eaten);
	        if (is(括弧数字))
				括弧数字(a);
	        else if (is(カナ))
	        	カナ(a);
    		else
            	error("数字の次に不明な要素: %s", token);
	    }
	}
	
	void 区分番号(Node parent) {
        while (eat(区分番号)) {
            Node a = add(parent, eaten);
            if (is(数字))
            	数字(a);
            else if (is(括弧数字))
            	括弧数字(a);
            else
            	error("区分番号の次に不明な要素: %s", token);
        }
	}

	void 節(Node parent) {
        while (eat(節)) {
            Node a = add(parent, eaten);
			数字(a);
			区分番号(a);
        }
	}

	@Override
	public void parse(Node parent) {
	    if (eat(通則)) {
	        Node a = add(parent, eaten);
	        数字(a);
	    }
	    節(parent);
	}

}