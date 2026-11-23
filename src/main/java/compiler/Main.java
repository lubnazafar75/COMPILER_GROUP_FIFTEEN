
package compiler;

import compiler.ast.Program;
import compiler.error.ErrorReporter;
import compiler.ir.IrGenerator;
import compiler.ir.IrProgram;
import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.parser.Parser;
import compiler.semantic.SemanticAnalyzer;
import compiler.util.AstPrinter;
import compiler.util.IrPrinter;
import compiler.util.TokenPrinter;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class Main {

    public static final int OK = 0;
    public static final int LEXICAL_ERRORS = 1;
    public static final int USAGE_ERROR = 2;
    public static final int IO_ERROR = 3;
    public static final int SYNTAX_ERRORS = 4;
    public static final int SEMANTIC_ERRORS = 5;

    private static final List<String> MODES =
            List.of("--tokens", "--ast", "--check", "--ir");

    public static void main(String[] args) {
        System.exit(run(args, System.out, System.err));
    }

    public static int run(String[] args, PrintStream out, PrintStream err) {

        if (args.length != 2 || !MODES.contains(args[0])) {
            printUsage(err);
            return USAGE_ERROR;
        }

        String mode = args[0];
        String source;

        try {
            source = Files.readString(
                    Path.of(args[1]), StandardCharsets.UTF_8);
        } catch (IOException e) {
            err.println("Error: cannot read file '" + args[1]
                    + "' (" + e.getMessage() + ")");
            return IO_ERROR;
        }

        out.println("Source file: " + args[1]);

        // Phase 1: scanning
        ErrorReporter lexicalErrors = new ErrorReporter();
        List<Token> tokens = new Lexer(source, lexicalErrors).tokenize();

        if (mode.equals("--tokens")) {
            return showTokens(tokens, lexicalErrors, out, err);
        }

        if (lexicalErrors.hasErrors()) {
            err.println(lexicalErrors.count()
                    + " lexical error(s) found - fix these first:");
            lexicalErrors.printAll(err);
            return LEXICAL_ERRORS;
        }

        // Phase 2: parsing
        Parser parser = new Parser(tokens);
        Program program = parser.parse();

        if (parser.getErrors().hasErrors()) {
            err.println(parser.getErrors().count()
                    + " syntax error(s) found:");

            parser.getErrors().getErrors().forEach(err::println);

            return SYNTAX_ERRORS;
        }

        if (mode.equals("--ast")) {
            AstPrinter printer = new AstPrinter();
            out.print(printer.print(program));
            out.println("Parse completed with no syntax errors.");
            return OK;
        }

        // Phase 3: semantic analysis
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(program);

        if (analyzer.getErrors().hasErrors()) {
            err.println(analyzer.getErrors().count()
                    + " semantic error(s) found:");
            analyzer.getErrors().printAll(err);
            return SEMANTIC_ERRORS;
        }

        if (mode.equals("--check")) {
            out.println("Semantic analysis completed with no errors.");
            return OK;
        }

        // Phase 3: intermediate representation
        IrProgram ir = new IrGenerator().generate(program);
        out.print(IrPrinter.print(ir));
        out.println("Generated " + ir.size()
                + " three-address instruction(s).");

        return OK;
    }

    private static int showTokens(
            List<Token> tokens,
            ErrorReporter lexicalErrors,
            PrintStream out,
            PrintStream err) {

        TokenPrinter.print(tokens, out);

        if (lexicalErrors.hasErrors()) {
            err.println();
            err.println(lexicalErrors.count()
                    + " lexical error(s) found:");
            lexicalErrors.printAll(err);
            return LEXICAL_ERRORS;
        }

        out.println("Scan completed with no lexical errors.");
        return OK;
    }

    private static void printUsage(PrintStream err) {
        err.println("Usage: java -jar compiler-group-fifteen.jar --tokens <file.mini>");
        err.println("       java -jar compiler-group-fifteen.jar --ast    <file.mini>");
        err.println("       java -jar compiler-group-fifteen.jar --check  <file.mini>");
        err.println("       java -jar compiler-group-fifteen.jar --ir     <file.mini>");
    }
}
