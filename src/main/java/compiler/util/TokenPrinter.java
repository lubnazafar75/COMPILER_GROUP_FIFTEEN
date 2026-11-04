package compiler.util;
import compiler.lexer.Token;
import java.io.PrintStream;
import java.util.List;
/**
 * Prints the token list as a readable table for the --tokens report.
 * Sample output:
 *   LINE  COL   CATEGORY      TYPE            LEXEME
 *   ----  ----  ------------  --------------  ---------
 *      1     1  KEYWORD       INT             int
 */
public final class TokenPrinter {
    private static final String ROW = "%4d  %4d  %-12s  %-14s  %s%n";
    private static final String RULE =
            "------------------------------------------------------------------";
    private TokenPrinter() { }
    public static void print(List<Token> tokens, PrintStream out) {
        out.println(RULE);
        out.println("                    LEXICAL ANALYSIS - TOKEN REPORT");
        out.println(RULE);
        out.printf("%4s  %4s  %-12s  %-14s  %s%n", "LINE", "COL", "CATEGORY", "TYPE", "LEXEME");
        out.printf("%4s  %4s  %-12s  %-14s  %s%n", "----", "----", "------------", "--------------", "----------");
        for (Token t : tokens) {
            out.printf(ROW,
                    t.getLine(),
                    t.getColumn(),
                    t.getType().getCategory(),
                    t.getType(),
                    t.getLexeme());
        }
        out.println(RULE);
        out.println("Total tokens (including EOF): " + tokens.size());
    }
}