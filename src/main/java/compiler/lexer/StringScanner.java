package compiler.lexer;

import compiler.error.ErrorReporter;

public final class StringScanner {
    public Token scan(SourceReader reader, ErrorReporter errors) {
        int line = reader.getLine();
        int column = reader.getColumn();
        int start = reader.getPosition();
        reader.advance();

        while (!reader.isAtEnd() && reader.peek() != '"' && reader.peek() != '\n') {
            if (reader.peek() == '\\') {
                int escLine = reader.getLine();
                int escCol = reader.getColumn();
                reader.advance();
                char next = reader.peek();
                if (next == '"'||  next == '\\' || next == 'n' || next == 't') {
                    reader.advance();
                } 
                else if (next != '\n' && next != SourceReader.END) {
                    errors.invalidEscape(next, escLine, escCol);
                    reader.advance();
                }
            } 
            else {
                reader.advance();
            }
        }
        if (reader.peek() != '"') {
            errors.unterminatedString(line, column);
            return new Token(TokenType.ERROR, reader.text(start, reader.getPosition()), line, column);
        }
        reader.advance();
        return new Token(TokenType.STRING_LITERAL, reader.text(start, reader.getPosition()), line, column);
    }
}
