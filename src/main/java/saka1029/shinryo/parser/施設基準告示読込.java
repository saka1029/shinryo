package saka1029.shinryo.parser;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * 施設基準告示用パーサ
 * 
 * <pre>
 * 文法:
 * 施設基準告示 = "基本診療料" 施設基準 "特掲診療料" 施設基準
 * 施設基準     = { "第漢数字" 漢数字の } { "別表第" 漢数字の }
 * 漢数字の     = { "漢数字の" 括弧数字 }
 * 括弧数字     = { "括弧数字" ( カナ | 丸数字 ) }
 * カナ         = { "カナ" 丸数字 }
 * 丸数字       = { "丸数字" 数字 }
 * 数字         = { "数字" 括弧漢数字 }
 * 括弧漢数字   = { "括弧漢数字" }
 * </pre>
 */
public class 施設基準告示読込 extends Parser {
	
	static final Logger LOGGER = Logger.getLogger(施設基準告示読込.class.getName());
	
	public static final TokenType 基本診療料 = new TokenType("基本診療料", Pat.number("基本診療料の施設基準等"), Pat.固定値id("1"));
	public static final TokenType 特掲診療料 = new TokenType("特掲診療料", Pat.number("特掲診療料の施設基準等"), Pat.固定値id("2"));
	public static final TokenType 第漢数字 = new TokenType("第漢数字", Pat.numberHeader("第" + Pat.漢数字の), Pat.漢数字id);
	public static final TokenType 漢数字の = new TokenType("漢数字の", Pat.numberHeader(Pat.fromTo(Pat.漢数字の)), Pat.漢数字id);
	public static final TokenType 括弧数字の = new TokenType("括弧数字の", Pat.numberHeader(Pat.括弧数字の), Pat.数字id);
	public static final TokenType 括弧漢数字 = new TokenType("括弧漢数字", Pat.numberHeader(Pat.括弧漢数字), Pat.漢数字id);
	public static final TokenType カナ = new TokenType("カナ", Pat.numberHeader(Pat.カナ), Pat.イロハid);
	public static final TokenType 丸数字 = new TokenType("丸数字", Pat.numberHeader(Pat.丸数字), Pat.丸数字id);
	public static final TokenType 数字 = new TokenType("数字", Pat.numberHeader(Pat.数字), Pat.数字id);
	public static final TokenType 別表 = new TokenType("別表", Pat.number("別表"), Pat.固定値id("b"));
	public static final TokenType 別表第 = new TokenType("別表第", Pat.numberHeader("別表" + Pat.fromTo("第" + Pat.漢数字の)), Pat.漢数字id);

	static final List<TokenType> TYPES = List.of(基本診療料, 特掲診療料, 第漢数字, 漢数字の, 括弧数字の, 括弧漢数字, カナ, 丸数字, 数字, 別表第);
	
	@Override
    public List<TokenType> types() {
        return TYPES;
    }
	
	public 施設基準告示読込() {
	    super(false);
	}

	void 括弧漢数字(Node parent) {
		while (eat(括弧漢数字)) {
			add(parent, eaten);
		}
	}

	void 数字(Node parent) {
		while (eat(数字)) {
			Node n = add(parent, eaten);
			括弧漢数字(n);
		}
	}

	void 丸数字(Node parent) {
		while (eat(丸数字)) {
			Node n = add(parent, eaten);
			数字(n);
		}
	}

	void カナ(Node parent) {
		while (eat(カナ)) {
			Node n = add(parent, eaten);
			丸数字(n);
		}
	}

	void 括弧数字の(Node parent) {
		while (eat(括弧数字の)) {
			Node n = add(parent, eaten);
			if (is(カナ))
				カナ(n);
			else if (is(丸数字))
				丸数字(n);
		}
	}

	void 漢数字の(Node parent) {
		while (eat(漢数字の)) {
			Node n = add(parent, eaten);
			括弧数字の(n);
		}
	}

	void 施設基準(Node parent) {
		while (eat(第漢数字)) {
			Node n = add(parent, eaten);
			漢数字の(n);
		}
		if (is(別表第)) {
			// 「別表第」は「別表」ノードの下に配置する。
			Token beppyo = new Token(別表, "別表", "", Collections.emptyList(), token);
			Node b = add(parent, beppyo);
			while (eat(別表第)) {
				Node n = add(b, eaten);
				漢数字の(n);
			}
		}
	}

	@Override
	public void parse(Node parent) {
		if (!eat(基本診療料))
			throw error("「基本診療料の施設基準等」がありません");
		Node k = add(parent, eaten);
		施設基準(k);
		if (!eat(特掲診療料))
			throw error("「特掲診療料の施設基準等」がありません");
		Node t = add(parent, eaten);
		施設基準(t);
	}
}
