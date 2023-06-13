package saka1029.shinryo.parser;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * 調剤告示用パーサ。
 * 
 * <pre>
 * 文法:
 * 調剤告示 = [ "通則" 数字 ] 節
 * 節       = { "節" ( "区分" 区分番号 | 数字 ) }
 * 数字     = { "数字" カナ 注 }
 * カナ     = { "カナ" { "括弧数字" } }
 * 区分番号 = { "区分番号" 数字 注 }
 * 注       = "注" カナ | "注１" カナ 注数字
 * 注数字   = { "数字" カナ 注 }
 * </pre>
 * 
 * 「注」は単一の場合には「"注" カナ」であるが、
 * 複数の注が連続する場合には「"注１" カナ」、「"２" カナ」、「"３" カナ」...となる。
 * 「注数字」は「数字」と同一であるが、「"数字"」に対する制約が異なる。
 * 「注数字」における「数字」は「注１」よりも右になければならない。
 */
public class 調剤告示読み込み extends Parser {
    static final Logger logger = Logger.getLogger(調剤告示読み込み.class.getName());

	public static final TokenType 通則 = new TokenType("通則", Pat.number("通則"), Pat.固定値id("t"));
	public static final TokenType 数字 = new TokenType("数字", Pat.numberHeader(Pat.数字), Pat.数字id);
	public static final TokenType 節 = new TokenType("節", Pat.numberHeader("第" + Pat.数字 + "節"), Pat.数字id);
	public static final TokenType 区分 = new TokenType("区分", Pat.number("区分"), Pat.固定値id("k"));
	public static final TokenType 区分番号 = new TokenType("区分番号", Pat.numberHeader(Pat.fromTo(Pat.調剤告示区分番号)), Pat.区分番号id);
	public static final TokenType カナ = new TokenType("カナ", Pat.numberHeader(Pat.カナ), Pat.イロハid);
	public static final TokenType 注１ = new TokenType("注１", Pat.numberHeader("注１"), Pat.固定値id("tyu1"));
	public static final TokenType 注 = new TokenType("注", Pat.numberHeader("注"), Pat.固定値id("tyu1"));
	public static final TokenType 括弧数字 = new TokenType("括弧数字", Pat.numberHeader(Pat.括弧数字), Pat.数字id);
	public static final TokenType 注ルート = new TokenType("注", Pat.numberHeader("注"), Pat.固定値id("tyu"));

	// 注ルートはパース時に作成するトークンなので、トークンリード時には指定しない。
	static final List<TokenType> TYPES = List.of(通則, 区分番号, 数字, 節, 区分, カナ, 注１, 注, 括弧数字);
	
	@Override
    public List<TokenType> types() {
        return TYPES;
    }

	public 調剤告示読み込み() {
	    super(false);
	}

    void カナ(Node parent) {
        while (eat(カナ)) {
            Node n = add(parent, eaten);
            while (eat(括弧数字)) {
                add(n, eaten);
            }
        }
    }

    /**
     * 「注１」の場合は階層を１段追加する。
     * 前:
     *     注１ ＸＸＸＸＸ
     *         イ  ＹＹＹＹＹ
     * 後:
     *     注
     *         １ ＸＸＸＸＸ
     *             イ  ＹＹＹＹＹ
     */
    void 注(Node parent) {
        if (isChild(parent, 注) && eat(注)) {
        	Node n = add(parent, eaten);
            カナ(n);
        } else if (isChild(parent, 注１) && eat(注１)) {
        	Token tyu = new Token(注ルート, "注", "", Collections.emptyList(), eaten);
        	Token one = new Token(数字, "１", eaten.header, eaten.body, eaten);
        	Node tyuNode = add(parent, tyu);
        	Node oneNode = add(tyuNode, one);
            カナ(oneNode);
            注数字(tyuNode);
        }
    }
    
    void 注数字(Node parent) {
    	while (isChild(parent, 数字) && eat(数字)) {
    		Node n = add(parent, eaten);
    		カナ(n);
    		注(n);
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
	}

}
