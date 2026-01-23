package saka1029.shinryo.kuromoji;

import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

import saka1029.shinryo.common.Common;

public class TestKuromoji {
    static final Logger logger = Common.logger(TestKuromoji.class);

    static final String TEXT = """
        保険医療機関において初診を行った場合に算定する。
        ただし、別に厚生労働大臣が定める施設基準に適合
        しているものとして地方厚生局長等に届け出た保険医療機関
        において、情報通信機器を用いた初診を行った場合には、253,900点を算定する。
    """;

    @Test
    public void testKuromoji() {
        logger.info("*** " + Common.methodName());
        Tokenizer tokenizer = new Tokenizer() ;
        List<Token> tokens = tokenizer.tokenize(TEXT.replaceAll("\\s+", ""));
        for (Token token : tokens)
            logger.info(token.getSurface()
                + "\t" + token.getPartOfSpeechLevel1()
                + "\t" + token.getAllFeatures());
    }

    @Test
    public void testWordSequences() {
        logger.info("*** " + Common.methodName());
        Tokenizer tokenizer = new Tokenizer() ;
        List<Token> tokens = tokenizer.tokenize(TEXT.replaceAll("\\s+", ""));
        StringBuilder noun = new StringBuilder();
        for (Token token : tokens)
            if (token.getPartOfSpeechLevel1().equals("名詞")) {
                noun.append(token.getSurface());
            } else {
                if (noun.length() > 0) {
                    logger.info(noun.toString());
                    noun.setLength(0);
                }
            }
        if (noun.length() > 0)
            logger.info(noun.toString());
    }
}
