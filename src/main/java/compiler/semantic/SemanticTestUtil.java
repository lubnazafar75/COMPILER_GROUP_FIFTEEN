package compiler.semantic;

import compiler.ast.PrintStmt;
import compiler.ast.Program;
import compiler.ast.Stmt;
import compiler.error.ErrorReporter;
import compiler.error.SemanticErrorReporter;
import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.parser.Parser;

import java.util.List;

public final class SemanticTestUtil {

    private SemanticTestUtil() {
    }

    public static SemanticErrorReporter check(String source) {
        Program program = parse(source);

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(program);

        return analyzer.getErrors();
    }

    public static List<String> messages(String source) {
        return check(source).getErrors()
                .stream()
                .map(error -> error.getMessage())
                .toList();
    }

    public static Type typeOf(String expression) {
        return typeOf("", expression);
    }

    public static Type typeOf(String declarations, String expression) {
        Program program = parse(declarations + "\nprint " + expression + ";");

        SymbolTable symbols = new SymbolTable();
        SemanticErrorReporter errors = new SemanticErrorReporter();
        TypeChecker checker = new TypeChecker(symbols, errors);
        DeclarationChecker declarationChecker =
                new DeclarationChecker(symbols, checker, errors);

        List<Stmt> statements = program.statements();

        for (int i = 0; i < statements.size() - 1; i++) {
            declarationChecker.check(statements.get(i));
        }

        Stmt last = statements.get(statements.size() - 1);

        if (!(last instanceof PrintStmt)) {
            throw new IllegalStateException(
                    "Expected the final statement to be a print statement."
            );
        }

        return checker.typeOf(((PrintStmt) last).expression());
    }

    private static Program parse(String source) {
        ErrorReporter lexicalErrors = new ErrorReporter();

        List<Token> tokens = new Lexer(source, lexicalErrors).tokenize();

        if (lexicalErrors.hasErrors()) {
            throw new IllegalArgumentException(
                    "Test source contains lexical errors."
            );
        }

        Parser parser = new Parser(tokens);
        Program program = parser.parse();

        if (parser.getErrors().hasErrors()) {
            throw new IllegalArgumentException(
                    "Test source contains syntax errors."
            );
        }

        return program;
    }
}