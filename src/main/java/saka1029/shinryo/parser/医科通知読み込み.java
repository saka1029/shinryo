package saka1029.shinryo.parser;

import java.util.List;
import java.util.logging.Logger;

/**
 * 医科通知用パーサ。
 * 
 * <pre>
 * 文法:
 * 医科通知    = 通則 章
 * 通則        = [ "通則" 数字 ]
 * 章          = { "章" 通則 部 }
 * 部          = { "部" ( 区分分類 | 数字 | 節 ) }
 * 節          = { "節" 通則 ( 款 | カナ | 数字 区分番号 ) }
 * 款          = { "款" 数字 区分番号 }
 * 区分分類    = { "区分分類" 数字 区分番号 }
 * 区分番号    = { "区分番号" ( 括弧数字 | カナ | 数字 ) } 
 * 数字        = { "数字" ( カナ | 括弧数字 ) }
 * 括弧数字    = { "括弧数字" ( カナ | 丸数字 | 括弧カナ | 例 ) }
 * カナ        = { "カナ" ( 括弧カナ | 数字 | 丸数字 ) }
 * 括弧カナ    = { "括弧カナ" ( 丸数字 | 例 ) }
 * 丸数字      = { "丸数字" 括弧カナ }
 * 例          = { "例" ( カナ | 丸数字 ) }
 * </pre>
 * 
 * 「区分大分類」は全体の中で一度しか登場しないので、コメントアウトして文法から除外する。
 */
public class 医科通知読み込み extends Parser {
    static final Logger logger = Logger.getLogger(医科通知読み込み.class.getName());

	public static final TokenType 通則 = new TokenType("通則", Pat.number("＜通則＞"), Pat.固定値id("t"));
	public static final TokenType 章 = new TokenType("章", Pat.numberHeader("第" + Pat.数字 + "章"), Pat.数字id);
	public static final TokenType 部 = new TokenType("部", Pat.numberHeader("第" + Pat.数字 + "部"), Pat.数字id);
	public static final TokenType 節 = new TokenType("節", Pat.numberHeader("第" + Pat.数字 + "節"), Pat.数字id);
	public static final TokenType 款 = new TokenType("款", Pat.numberHeader("第" + Pat.数字 + "款"), Pat.数字id);
//	public static final TokenType 区分大分類 = new TokenType("区分大分類", Pat.number(Pat.区分大分類), Pat.固定値id("a"));
	public static final TokenType 区分分類 = new TokenType("区分分類", Pat.number(Pat.区分分類), Pat.固定値id("b"));
	public static final TokenType 区分番号 = new TokenType("区分番号", Pat.numberHeader(Pat.区分番号), Pat.区分番号id);
	public static final TokenType 数字 = new TokenType("数字", Pat.numberHeader(Pat.数字), Pat.数字id);
	public static final TokenType 括弧数字 = new TokenType("括弧数字", Pat.numberHeader(Pat.括弧数字), Pat.数字id);
	public static final TokenType カナ = new TokenType("カナ", Pat.numberHeader(Pat.カナ), Pat.アイウid);
	public static final TokenType 括弧カナ = new TokenType("括弧カナ", Pat.numberHeader(Pat.括弧カナ), Pat.イロハid);
	public static final TokenType 丸数字 = new TokenType("丸数字", Pat.numberHeader(Pat.丸数字), Pat.丸数字id);
	public static final TokenType 例 = new TokenType("例", Pat.numberHeader(Pat.例), Pat.数字id);

	static final List<TokenType> TYPES = List.of(通則, 章, 部, 款, 節, /*区分大分類,*/ 区分分類, 区分番号, 数字, 括弧数字, カナ, 括弧カナ, 丸数字, 例);
	
	@Override
    public List<TokenType> types() {
        return TYPES;
    }

	/**
	 * 丸数字の下に括弧カナが来るケース。
	 * <pre>
	 *        Ｄ３１３  大腸内視鏡検査
     *          (１)  「１」のファイバースコピーによるものについては、関連する学会の消化器内視鏡に関
     *              するガイドラインを参考に消化器内視鏡の洗浄消毒を実施していることが望ましい。
     *          (２)  「２」のカプセル型内視鏡によるものは以下のいずれかに該当する場合に限り算定する。
     *              ア  大腸内視鏡検査が必要であり、大腸ファイバースコピーを実施したが、腹腔内の癒着
     *                 等により回盲部まで到達できなかった患者に用いた場合
     *              イ  大腸内視鏡検査が必要であるが、腹部手術歴があり癒着が想定される場合等、器質的
     *                 異常により大腸ファイバースコピーが実施困難であると判断された患者に用いた場合
     *              ウ  大腸内視鏡検査が必要であるが、以下のいずれかに該当し、身体的負担により大腸フ
     *                 ァイバースコピーが実施困難であると判断された患者に用いた場合
     *                 ①  以下の(イ)から(ニ)のいずれかに該当する場合
     *                   (イ)  ３剤の異なる降圧剤を用いても血圧コントロールが不良の高血圧症（収縮期血
     *                       圧160mmHg以上）
     *                   (ロ)  慢性閉塞性肺疾患（１秒率  70％未満）
	 * </pre>
	 */
	void 丸数字(Node parent) {
	    while (eat(丸数字)) {
	        Node msuji = add(parent, eaten);
	        if (isChild(msuji, 括弧カナ))
				括弧カナ(msuji);
	    }
	}

