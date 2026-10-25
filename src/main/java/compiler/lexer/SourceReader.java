package compiler.lexer;
/**
 * Walks through the source text one character at a time and keeps the
 * current line and column up to date.
 *
 * Every scanner moves through the text ONLY by calling {@link #advance()},
 * so line/column tracking lives in exactly one place and cannot drift,
 * even across newlines inside strings or block comments.
 *
 * Owner: Philip Essel (0157) - Phase 1 Scanner Lead
 */
public final class SourceReader {
    /** Returned by peek methods when there is nothing left to read. */
    public static final char END = '\0';
    private final String source;
    private int position = 0;
    private int line = 1;
    private int column = 1;
    public SourceReader(String source) {
        this.source = source == null ? "" : source;
    }
    public boolean isAtEnd() {
        return position >= source.length();
    }
    /** Current character without consuming it. */
    public char peek() {
        return isAtEnd() ? END : source.charAt(position);
    }
    /** Character after the current one, without consuming anything. */
    public char peekNext() {
        return position + 1 >= source.length() ? END : source.charAt(position + 1);
    }
    /** Consumes the current character and updates line/column. */
    public char advance() {
        char c = source.charAt(position++);
        if (c == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        return c;
    }
    /** Consumes the current character only if it equals {@code expected}. */
    public boolean match(char expected) {
        if (isAtEnd() || source.charAt(position) != expected) {
            return false;
        }
        advance();
        return true;
    }
    public int getPosition() { return position; }
    public int getLine() { return line; }
    public int getColumn() { return column; }
    /** Source text between two positions (start inclusive, end exclusive). */
    public String text(int start, int end) {
        return source.substring(start, end);
    }
}