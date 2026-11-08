
package compiler.error;

/**
 * Represents one syntax error reported by the parser.
 */
public record SyntaxError(String message, int line, int column) {

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
        return "Line " + line + ", Col " + column + ": " + message;
    }
}
