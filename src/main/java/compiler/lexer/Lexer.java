package compiler.lexer;

import compiler.Token;
import compiler.TokenType;

public class Lexer {
    private final String src;
    private int cursor = 0;
    private int currentLine = 1;
    private int currentColumn = 1;

    // Sub-scanner delegates
    private final StringScanner stringScanner;
    private final CommentSkipper commentSkipper;
    private final KeywordTable keywordTable;
    private final NumberScanner numberScanner;
    private final OperatorScanner operatorScanner;

    public Lexer(String src) {
        this.src = (src != null) ? src : "";
        this.stringScanner = new StringScanner();
        this.commentSkipper = new CommentSkipper();
        this.keywordTable = new KeywordTable();
        this.numberScanner = new NumberScanner();
        this.operatorScanner = new OperatorScanner();
    }

    public Token nextToken() {
        while (cursor < src.length()) {
            char ch = src.charAt(cursor);

            // Handle newline
            if (ch == '\n') {
                currentLine++;
                currentColumn = 1;
                cursor++;
                continue;
            }

            // Handle standard whitespace
            if (Character.isWhitespace(ch)) {
                currentColumn++;
                cursor++;
                continue;
            }

            // 1. Comments
            if (commentSkipper.isCommentStart(src, cursor)) {
                commentSkipper.skipComment(this, src, cursor);
                continue;
            }

            // 2. String literals
            if (ch == '"') {
                return stringScanner.scanString(this, src, cursor);
            }

            // 3. Numbers
            if (Character.isDigit(ch)) {
                return numberScanner.scanNumber(this, src, cursor);
            }

            // 4. Identifiers & Keywords
            if (Character.isLetter(ch) || ch == '_') {
                return keywordTable.scanIdentifierOrKeyword(this, src, cursor);
            }

            // 5. Operators & Punctuation
            if (operatorScanner.isOperatorOrPunctuation(ch)) {
                return operatorScanner.scanOpOrPunct(this, src, cursor);
            }

            // 6. Unknown / Lexical Error fallback
            LexicalError.reportIllegalCharacter(ch, currentLine, currentColumn);
            advanceCursor(1);
        }

        return new Token(TokenType.EOF, "", currentLine, currentColumn);
    }

    public int getCursor() {
        return cursor;
    }

    public int getLine() {
        return currentLine;
    }

    public int getColumn() {
        return currentColumn;
    }

    public String getSource() {
        return src;
    }

    public char peek() {
        return (cursor < src.length()) ? src.charAt(cursor) : '\0';
    }

    public void advanceCursor(int count) {
        this.cursor = Math.min(this.cursor + count, src.length());
        this.currentColumn += count;
    }

    public void rawAdvanceCursorOnly(int count) {
        this.cursor = Math.min(this.cursor + count, src.length());
    }

    public void setLineState(int line, int col) {
        this.currentLine = line;
        this.currentColumn = col;
    }
}

// --------------------------------------------------------------------------
// Package-private placeholder stubs so Lexer.java compiles independently.
// When teammates add their own public classes in this package, these can be removed.
// --------------------------------------------------------------------------

class StringScanner {
    public Token scanString(Lexer lexer, String src, int cursor) {
        return new Token(TokenType.STRING, "", lexer.getLine(), lexer.getColumn());
    }
}

class CommentSkipper {
    public boolean isCommentStart(String src, int cursor) {
        return cursor + 1 < src.length() && src.charAt(cursor) == '/' &&
                (src.charAt(cursor + 1) == '/' || src.charAt(cursor + 1) == '*');
    }

    public void skipComment(Lexer lexer, String src, int cursor) {
        lexer.advanceCursor(2);
    }
}

class KeywordTable {
    public Token scanIdentifierOrKeyword(Lexer lexer, String src, int cursor) {
        return new Token(TokenType.IDENTIFIER, "", lexer.getLine(), lexer.getColumn());
    }
}

class NumberScanner {
    public Token scanNumber(Lexer lexer, String src, int cursor) {
        return new Token(TokenType.NUMBER, "", lexer.getLine(), lexer.getColumn());
    }
}

class OperatorScanner {
    public boolean isOperatorOrPunctuation(char ch) {
        return "+-*/%=<>!&|^~?:;,.()[]{}".indexOf(ch) != -1;
    }

    public Token scanOpOrPunct(Lexer lexer, String src, int cursor) {
        return new Token(TokenType.OPERATOR, String.valueOf(src.charAt(cursor)), lexer.getLine(), lexer.getColumn());
    }
}

class LexicalError {
    public static void reportIllegalCharacter(char ch, int line, int column) {
        System.err.printf("Lexical Error at [%d:%d]: Illegal character '%c'%n", line, column, ch);
    }
}