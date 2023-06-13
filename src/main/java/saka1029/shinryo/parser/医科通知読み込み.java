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
	public static final TokenType 区分番号 = new TokenType("区分番号", Pat.numberHeader(Pat.fromTo(Pat.区分番号)), Pat.区分番号id);
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

	public 医科通知読み込み() {
	    super(true);
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
     * 区分番号D236-2の(3)は「丸数字」の下に「イロハ」順の「カナ」がある。「括弧カナ」が正しい。
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

	/**
	 * 区分番号A101の(18)に「[計算例]」というトークンが存在する。
	 * 区分番号A308の(11)にも同様のトークンが存在する。
	 * <pre>
	 *          （18）  「注 11」に規定するＦＩＭの測定に係る取扱いについては、以下のとおりとする。な
     *              お、令和４年３月31日において現に療養病棟入院基本料に係る届出を行っている保険医療
     *              機関については、令和４年９月30日までの間に限り、ＦＩＭの測定を行っているものとみ
     *              なすものであること。
     *              ア  前月までの６か月間に当該医療機関の療養病棟から退棟した患者（イ及びエの規定に
     *                 よって計算対象から除外する患者を除く。）について、以下の①の総和を②の総和で除
     *                 したもの（以下「療養病棟リハビリテーション実績指数」という。）を各年度４月、７
     *                 月、10月及び１月において算出していること。
     *                 ①  退棟時のＦＩＭ運動項目の得点から、入棟時のＦＩＭ運動項目の得点を控除したもの。
     *                 ②  各患者の入棟から退棟までの日数を、回復期リハビリテーション病棟入院料「注
     *                   １」に規定する厚生労働大臣が定める日数の上限のうち当該患者の入棟時の状態に応
     *                   じたもので除したもの（回復期リハビリテーションを要する状態に該当しない患者に
     *                   ついては、180日で除すること。）
     *               [計算例]
     *                 ①  前月までの６か月間に50人退棟し、入棟時にＦＩＭ運動項目が50点、退棟時に80
     *                   点だったものが30人、入棟時にＦＩＭ運動項目が40点、退棟時に65点だったものが
     *                   20人とすると、(80-50)× 30＋(65-40)× 20 = 1,400
     *                 ②  前月までの６か月間に50人退棟し、そのうち30人が大腿骨骨折手術後（回復期リ
     *                   ハビリテーション病棟入院料における算定日数上限が90日）で実際には72日で退
     *                   棟、残り20人が脳卒中（回復期リハビリテーション病棟入院料における算定日数上限
     *                   が150日）で実際には135日で退棟したとすると、(72/90)× 30 + (135/150)× 20 = 42
     *                    従って、この例では療養病棟リハビリテーション実績指数は①／②＝33.3となる。
	 * </pre>
	 */
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

    /**
     * 区分番号A248の(5)の下に「イロハ」順の「カナ」がある。
     * 区分番号B001-2-6の(2)も同様。
     * 通知のカナは「アイウ」順なので元の文書の誤り。
     * <pre>
     *          (５)  (４)において、精神症状を有する患者とは、以下の場合をいうこと。
     *               イ  過去６か月以内に精神科受診の既往がある患者
     *               ロ  医師が、抑うつ、せん妄、躁状態等、精神状態の異常を認めた患者
     *               ハ  アルコール中毒を除く急性薬毒物中毒が診断された患者
     * </pre>
     * 区分番号D291-3の(4)の下に「括弧アイウ」がある。
     * <pre>
     *          (４)  「通則５」の入院中の患者以外の患者に対する内視鏡検査（区分番号「Ｄ３２４」及び
     *              「Ｄ３２５」を除く。以下、「通則５」に係る留意事項において、「内視鏡検査」とい
     *              う。）の休日加算、時間外加算又は深夜加算は、次の場合に算定できる。ただし、内視鏡
     *              検査が保険医療機関又は保険医の都合により休日、時間外又は深夜に行われた場合には算
     *              定できない。
     *              (ア)  休日加算、時間外加算又は深夜加算が算定できる初診又は再診に引き続き行われた
     *                   緊急内視鏡検査の場合
     *              (イ)  初診又は再診に引き続いて、内視鏡検査に必要不可欠な検査等を行った後速やかに
     *                   内視鏡検査（休日に行うもの又はその開始時間（患者に対し直接施療した時をいう。）
     *                   が診療時間以外の時間若しくは深夜であるものに限る。）を開始した場合であって、
     *                   当該初診又は再診から内視鏡検査の開始時間までの間が８時間以内である場合（当該
     *                   内視鏡検査の開始時間が入院手続きの後の場合を含む。）
     * </pre>
     */
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
	        if (isChild(suji, カナ))
                カナ(suji);
	        else
	            括弧数字(suji);
	    }
	}
	
	/**
	 * 区分番号D291-3の下に不明なトークンがある。
	 * <pre>
	 *        Ｄ２９１－３  内服・点滴誘発試験
     *          (１)  貼付試験、皮内反応、リンパ球幼若化検査等で診断がつかない薬疹の診断を目的とした
     *              場合であって、入院中の患者に対して被疑薬を内服若しくは点滴・静注した場合に限り算
     *              定できる。
     *          (２)  検査を行うに当たっては、内服・点滴誘発試験の危険性、必要性、検査方法及びその他
     *              の留意事項について、患者又はその家族等に対して文書により説明の上交付するとともに、
     *              その文書の写しを診療録に添付すること。
     *        ［内視鏡検査に係る共通事項（区分番号「Ｄ２９５」から区分番号「Ｄ３２５」まで）］
     *          (１)  本節の通則による新生児加算又は乳幼児加算を行う場合には、超音波内視鏡検査加算は、
     *              所定点数に含まないものとする。
     *          (２)  内視鏡検査の「通則２」による算定において、区分番号「Ｄ３１３」大腸内視鏡検査の
     *              「１」のイ、ロ及びハについては、同一の検査として扱う。また、準用が通知されている
     *              検査については、当該検査が準ずることとされている検査と同一の検査として扱う。
     * </pre>
	 * @param parent
	 */
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