	void 例(Node parent) {
	    while (eat(例)) {
	        Node rei = add(parent, eaten);
	        if (is(カナ))
	            カナ(rei);
	        else
                丸数字(rei);
	    }
	}

	void 括弧カナ(Node parent) {
		while (eat(括弧カナ)) {
			Node kkana = add(parent, eaten);
			if (isChild(kkana, 丸数字))
                丸数字(kkana);
			else
			    例(kkana);
		}
	}

    void カナ(Node parent) {
    	while (eat(カナ)) {
    		Node kana = add(parent, eaten);
    		if (is(括弧カナ))
                括弧カナ(kana);
    		else if (isChild(kana, 数字))
    		    数字(kana);
    		else
    		    丸数字(kana);
    	}
    }

    void 括弧数字(Node parent) {
    	while (eat(括弧数字)) {
    		Node ksuji = add(parent, eaten);
    		if (is(カナ))
                カナ(ksuji);
    		else if (is(丸数字))
    		    丸数字(ksuji);
    		else if (is(例))
    		    例(ksuji);
    		else
    		    括弧カナ(ksuji);
    	}
    }

	void 数字(Node parent) {
	    while (eat(数字)) {
	        Node suji = add(parent, eaten);
	        if (is(カナ))
                カナ(suji);
	        else
	            括弧数字(suji);
	    }
	}
	
	void 区分番号(Node parent) {
        while (eat(区分番号)) {
            Node kubun = add(parent, eaten);
            if (is(括弧数字))
                括弧数字(kubun);
            else if (is(カナ))
                カナ(kubun);
            else
                数字(kubun);
        }
	}

	void 区分分類(Node parent) {
	    while (eat(区分分類)) {
	        Node kubunb = add(parent, eaten);
	        数字(kubunb);
	        区分番号(kubunb);
	    }
	}

	/**
	 * 区分大分類
	 * 
	 * <pre>
	 * 第９部 処置
	 *   ＜通則＞
	 *     １ ...
	 *   ＜処置料＞                 : 区分大分類 (これは第９部に１回だけ出現する）
	 *     （一般処置）             : 区分分類
	 *       Ｊ０００ 創傷処置      : 区分番号
	 * </pre>
	 */
//	void 区分大分類(Node parent) {
//	    while (eat(区分大分類)) {
//	        Node kubund = add(parent, eaten);
//	        区分分類(kubund);
//	    }
//	}

	/**
	 *
	 * 「時間外緊急院内検査加算」の先頭に「数字」を追加した。
	 * 第１節  検体検査料
	 *   第１款  検体検査実施料
	 *     １ 時間外緊急院内検査加算
	 *       (１)  時間外緊急院内検査加算については、保険医療機関において、当該保険医療機関が表示
	 */
	void 款(Node parent) {
	    while (eat(款)) {
	        Node kan = add(parent, eaten);
            数字(kan);
            区分番号(kan);
	    }
	}

	void 節(Node parent) {
        while (eat(節)) {
            Node setu = add(parent, eaten);
            通則(setu);
            if (is(款))
                款(setu);
            else if (is(カナ))
                カナ(setu);
            else {
                数字(setu);
                区分番号(setu);
            }
        }
	}

	void 部(Node parent) {
	    while (eat(部)) {
	        Node bu = add(parent, eaten);
	        通則(bu);
	        if (is(区分分類))
	            区分分類(bu);
	        else if (is(数字))
	            数字(bu);
	        else
                節(bu);
	    }
	}


	void 章(Node parent) {
	    while (eat(章)) {
	        Node sho = add(parent, eaten);
	        通則(sho);
	        部(sho);
	    }
	}
	
	void 通則(Node parent) {
	    if (eat(通則)) {
	        Node tusoku = add(parent, eaten);
	        数字(tusoku);
	    }
	}

	@Override
	public void parse(Node parent) {
	    通則(parent);
	    章(parent);
	}

}
