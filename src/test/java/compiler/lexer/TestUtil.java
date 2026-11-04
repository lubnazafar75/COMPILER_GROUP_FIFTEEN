package compiler.lexer;

import compiler.error.ErrorReporter;
import java.util.List;
import java.util.stream.Collectors;

public final class TestUtil {

    private TestUtil() {
    }

    public static List<Token> tokenize(String source) {
        return new Lexer(source).tokenize();
    }

    public static ErrorReporter errorsFor(String source) {
        ErrorReporter errors = new ErrorReporter();
        new Lexer(source, errors).tokenize();
        return errors;
    }

    public static List<TokenType> types(String source) {
        return tokenize(source)
                .stream()
                .map(Token::getType)
                .collect(Collectors.toList());
    }

    public static List<String> lexemes(String source) {
        return tokenize(source)
                .stream()
                .map(Token::getLexeme)
                .collect(Collectors.toList());
    }
}
