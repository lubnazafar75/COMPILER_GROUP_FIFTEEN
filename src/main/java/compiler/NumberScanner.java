
package compiler;

import compiler.error.ErrorReporter;



public final class NumberScanner {

    public Token scan(SourceReader reader, ErrorReporter errors) {
        int line = reader.getLine();
        int column = reader.getColumn();
        int start = reader.getPosition();

        consumeDigits(reader);

        if (reader.peek() == '.') {
            if (Character.isDigit(reader.peekNext())) {
                reader.advance();
                consumeDigits(reader);
                String text = reader.text(start, reader.getPosition());
                return new Token(TokenType.FLOAT_LITERAL, text, line, column);
            }

            reader.advance();
            String text = reader.text(start, reader.getPosition());
            errors.malformedNumber(text, line, column);
            return new Token(TokenType.ERROR, text, line, column);
        }

        String text = reader.text(start, reader.getPosition());
        return new Token(TokenType.INT_LITERAL, text, line, column);
    }

    private static void consumeDigits(SourceReader reader) {
        while (Character.isDigit(reader.peek())) {
            reader.advance();
        }
    }
}
