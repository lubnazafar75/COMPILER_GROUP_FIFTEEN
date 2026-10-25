package compiler.lexer;

import java.util.Map;

/**
 * Scans identifiers and decides whether each word is a reserved keyword.
 *
 * Rule: an identifier starts with a letter or '_' and continues with
 * letters, digits or '_'. Keywords are case-sensitive ("if" is a keyword,
 * "If" is an identifier).
 *
 * To add a keyword: add a constant to TokenType, then one entry below.
 *
 * Owner: Michael Addo (0159)
 */
public final class KeywordTable {

    private static final Map<String, TokenType> KEYWORDS = Map.of(
            "if",     TokenType.IF,
            "else",   TokenType.ELSE,
            "while",  TokenType.WHILE,
            "return", TokenType.RETURN,
            "int",    TokenType.INT,
            "string", TokenType.STRING,
            "print",  TokenType.PRINT,
            "input",  TokenType.INPUT
    );

    public static boolean isIdentifierStart(char c) {
        return Character.isLetter(c) || c == '_';
    }

    public static boolean isIdentifierPart(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    /** Returns the keyword type for a word, or IDENTIFIER if it is not reserved. */
    public static TokenType lookup(String word) {
        return KEYWORDS.getOrDefault(word, TokenType.IDENTIFIER);
    }

    public static boolean isKeyword(String word) {
        return KEYWORDS.containsKey(word);
    }

    /** Reads one identifier/keyword starting at the reader's current position. */
    public Token scan(SourceReader reader) {
        int line = reader.getLine();
        int column = reader.getColumn();
        int start = reader.getPosition();

        while (!reader.isAtEnd() && isIdentifierPart(reader.peek())) {
            reader.advance();
        }

        String word = reader.text(start, reader.getPosition());
        return new Token(lookup(word), word, line, column);
    }
}
