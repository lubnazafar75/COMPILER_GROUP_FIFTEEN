package compiler.parser;
import compiler.ast.Program;
import compiler.error.SyntaxError;
import compiler.error.SyntaxErrorReporter;
import compiler.lexer.Lexer;
import compiler.util.AstPrinter;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Shared helpers for every Phase 2 test class.
 *
 * Owner: Oliver Sedinam Agbenyadzi (0170)
 */
public final class ParserTestUtil {
    private ParserTestUtil() { }
    /** Scans and parses source text, ignoring any errors. */
    public static Program parse(String source) {
        return new Parser(new Lexer(source).tokenize()).parse();
    }
    /** Scans and parses source text and returns the collected syntax errors. */
    public static SyntaxErrorReporter errorsFor(String source) {
        SyntaxErrorReporter errors = new SyntaxErrorReporter();
        new Parser(new Lexer(source).tokenize(), errors).parse();
        return errors;
    }
    /** The error messages only, in order. */
    public static List<String> messages(String source) {
        return errorsFor(source).getErrors().stream()
                .map(SyntaxError::getMessage)
                .collect(Collectors.toList());
    }
    /** The whole program as printed by AstPrinter. */
    public static String tree(String source) {
        return AstPrinter.print(parse(source));
    }
    /**
     * Parses a single expression (by wrapping it in "print ...;") and returns
     * only the expression's tree, indented from column 0.
     */
    public static String exprTree(String expression) {
        String[] lines = tree("print " + expression + ";").split("\n");
        // lines[0] = "Program", lines[1] = " Print", the expression starts at lines[2]
        StringBuilder result = new StringBuilder();
        for (int i = 2; i < lines.length; i++) {
            result.append(lines[i].substring(4)).append('\n');
        }
        return result.toString();
    }
}