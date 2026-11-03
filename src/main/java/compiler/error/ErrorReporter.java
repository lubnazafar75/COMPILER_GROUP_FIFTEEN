package compiler.error;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Collects lexical errors instead of crashing, so the scanner can keep going
 * and report every problem in one run (error recovery).
 *
 * Errors are stored, not printed immediately: Main prints them at the end,
 * and the JUnit tests can inspect them directly.
 *
 * Owner: Wrightner Owusu Ansah (0161)
 */
public final class ErrorReporter {

    private final List<LexicalError> errors = new ArrayList<>();

    public void report(LexicalError.Kind kind, String message, int line, int column) {
        errors.add(new LexicalError(kind, message, line, column));
    }

    // ---- Convenience methods used by the scanners -------------------------

    public void illegalCharacter(char c, int line, int column) {
        report(LexicalError.Kind.ILLEGAL_CHARACTER,
                "Illegal character '" + c + "'", line, column);
    }

    public void unterminatedString(int line, int column) {
        report(LexicalError.Kind.UNTERMINATED_STRING,
                "String literal is not closed before end of line", line, column);
    }

    public void unterminatedComment(int line, int column) {
        report(LexicalError.Kind.UNTERMINATED_COMMENT,
                "Block comment '/*' is never closed with '*/'", line, column);
    }

    public void malformedNumber(String text, int line, int column) {
        report(LexicalError.Kind.MALFORMED_NUMBER,
                "Malformed number '" + text + "' (a digit must follow '.')", line, column);
    }

    public void invalidEscape(char c, int line, int column) {
        report(LexicalError.Kind.INVALID_ESCAPE,
                "Unknown escape sequence '\\" + c + "'", line, column);
    }

    // ---- Queries -------------------------------------------------------------

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public int count() {
        return errors.size();
    }

    public List<LexicalError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public void printAll(PrintStream out) {
        for (LexicalError e : errors) {
            out.println(e);
        }
    }
}