package compiler.error;


import compiler.lexer.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Collects syntax errors encountered during parsing.
 */
public class SyntaxErrorReporter {

    private final List<SyntaxError> errors = new ArrayList<>();

    public void report(int line, int column, String message) {
        errors.add(new SyntaxError(message, line, column));
    }

    public void report(Token token, String message) {
        report(token.getLine(), token.getColumn(), message);
    }

    public List<SyntaxError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public int count() {
        return errors.size();
    }

    public void clear() {
        errors.clear();
    }
}
