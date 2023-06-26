package saka1029.shinryo.parser;

import java.util.List;
import java.util.logging.Logger;

public class 施設基準通知読込 extends Parser {

    static final Logger LOGGER = Logger.getLogger(施設基準告示読込.class.getName());

    static final TokenType 基本診療料 = new TokenType("基本診療料", Pat.number("基本診療料の施設基準等"), Pat.固定値id("1"));
    static final TokenType 特掲診療料 = new TokenType("特掲診療料", Pat.number("特掲診療料の施設基準等"), Pat.固定値id("2"));
    static final TokenType 第数字の = new TokenType("第数字の", Pat.numberHeader("第" + Pat.数字の), Pat.数字id);
	static final TokenType 別添 = new TokenType("別添", Pat.number("別添" + Pat.数字), Pat.数字id);
	static final TokenType 数字の = new TokenType("数字の", Pat.numberHeader(Pat.数字の), Pat.数字id);
	static final TokenType 括弧数字 = new TokenType("括弧数字", Pat.numberHeader(Pat.括弧数字), Pat.数字id);
	static final TokenType カナ = new TokenType("カナ", Pat.numberHeader(Pat.カナ), Pat.アイウid);
	static final TokenType 括弧カナ = new TokenType("括弧カナ", Pat.numberHeader(Pat.括弧カナ), Pat.イロハid);

    public 施設基準通知読込() {
        super(true);
    }

    static final List<TokenType> TYPES = List.of();

    @Override
    public List<TokenType> types() {
        return TYPES;
    }

    @Override
    public void parse(Node parent) {
        // TODO Auto-generated method stub

    }

}
