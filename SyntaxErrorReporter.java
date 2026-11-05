package compiler.error;

import compiler.lexer.Token;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class SyntaxErrorReporter {

    private final List<SyntaxError> errors = new ArrayList<>();

    public void report(int line, int column, String message) {
        errors.add(new SyntaxError(message, line, column));
    }

    public void report(Token at, String message) {
        report(at.getLine(), at.getColumn(), message);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public int count() {
        return errors.size();
    }

    public List<SyntaxError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public void printAll(PrintStream out) {
        for (SyntaxError e : errors) {
            out.println(e);
        }
    }
}
