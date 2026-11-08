
package compiler;

import compiler.ast.Program;
import compiler.error.ErrorReporter;
import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.parser.Parser;
import compiler.util.AstPrinter;
import compiler.util.TokenPrinter;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Command-line entry point for the compiler.
 *
 * Usage:
 * java -jar compiler-group-fifteen.jar --tokens file.mini
 * java -jar compiler-group-fifteen.jar --ast file.mini
 *
 * Exit codes:
 * 0 = success
 * 1 = lexical errors
 * 2 = wrong arguments
 * 3 = file could not be read
 * 4 = syntax errors
 */
public final class Main {

    public static final int OK = 0;
    public static final int LEXICAL_ERRORS = 1;
    public static final int USAGE_ERROR = 2;
    public static final int IO_ERROR = 3;
    public static final int SYNTAX_ERRORS = 4;

    public static void main(String[] args) {
        System.exit(run(args, System.out, System.err));
    }

    /**
     * Separated from main() so it can be tested without exiting the JVM.
     */
    public static int run(String[] args, PrintStream out, PrintStream err) {

        if (args.length != 2 ||
                !(args[0].equals("--tokens") || args[0].equals("--ast"))) {
            printUsage(err);
            return USAGE_ERROR;
        }

        String mode = args[0];
        String source;

        try {
            source = Files.readString(
                    Path.of(args[1]),
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            err.println("Error: cannot read file '" + args[1] +
                    "' (" + e.getMessage() + ")");
            return IO_ERROR;
        }

        // Phase 1: scanning
        ErrorReporter lexicalErrors = new ErrorReporter();
        List<Token> tokens = new Lexer(source, lexicalErrors).tokenize();

        if (mode.equals("--tokens")) {
            return showTokens(args[1], tokens, lexicalErrors, out, err);
        }

        return showTree(args[1], tokens, lexicalErrors, out, err);
    }

    private static int showTokens(
            String file,
            List<Token> tokens,
            ErrorReporter lexicalErrors,
            PrintStream out,
            PrintStream err) {

        out.println("Source file: " + file);
        TokenPrinter.print(tokens, out);

        if (lexicalErrors.hasErrors()) {
            err.println();
            err.println(lexicalErrors.count() + " lexical error(s) found:");
            lexicalErrors.printAll(err);
            return LEXICAL_ERRORS;
        }

        out.println("Scan completed with no lexical errors.");
        return OK;
    }

    private static int showTree(
            String file,
            List<Token> tokens,
            ErrorReporter lexicalErrors,
            PrintStream out,
            PrintStream err) {

        out.println("Source file: " + file);

        // Do not parse a program the scanner already rejected.
        if (lexicalErrors.hasErrors()) {
            err.println(lexicalErrors.count() +
                    " lexical error(s) found - fix these before parsing:");
            lexicalErrors.printAll(err);
            return LEXICAL_ERRORS;
        }

        // Phase 2: parsing
        Parser parser = new Parser(tokens);
        Program program = parser.parse();

        if (parser.getErrors().hasErrors()) {
            err.println(parser.getErrors().count() +
                    " syntax error(s) found:");

            parser.getErrors().getErrors().forEach(err::println);

            return SYNTAX_ERRORS;
        }

        out.print(new AstPrinter().print(program));
        out.println("Parse completed with no syntax errors.");

        return OK;
    }

    private static void printUsage(PrintStream err) {
        err.println("Usage: java -jar compiler-group-fifteen.jar --tokens file.mini");
        err.println("       java -jar compiler-group-fifteen.jar --ast    file.mini");
    }
}
