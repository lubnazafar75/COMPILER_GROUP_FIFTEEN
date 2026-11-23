package compiler.semantic;
import compiler.ast.Program;
import compiler.error.SemanticError;
import compiler.error.SemanticErrorReporter;
import compiler.ir.IrGenerator;
import compiler.lexer.Lexer;
import compiler.parser.Parser;
import compiler.util.IrPrinter;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Shared helpers for every Phase 3 test class.
 *
 * Each helper takes mini-language source text and runs the whole chain
 * in front of it (scanner, parser, and for the IR helpers the checker
 * too), so a test can be one line.
 *
 * Owner: Oliver Sedinam Agbenyadzi (0170)
 */
public final class SemanticTestUtil {
    private SemanticTestUtil() { }
    /** Scans and parses source text into a syntax tree. */
    public static Program parse(String source) {
        return new Parser(new Lexer(source).tokenize()).parse();
    }
    /** Runs the semantic analyzer and returns the collected errors. */
    public static SemanticErrorReporter check(String source) {
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(parse(source));
        return analyzer.getErrors();
    }
    /** The semantic error messages only, in order. */
    public static List<String> messages(String source) {
        return check(source).getErrors().stream()
                .map(SemanticError::getMessage)
                .collect(Collectors.toList());
    }
    /** True if the program has no semantic errors at all. */
    public static boolean isClean(String source) {
        return !check(source).hasErrors();
    }
    /** The type of a single expression, worked out in an empty scope. */
    public static Type typeOf(String expression) {
        return typeOf("", expression);
    }
    /**
     * The type of an expression, after running some declarations first.
     *
     *     typeOf("int a = 1;", "a + 2")   ->   INT
     */
    public static Type typeOf(String declarations, String expression) {
        SemanticErrorReporter errors = new SemanticErrorReporter();
        SymbolTable symbols = new SymbolTable();
        TypeChecker types = new TypeChecker(symbols, errors);
        DeclarationChecker checker = new DeclarationChecker(symbols, types, errors);
       Program setup = parse(declarations + "\nprint " + expression + ";");
        // Check every statement except the last, which holds the expression.
        List<compiler.ast.Stmt> statements = setup.statements();
        for (int i = 0; i < statements.size() - 1; i++) {
            checker.check(statements.get(i));
        }
        compiler.ast.PrintStmt last =
                (compiler.ast.PrintStmt) statements.get(statements.size() - 1);
        return types.typeOf(last.expression());
    }
    /** The three-address code for a program, as printed text. */
    public static String ir(String source) {
        return IrPrinter.print(new IrGenerator().generate(parse(source)));
    }
    /** The three-address code with the lines trimmed, for compact tests. */
    public static List<String> irLines(String source) {
        return ir(source).lines().map(String::trim).collect(Collectors.toList());
    }
}
