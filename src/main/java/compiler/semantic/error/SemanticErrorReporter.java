
package compiler.error;

import compiler.lexer.Token;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SemanticErrorReporter {

    private final List<SemanticError> errors = new ArrayList<>();

    public void report(int line, int column, String message) {
        errors.add(new SemanticError(message, line, column));
    }

    public void report(Token token, String message) {
        report(token.getLine(), token.getColumn(), message);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public int count() {
        return errors.size();
    }

    public List<SemanticError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public void printAll() {
        for (SemanticError error : errors) {
            System.err.println(error);
        }
    }

    public void printAll(java.io.PrintStream out) {
        for (SemanticError error : errors) {
            out.println(error);
        }
    }
}
