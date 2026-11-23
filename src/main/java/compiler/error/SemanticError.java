package compiler.error;

/**
 * One semantic mistake: the program parses, but it does not make sense.
 * For example using a variable that was never declared, or adding a
 * string to a number.
 *
 * Owner: Oliver Sedinam Agbenyadzi (0170)
 */
public final class SemanticError {

    private final String message;
    private final int line;
    private final int column;

    public SemanticError(String message, int line, int column) {
        this.message = message;
        this.line = line;
        this.column = column;
    }

    public String getMessage() {
        return message;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return String.format("Semantic Error [Line %d, Col %d]: %s", line, column, message);
    }
}