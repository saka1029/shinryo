package saka1029.shinryo.parser;

import java.util.List;
import java.util.logging.Logger;

/**
 * 施設基準通知用パーサ
 * 
 * <pre>
 * 文法:
 * 施設基準通知 = "基本診療料" 施設基準 "特掲診療料" 施設基準
 * 施設基準     = 第数次の 別添
 * 別添         = { "別添" 数字の 第数次の }
 * 第数字の     = { "第数次の" 数字の }
 * 数字の       = { "数字の" ( カナ | 括弧数字 ) }
 * 括弧数字     = { "括弧数字" ( 丸数字 | 括弧カナ | カナ ) }
 * 丸数字       = { "丸数字" ( 括弧カナ | カナ ) }
 * カナ         = { "カナ" ( 丸数字 | 括弧カナ ) }
 * 括弧カナ     = { "括弧カナ" 丸数字 }
 * </pre>
 * 「丸数字」と「括弧カナ」および「丸数字」と「カナ」は相互に参照しあっているので、例えば
 * 以下のように解釈されることがある。
 * <pre>
 * ①
 *     ア
 *     イ
 *         ②
 * </pre>
 * これを防ぐために、先祖が既に丸数字を含む場合は丸数字と解釈しないようにする。
 */
public class 施設基準通知読込 extends Parser {

    static final Logger LOGGER = Logger.getLogger(施設基準告示読込.class.getName());

    static final TokenType 基本診療料 = new TokenType("基本診療料", Pat.number("基本診療料の施設基準等"), Pat.固定値id("1"));
    static final TokenType 特掲診療料 = new TokenType("特掲診療料", Pat.number("特掲診療料の施設基準等"), Pat.固定値id("2"));
    static final TokenType 第数字の = new TokenType("第数字の", Pat.numberHeader(Pat.fromTo("第" + Pat.数字の)), s -> "d" + Pat.正規化(s));
    static final TokenType 別添 = new TokenType("別添", Pat.number("別添" + Pat.数字), Pat.数字id);
    static final TokenType 数字の = new TokenType("数字の", Pat.numberHeader(Pat.数字の), Pat.数字id);
    static final TokenType 括弧数字 = new TokenType("括弧数字", Pat.numberHeader(Pat.括弧数字), Pat.数字id);
    static final TokenType カナ = new TokenType("カナ", Pat.numberHeader(Pat.カナ), Pat.アイウid);
    static final TokenType 括弧カナ = new TokenType("括弧カナ", Pat.numberHeader(Pat.括弧カナ), Pat.イロハid);
    static final TokenType 丸数字 = new TokenType("丸数字", Pat.numberHeader(Pat.丸数字), Pat.丸数字id);

    public 施設基準通知読込() {
        super(true);
    }

    static final List<TokenType> TYPES = List.of(基本診療料, 特掲診療料, 第数字の, 別添, 数字の, 括弧数字, カナ, 括弧カナ, 丸数字);

    @Override
    public List<TokenType> types() {
        return TYPES;
    }

    void 丸数字(Node parent) {
        // 既にparentの先祖に丸数字があれば何もしない。
        if (containsAncestor(parent, 丸数字))
            return;
        while (eat(丸数字)) {
            Node c = add(parent, eaten);
            if (is(括弧カナ))
                括弧カナ(c);
            else
                カナ(c);
        }
    }

    void 括弧カナ(Node parent) {
        if (containsAncestor(parent, 括弧カナ))
            return;
        while (eat(括弧カナ)) {
            Node c = add(parent, eaten);
            丸数字(c);
        }
    }

    void カナ(Node parent) {
        if (containsAncestor(parent, カナ))
            return;
        while (eat(カナ)) {
            Node c = add(parent, eaten);
            if (is(丸数字))
                丸数字(c);
            else
                括弧カナ(c);
        }
    }

    void 括弧数字(Node parent) {
        while (eat(括弧数字)) {
            Node c = add(parent, eaten);
            if (is(括弧カナ))
                括弧カナ(c);
            else if (is(丸数字))
                丸数字(c);
            else
                カナ(c);
        }
    }

    void 数字の(Node parent) {
        while (eat(数字の)) {
            Node c = add(parent, eaten);
            if (is(カナ))
                カナ(c);
            else
                括弧数字(c);
        }
    }

    void 第数字の(Node parent) {
        while (eat(第数字の)) {
            Node c = add(parent, eaten);
            数字の(c);
        }
    }

    void 別添(Node parent) {
        while (eat(別添)) {
            Node c = add(parent, eaten);
            数字の(c);
            第数字の(c);
        }
    }

    void 施設基準(Node parent) {
        第数字の(parent);
        別添(parent);
    }

    @Override
    public void parse(Node parent) {
        if (!eat(基本診療料))
            throw error("「基本診療料の施設基準等」がありません %s", token);
        Node k = add(parent, eaten);
        施設基準(k);
        if (!eat(特掲診療料))
            throw error("「特掲診療料の施設基準等」がありません %s", token);
        Node t = add(parent, eaten);
        施設基準(t);
    }

}
