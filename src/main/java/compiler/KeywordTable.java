
package compiler;

import java.util.Set;

class KeywordTable {

    //this stores all reserved keywords supported by the language.
    //set provides fast membership checking and avoids duplicates.
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

    //reads a word and determines whether it is a keyword or identifier.
    public Token scanIdentifierOrKeyword(
            Lexer lexer, String src, int cursor) {

        int start = cursor;
        int line = lexer.getLine();
        int column = lexer.getColumn();

        //it continues reading while the character is valid in a word.
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

        //classifies the word as a keyword or an identifier.
        TokenType type = classifyWord(word);

        return new Token(type, word, line, column);
    }

    private TokenType classifyWord(String word) {
        if (KEYWORDS.contains(word)) {
            return TokenType.KEYWORD;
        }

        return TokenType.IDENTIFIER;
    }
}
