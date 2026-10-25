package compiler;

import java.util.Objects;

public class Token {
    private final TokenType type;
    private final String value;
    private final int line;
    private final int column;

    public Token(TokenType type, String value, int line, int column) {
        this.type = Objects.requireNonNull(type, "TokenType cannot be null");
        this.value = value == null ? "" : value;
        this.line = line;
        this.column = column;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token token)) return false;
        return line == token.line &&
                column == token.column &&
                type == token.type &&
                Objects.equals(value, token.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value, line, column);
    }

    @Override
    public String toString() {
        return String.format("[%d:%d] %s -> '%s'", line, column, type, value);
    }
}