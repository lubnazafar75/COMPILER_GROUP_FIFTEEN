package compiler.error;

import compiler.lexer.Token;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Collects semantic errors instead of stopping at the first one, so one
 * run of the compiler reports everything that is wrong with the program.
 *
 * Owner: Oliver Sedinam Agbenyadzi (0170)
 */
public final class SemanticErrorReporter {

    private final List<SemanticError> errors = new ArrayList<>();

    /** Records a mistake at an explicit position. */
    public void report(int line, int column, String message) {
        errors.add(new SemanticError(message, line, column));
    }

    /** Records a mistake at the position of a token. */
    public void report(Token at, String message) {
        errors.add(new SemanticError(message, at.getLine(), at.getColumn()));
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public int count() {
        return errors.size();
    }

    /** The errors in the order they were found. */
    public List<SemanticError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /** Prints one error per line. */
    public void printAll(PrintStream out) {
        for (SemanticError error : errors) {
            out.println(error);
        }
    }
}
