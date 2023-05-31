package saka1029.shinryo.parser;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * 医科告示用パーサ。
 * 
 * <pre>
 * 文法:
 * 医科告示 = { "章" ( 部 | 数字 ) }
 * 部       = { "部" 通則 ( 節 | 数字 ) }
 * 節       = { "節" 通則 ( 区分 | 区分番号 | 款 | 数字 ) }
 * 款       = { "款" 通則 ( 区分 | 数字 ) }
 * 通則     = { "通則" 数字 }
 * 区分     = { "区分" ( 区分分類 | 区分番号 ) }
 * 区分分類 = { "区分分類" 通則 区分番号 }
 * 区分番号 = { "区分番号" 数字 注 }
 * 数字     = { "数字" カナ 注 }
 * カナ     = { "カナ" { "括弧数字" } }
 * 区分番号 = { "区分番号" 数字 注 }
 * 注       = "注" カナ | "注１" カナ 注数字
 * 注数字   = { "数字" カナ 注 }
 * </pre>
 * 
 * 「注」は単一の場合には「"注" カナ」であるが、
 * 複数の注が連続する場合には「"注１" カナ」、「"２" カナ」、「"３" カナ」...となる。
 * 「注 = "注" カナ | "注１" カナ 注数字」は「"注" カナ | "注ルート" "数字" カナ 注数字」に置換する。
 * 「注数字」は「数字」と同一であるが、「"数字"」に対する制約が異なる。
 * 「注数字」における「数字」は「注１」よりも右になければならない。
 */
public class IKParser extends Parser {
    static final Logger logger = Logger.getLogger(IKParser.class.getName());

	public static final TokenType 通則 = new TokenType("通則", Pat.number("通則"), Pat.固定値id("t"));
	public static final TokenType 章 = new TokenType("章", Pat.numberHeader("第" + Pat.数字 + "章"), Pat.数字id);
	public static final TokenType 部 = new TokenType("部", Pat.numberHeader("第" + Pat.数字 + "部"), Pat.数字id);
	public static final TokenType 節 = new TokenType("節", Pat.numberHeader("第" + Pat.数字 + "節"), Pat.数字id);
	public static final TokenType 款 = new TokenType("款", Pat.numberHeader("第" + Pat.数字 + "款"), Pat.数字id);
	public static final TokenType 数字 = new TokenType("数字", Pat.numberHeader(Pat.数字), Pat.数字id);
	public static final TokenType 区分 = new TokenType("区分", Pat.number("区分"), Pat.固定値id("k"));
	public static final TokenType 区分分類 = new TokenType("区分分類", Pat.number(Pat.区分分類), Pat.固定値id("b"));
	public static final TokenType 区分番号 = new TokenType("区分番号", Pat.numberHeader(Pat.fromTo(Pat.区分番号)), Pat.区分番号id);
	public static final TokenType カナ = new TokenType("カナ", Pat.numberHeader(Pat.カナ), Pat.イロハid);
	public static final TokenType 注１ = new TokenType("注１", Pat.numberHeader("注１"), Pat.固定値id("tyu1"));
	public static final TokenType 注 = new TokenType("注", Pat.numberHeader("注"), Pat.固定値id("tyu1"));
	public static final TokenType 括弧数字 = new TokenType("括弧数字", Pat.numberHeader(Pat.括弧数字), Pat.数字id);
	public static final TokenType 注ルート = new TokenType("注", Pat.numberHeader("注"), Pat.固定値id("tyu"));

	// 注ルートはパース時に作成するトークンなので、トークンリード時には指定しない。
	static final List<TokenType> TYPES = List.of(通則, 章, 部, 節, 款, 数字, 区分, 区分分類, 区分番号, カナ, 注１, 注, 括弧数字);
	
	@Override
    public List<TokenType> types() {
        return TYPES;
    }

    void 注数字(Node parent, Node tyu) { // tyuはインデント制約用
    	while (eatChild(tyu, 数字)) {
    		Node n = add(parent, eaten);
    		カナ(n);
    		注(n);
    	}
    }

    /**
     * 「注１」の場合は階層を１段追加する。
     * (「注」の場合はそのまま）
     * 前:
     *     注１ ＸＸＸＸＸ
     *         イ  ＹＹＹＹＹ
     * 後:
     *     注
     *         １ ＸＸＸＸＸ
     *             イ  ＹＹＹＹＹ
     */
    void 注(Node parent) {
        if (eatChild(parent, 注)) {
        	Node n = add(parent, eaten);
            カナ(n);
        } else if (eatChild(parent, 注１)) {
        	Token tyu = new Token(注ルート, "注", "", Collections.emptyList(), eaten);
        	Token one = new Token(数字, "１", eaten.header, eaten.body, eaten);
        	Node tyuNode = add(parent, tyu);
        	Node oneNode = add(tyuNode, one);
            カナ(oneNode);
            注数字(tyuNode, tyuNode);
        }
    }

    void カナ(Node parent) {
        while (eat(カナ)) {
            Node n = add(parent, eaten);
            while (eat(括弧数字)) {
                add(n, eaten);
            }
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
            Node kubunBango = add(parent, eaten);
            数字(kubunBango);
            注(kubunBango);
        }
	}
	
	void 区分分類(Node parent) {
		while (eat(区分分類)) {
			Node bunrui = add(parent, eaten);
			通則(bunrui);
			区分番号(bunrui);
		}
	}

	void 区分(Node parent) {
		if (eat(区分)) {
			Node kubun = add(parent, eaten);
			if (is(区分分類))
				区分分類(kubun);
			else
				区分番号(kubun);
		}
	}

	void 通則(Node parent) {
		while (eat(通則)) {
			Node tusoku = add(parent, eaten);
			数字(tusoku);
		}
	}

	void 款(Node parent) {
		while (eat(款)) {
			Node kan = add(parent, eaten);
			通則(kan);
			if (is(区分))
				区分(kan);
			else
				数字(kan);
		}
	}
	
	void 節(Node parent) {
        while (eat(節)) {
            Node setu = add(parent, eaten);
            通則(setu);
            if (is(区分))
                区分(setu);
            else if (is(区分番号))
                区分番号(setu);
            if (is(款))
            	款(setu);
            else
                数字(setu);
        }
	}

	public void 部(Node parent) {
        while (eat(部)) {
            Node bu = add(parent, eaten);
            通則(bu);
            if (is(節))
                節(bu);
            else
                数字(bu);
        }
	}

	@Override
	public void parse(Node parent) {
		while (eat(章)) {
			Node sho = add(parent, eaten);
			if (is(部))
			    部(sho);
			else
			    数字(sho);
		}
	}
}
