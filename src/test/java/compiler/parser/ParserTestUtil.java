
package compiler.parser;

import compiler.ast.Expr;
import compiler.ast.Program;
import compiler.error.SyntaxErrorReporter;
import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.util.AstPrinter;

import java.util.List;
import java.util.stream.Collectors;

public final class ParserTestUtil {

    private ParserTestUtil() {
    }

    public static Program parse(String source) {
        List<Token> tokens = new Lexer(source).tokenize();

        Parser parser = new Parser(tokens);
        return parser.parse();
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
        Program program = parse(source);
        return new AstPrinter().print(program);
    }

    public static String exprTree(String source) {
        List<Token> tokens = new Lexer(source).tokenize();

        SyntaxErrorReporter errors = new SyntaxErrorReporter();
        TokenStream tokenStream = new TokenStream(tokens, errors);

        Expr expression = new ExpressionParser(tokenStream).parseExpression();

        return new AstPrinter().printExpression(expression);
    }
}
