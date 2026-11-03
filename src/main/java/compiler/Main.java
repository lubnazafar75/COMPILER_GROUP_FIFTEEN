package compiler;

import compiler.error.ErrorReporter;
import compiler.lexer.Lexer;
import compiler.lexer.Token;
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
 *   Usage:  java -jar compiler-group-fifteen.jar --tokens <file.mini>
 *   In IntelliJ: Run > Edit Configurations > Program arguments:
 *           --tokens samples/hello.mini
 *
 * Exit codes: 0 = success, 1 = lexical errors found,
 *             2 = wrong arguments, 3 = file could not be read.
 *
 * Later phases add more modes here (e.g. --parse, --ir, --run).
 *
 * Owner: Mercy Atta Gyamfi (0164) - Team Lead
 */
public final class Main {

    public static final int OK = 0;
    public static final int LEXICAL_ERRORS = 1;
    public static final int USAGE_ERROR = 2;
    public static final int IO_ERROR = 3;

    public static void main(String[] args) {
        System.exit(run(args, System.out, System.err));
    }

    /** Separated from main() so it can be tested without exiting the JVM. */
    public static int run(String[] args, PrintStream out, PrintStream err) {
        if (args.length != 2 || !args[0].equals("--tokens")) {
            printUsage(err);
            return USAGE_ERROR;
        }

        String source;
        try {
            source = Files.readString(Path.of(args[1]), StandardCharsets.UTF_8);
        } catch (IOException e) {
            err.println("Error: cannot read file '" + args[1] + "' (" + e.getMessage() + ")");
            return IO_ERROR;
        }

        ErrorReporter errors = new ErrorReporter();
        Lexer lexer = new Lexer(source, errors);
        List<Token> tokens = lexer.tokenize();

        out.println("Source file: " + args[1]);
        TokenPrinter.print(tokens, out);

        if (errors.hasErrors()) {
            err.println();
            err.println(errors.count() + " lexical error(s) found:");
            errors.printAll(err);
            return LEXICAL_ERRORS;
        }
        out.println("Scan completed with no lexical errors.");
        return OK;
    }

    private static void printUsage(PrintStream err) {
        err.println("Usage: java -jar compiler-group-fifteen.jar --tokens <file.mini>");
    }
}