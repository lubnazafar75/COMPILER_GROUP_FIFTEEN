
package compiler.parser;

import compiler.ast.Program;
import compiler.error.SyntaxErrorReporter;
import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.util.AstPrinter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper methods used by parser tests.
 */
public final class ParserTestUtil {

    private ParserTestUtil() {
    }

    public static SyntaxErrorReporter errorsFor(String source) {
        List<Token> tokens = new Lexer(source).tokenize();

        Parser parser = new Parser(tokens);
        parser.parse();

        return parser.getErrors();
    }

    public static List<String> messages(String source) {
        return errorsFor(source).getErrors().stream()
                .map(error -> error.getMessage())
                .collect(Collectors.toList());
    }

    public static String tree(String source) {
        List<Token> tokens = new Lexer(source).tokenize();

        Parser parser = new Parser(tokens);
        Program program = parser.parse();

        return new AstPrinter().print(program);
    }
}
