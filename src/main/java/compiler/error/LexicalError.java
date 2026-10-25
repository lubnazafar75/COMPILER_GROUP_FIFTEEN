package compiler.error;

/**
 * One problem found while scanning: what went wrong and where.
 *
 * Owner: Wrightner Owusu Ansah (0161)
 */
public final class LexicalError {

    /** The kinds of lexical problem the scanner can detect. */
    public enum Kind {
        ILLEGAL_CHARACTER,
        UNTERMINATED_STRING,
        UNTERMINATED_COMMENT,
        MALFORMED_NUMBER,
        INVALID_ESCAPE
    }

    private final Kind kind;
    private final String message;
    private final int line;
    private final int column;

    public LexicalError(Kind kind, String message, int line, int column) {
        this.kind = kind;
        this.message = message;
        this.line = line;
        this.column = column;
    }

    public Kind getKind()       { return kind; }
    public String getMessage()  { return message; }
    public int getLine()        { return line; }
    public int getColumn()      { return column; }

    @Override
    public String toString() {
        return String.format("Lexical Error [Line %d, Col %d] %s: %s",
                line, column, kind, message);
    }
}