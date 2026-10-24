package compiler;

enum TokenType {
    KEYWORD, IDENTIFIER, NUMBER, STRING, OPERATOR, PUNCTUATION, EOF
}

class Token {
    private final TokenType type;
    private final String value;
    private final int line;
    private final int column;

    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
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
    public String toString() {
        return String.format("[%d:%d] %s -> '%s'",
                line, column, type, value);
    }
}

class Lexer {
    private final String src;
    private int cursor = 0;
    private int currentLine = 1;
    private int currentColumn = 1;

    private final StringScanner stringScanner = new StringScanner();
    private final CommentSkipper commentSkipper = new CommentSkipper();
    private final KeywordTable keywordTable = new KeywordTable();
    private final NumberScanner numberScanner = new NumberScanner();
    private final OperatorScanner operatorScanner = new OperatorScanner();

    public Lexer(String src) {
        this.src = src;
    }

    public Token nextToken() {
        while (cursor < src.length()) {
            char ch = src.charAt(cursor);

            // Handle newlines and whitespace
            if (ch == '\n') {
                currentLine++;
                currentColumn = 1;
                cursor++;
                continue;
            } else if (Character.isWhitespace(ch)) {
                currentColumn++;
                cursor++;
                continue;
            }

            // Skip comments
            if (commentSkipper.isCommentStart(src, cursor)) {
                commentSkipper.skipComment(this, src, cursor);
                continue;
            }

            // Scan strings
            if (ch == '"') {
                return stringScanner.scanString(this, src, cursor);
            }

            // Scan numbers
            if (Character.isDigit(ch)) {
                return numberScanner.scanNumber(this, src, cursor);
            }

            // Scan identifiers and keywords
            if (Character.isLetter(ch) || ch == '_') {
                return keywordTable.scanIdentifierOrKeyword(
                        this, src, cursor);
            }

            // Scan operators and punctuation
            if (operatorScanner.isOperatorOrPunctuation(ch)) {
                return operatorScanner.scanOpOrPunct(
                        this, src, cursor);
            }

            // Report illegal characters
            LexicalError.reportIllegalCharacter(
                    ch, currentLine, currentColumn);
            advanceCursor(1);
        }

        return new Token(
                TokenType.EOF, "", currentLine, currentColumn);
    }

    public int getLine() {
        return currentLine;
    }

    public int getColumn() {
        return currentColumn;
    }

    public void advanceCursor(int count) {
        this.cursor += count;
        this.currentColumn += count;
    }

    public void setLineState(int line, int col) {
        this.currentLine = line;
        this.currentColumn = col;
    }

    public void rawAdvanceCursorOnly(int count) {
        this.cursor += count;
    }
}
