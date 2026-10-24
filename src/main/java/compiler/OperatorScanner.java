
package compiler;




public final class OperatorScanner {

    private static final String SINGLE_CHARS = "+-*/%=!<>(){}[];,";

    /** True if the current character can begin an operator or punctuation token. */
    public boolean canStart(SourceReader reader) {
        char c = reader.peek();

        if (SINGLE_CHARS.indexOf(c) >= 0) {
            return true;
        }

        return (c == '&' && reader.peekNext() == '&')
                || (c == '|' && reader.peekNext() == '|');
    }

    public Token scan(SourceReader reader) {
        int line = reader.getLine();
        int column = reader.getColumn();
        char c = reader.advance();

        TokenType type;
        String text;

        switch (c) {
            case '+': type = TokenType.PLUS; text = "+"; break;
            case '-': type = TokenType.MINUS; text = "-"; break;
            case '*': type = TokenType.STAR; text = "*"; break;
            case '/': type = TokenType.SLASH; text = "/"; break;
            case '%': type = TokenType.PERCENT; text = "%"; break;
            case '(': type = TokenType.LPAREN; text = "("; break;
            case ')': type = TokenType.RPAREN; text = ")"; break;
            case '{': type = TokenType.LBRACE; text = "{"; break;
            case '}': type = TokenType.RBRACE; text = "}"; break;
            case '[': type = TokenType.LBRACKET; text = "["; break;
            case ']': type = TokenType.RBRACKET; text = "]"; break;
            case ';': type = TokenType.SEMICOLON; text = ";"; break;
            case ',': type = TokenType.COMMA; text = ","; break;

            case '=':
                if (reader.match('=')) {
                    type = TokenType.EQUAL_EQUAL;
                    text = "==";
                } else {
                    type = TokenType.ASSIGN;
                    text = "=";
                }
                break;

            case '!':
                if (reader.match('=')) {
                    type = TokenType.NOT_EQUAL;
                    text = "!=";
                } else {
                    type = TokenType.NOT;
                    text = "!";
                }
                break;

            case '<':
                if (reader.match('=')) {
                    type = TokenType.LESS_EQUAL;
                    text = "<=";
                } else {
                    type = TokenType.LESS;
                    text = "<";
                }
                break;

            case '>':
                if (reader.match('=')) {
                    type = TokenType.GREATER_EQUAL;
                    text = ">=";
                } else {
                    type = TokenType.GREATER;
                    text = ">";
                }
                break;

            case '&':
                reader.advance();
                type = TokenType.AND;
                text = "&&";
                break;

            case '|':
                reader.advance();
                type = TokenType.OR;
                text = "||";
                break;

            default:
                type = TokenType.ERROR;
                text = String.valueOf(c);
        }

        return new Token(type, text, line, column);
    }
}
