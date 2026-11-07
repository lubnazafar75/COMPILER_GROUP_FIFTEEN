package compiler.parser;

/**
 * Thrown inside the parser when the tokens do not match the grammar.
 *
 * It is used only to jump out of the half-finished statement; the error
 * itself has already been recorded in the SyntaxErrorReporter. The parser
 * catches it, skips ahead to a safe place (see TokenStream.synchronize())
 * and carries on with the next statement.
 *
 * The stack trace is switched off because it is never shown to the user.
 *
 * Owner: Oliver Sedinam Agbenyadzi (0170)
 */
public final class ParseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ParseException(String message) {
        super(message, null, false, false);
    }
}