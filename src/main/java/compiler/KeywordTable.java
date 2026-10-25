package compiler;

import java.util.Set;

class KeywordTable {

    private static final Set<String> KEYWORDS = Set.of(
        "if",
        "else",
        "while",
        "return",
        "int",
        "string",
        "print",
        "input"
    );

    public Token scanIdentifierOrKeyword(
            Lexer lexer, String src, int cursor) {

        int start = cursor;
        int line = lexer.getLine();
        int column = lexer.getColumn();

        while (cursor < src.length()) {
            char ch = src.charAt(cursor);

            if (Character.isLetterOrDigit(ch) || ch == '_') {
                cursor++;
            } else {
                break;
            }
        }

        String word = src.substring(start, cursor);

        lexer.advanceCursor(cursor - start);

        TokenType type = KEYWORDS.contains(word)
                ? TokenType.KEYWORD
                : TokenType.IDENTIFIER;

        return new Token(type, word, line, column);
    }
}
