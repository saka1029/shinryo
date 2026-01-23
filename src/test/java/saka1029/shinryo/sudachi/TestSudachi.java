package saka1029.shinryo.sudachi;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import com.worksap.nlp.sudachi.Config;
import com.worksap.nlp.sudachi.Dictionary;
import com.worksap.nlp.sudachi.DictionaryFactory;
import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.Tokenizer;

import saka1029.shinryo.common.Common;

public class TestSudachi {

    static final Logger logger = Common.logger(TestSudachi.class);

    static final Path SUDACHI = Path.of("sudachi-dictionary-20260116");
    static final Path DICTIONARY_FILE = SUDACHI.resolve("system_small.dic");
    static final String TEXT = """
        保険医療機関において初診を行った場合に算定する。
        ただし、別に厚生労働大臣が定める施設基準に適合
        しているものとして地方厚生局長等に届け出た保険医療機関
        において、情報通信機器を用いた初診を行った場合には、253,900点を算定する。
    """;

    static Dictionary dictionary() throws IOException {
        return new DictionaryFactory()
            .create(Config.defaultConfig().systemDictionary(DICTIONARY_FILE));
    }

    @Test
    public void testSucachi() throws IOException {
        Dictionary dictionary = dictionary();
        Tokenizer tokenizer = dictionary.create();
        for (List<Morpheme> morphemes : tokenizer.tokenizeSentences(Tokenizer.SplitMode.C, TEXT.replaceAll("\\s+", "")))
            for (Morpheme m : morphemes)
                logger.info("%s\t%s".formatted(
                    m.surface(), String.join(",", m.partOfSpeech())));
    }

    @Test
    public void test名詞の並び() throws IOException {
        Dictionary dictionary = dictionary();
        Tokenizer tokenizer = dictionary.create();
        StringBuilder noun = new StringBuilder();
        for (List<Morpheme> morphemes : tokenizer.tokenizeSentences(Tokenizer.SplitMode.C, TEXT.replaceAll("\\s+", ""))) {
            for (Morpheme m : morphemes) {
                if (m.partOfSpeech().contains("名詞")) {
                    noun.append(m.surface());
                } else {
                    if (noun.length() > 0) {
                        logger.info(noun.toString());
                        noun.setLength(0);
                    }
                }
            }
        }
        if (noun.length() > 0)
            logger.info(noun.toString());
    }

}
