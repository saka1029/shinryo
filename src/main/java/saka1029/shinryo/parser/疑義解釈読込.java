package saka1029.shinryo.parser;

import java.util.List;
import java.util.logging.Logger;

public class 疑義解釈読込 extends Parser {
    static final Logger LOGGER = Logger.getLogger(疑義解釈読込.class.getName());

	public static final TokenType 分類 = new TokenType("分類", Pat.number("\\S+関係"), Pat.固定値id("b"));
	public static final TokenType 名称 = new TokenType("名称", Pat.number("【\\S+】"), Pat.固定値id("n"));
	public static final TokenType 問 = new TokenType("問", Pat.numberHeader("問\\s?" + Pat.数字ハイフン), Pat.数字id);
	public static final TokenType 答 = new TokenType("答", Pat.number("（答）.*"), Pat.固定値id("a"));

	static final List<TokenType> TYPES = List.of(分類, 名称, 問, 答);

    public 疑義解釈読込() {
        super(false);
    }

    @Override
    public List<TokenType> types() {
        return TYPES;
    }

    void 名称(Node parent) {
        while (eat(問)) {
            Node q = add(parent, eaten);
            if (eat(答))
                add(q, eaten);
            else
                error("（答）がありません");
        }
    }

    void 分類(Node parent) {
        while (eat(名称)) {
            Node name = add(parent, eaten);
            名称(name);
        }
    }

    @Override
    public void parse(Node parent) {
        while (eat(分類)) {
            Node bunrui = add(parent, eaten);
            分類(bunrui);
        }
    }

}
