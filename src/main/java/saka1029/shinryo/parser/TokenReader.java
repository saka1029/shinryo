package saka1029.shinryo.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenReader {
    static final Pattern COMMENT = Pattern.compile("\\s*#.*");
    static final Pattern FILE_DIRECTIVE = Pattern.compile("\\s*#\\s*file\\s*:\\s*(?<F>.*)\\s+page\\s*:\\s*(?<P>\\d+)\\s*");;
    
    public static List<Token> read(List<TokenType> types, String inTxtFile) throws IOException {
        Token token = null;
        String fileName = null;
        int pageNo = -1, lineNo = 0;
        List<Token> result = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(inTxtFile))) {
            String line = null;
            L: while ((line = reader.readLine()) != null) {
                ++lineNo;
                Matcher matcher = FILE_DIRECTIVE.matcher(line);
                if (matcher.matches()) {
                    fileName = matcher.group("F");
                    pageNo = Integer.parseInt(matcher.group("P"));
                } else if (COMMENT.matcher(line).matches()) {
                    continue L;
                } else {
                    for (TokenType type : types) {
                        Token t = type.match(line, fileName, pageNo, lineNo);
                        if (t != null) {
                            if (token != null)
                                result.add(token);
                            token = t;
                            continue L;
                        }
                    }
                    if (token == null)
                        token = new Token(TokenType.START, null, null, fileName, pageNo, lineNo, 0);
                    token.body().add(line.trim());
                }
            }
            if (token != null)
                result.add(token);
        }
        return result;
    }
}
