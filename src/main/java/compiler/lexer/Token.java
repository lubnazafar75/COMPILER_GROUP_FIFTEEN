package compiler.lexer;

import java.util.Objects;

/**
 * One token produced by the scanner: its type, the exact source text
 * (lexeme), and where it started (1-based line and column).
 *
 * Tokens are immutable so later phases can share them safely.
 *
 * Owner: Philip Essel (0157) - Phase 1 Scanner Lead
 */
public final class Token {

    private final TokenType type;
    private final String lexeme;
    private final int line;
    private final int column;

    public Token(TokenType type, String lexeme, int line, int column) {
        this.type = Objects.requireNonNull(type, "type");
        this.lexeme = lexeme == null ? "" : lexeme;
        this.line = line;
        this.column = column;
    }

    public TokenType getType()   { return type; }
    public String getLexeme()    { return lexeme; }
    public int getLine()         { return line; }
    public int getColumn()       { return column; }

    public boolean is(TokenType expected) {
        return type == expected;
    }

    @Override
    public String toString() {
        return String.format("[%d:%d] %s '%s'", line, column, type, lexeme);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token)) return false;
        Token other = (Token) o;
        return line == other.line && column == other.column
                && type == other.type && lexeme.equals(other.lexeme);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, lexeme, line, column);
    }
}