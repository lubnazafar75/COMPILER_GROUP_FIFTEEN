package compiler.lexer;

import compiler.error.ErrorReporter;

/**
 * Scans integer literals (42) and decimal literals (19.75).
 *
 * "12." with no digit after the dot is reported as a malformed number;
 * the dot is consumed so it does not cause a second error.
 *
 * Owner: Kessina Alex (0160)
 */
public final class NumberScanner {

    public Token scan(SourceReader reader, ErrorReporter errors) {
        int line = reader.getLine();
        int column = reader.getColumn();
        int start = reader.getPosition();

        consumeDigits(reader);

        if (reader.peek() == '.') {
            if (Character.isDigit(reader.peekNext())) {
                reader.advance();            // the '.'
                consumeDigits(reader);       // the fraction
                String text = reader.text(start, reader.getPosition());
                return new Token(TokenType.FLOAT_LITERAL, text, line, column);
            }
            reader.advance();                // consume the stray '.'
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