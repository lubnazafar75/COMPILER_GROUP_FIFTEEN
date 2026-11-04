
package compiler.parser;

import compiler.error.SyntaxErrorReporter;
import compiler.lexer.Token;
import compiler.lexer.TokenType;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * A cursor over the scanner's token list. The two parsers use it to look at
 * the next token, consume tokens, and report/recover from errors.
 *
 *   peek()        look at the next token without using it
 *   advance()     use up the next token
 *   match(...)    "if the next token is one of these, use it and say true"
 *   consume(...)  "the next token MUST be this one, otherwise it is an error"
 *
 * Owner: Michael Addo (0159)
 */
public final class TokenStream {

    /** Tokens that can only appear at the start of a statement. */
    private static final Set<TokenType> STATEMENT_STARTS = EnumSet.of(
            TokenType.INT, TokenType.STRING, TokenType.INPUT, TokenType.PRINT,
            TokenType.IF, TokenType.WHILE, TokenType.RETURN
    );

    private final List<Token> tokens;
    private final SyntaxErrorReporter errors;
    private int position = 0;

    public TokenStream(List<Token> tokens, SyntaxErrorReporter errors) {
        List<Token> copy = new ArrayList<>(tokens);

        // Safety net: the list must always end with EOF so peek() never runs off the end.
        if (copy.isEmpty() || copy.get(copy.size() - 1).getType() != TokenType.EOF) {
            int line = copy.isEmpty() ? 1 : copy.get(copy.size() - 1).getLine();
            int column = copy.isEmpty() ? 1 : copy.get(copy.size() - 1).getColumn();

            copy.add(new Token(TokenType.EOF, "", line, column));
        }

        this.tokens = copy;
        this.errors = errors;
    }

    // ---- Looking and moving ---------------------------------------------------

    /** The next token (not yet used). At the end this is the EOF token. */
    public Token peek() {
        return tokens.get(position);
    }

    /** The token that was used most recently. */
    public Token previous() {
        return tokens.get(Math.max(0, position - 1));
    }

    public boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    public int getPosition() {
        return position;
    }

    /** Uses up the next token and returns it. Never moves past EOF. */
    public Token advance() {
        Token current = peek();

        if (!isAtEnd()) {
            position++;
        }

        return current;
    }

    /** True if the next token has this type (the token is not used). */
    public boolean check(TokenType type) {
        return peek().getType() == type;
    }

    /** If the next token has one of these types, use it and return true. */
    public boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    // ---- Errors ---------------------------------------------------------------

    /**
     * The next token must have this type. If it does, use it and return it.
     * If not, record a syntax error and throw ParseException.
     *
     * @param expected plain-English description used in the message,
     *                 e.g. "';' after variable declaration"
     */
    public Token consume(TokenType type, String expected) {
        if (check(type)) {
            return advance();
        }

        throw expectedError(expected);
    }

    /**
     * Records "Expected X, but found Y" and returns the exception to throw.
     *
     * If the offending token is on a LATER line than the previous token
     * (a forgotten ';' is the classic case), the error is reported at the end
     * of the previous token, which is where the programmer must fix it.
     */
    public ParseException expectedError(String expected) {
        Token found = peek();

        String message = "Expected " + expected + ", but found " + describe(found);

        if (position > 0 && found.getLine() > previous().getLine()) {
            Token before = previous();

            errors.report(
                    before.getLine(),
                    before.getColumn() + before.getLexeme().length(),
                    message
            );
        } else {
            errors.report(found, message);
        }

        return new ParseException(message);
    }

    /** How a token is named in error messages. */
    public static String describe(Token token) {
        return token.getType() == TokenType.EOF
                ? "end of file"
                : "'" + token.getLexeme() + "'";
    }

    // ---- Recovery -------------------------------------------------------------

    /**
     * Error recovery ("panic mode"): after a syntax error, skip tokens until
     * we reach a place where a new statement can safely start, so one mistake
     * does not produce a flood of follow-up errors.
     *
     * We stop:
     *   - just after a ';'
     *   - before a keyword that starts a statement (int, print, if, ...)
     *   - before a '}' that closes the block we are inside
     * If we have to skip over a whole { ... } block, we skip it completely.
     */
    public void synchronize() {
        int depth = 0;
        boolean first = true;

        while (!isAtEnd()) {
            TokenType type = peek().getType();

            if (depth == 0) {
                if (!first && previous().getType() == TokenType.SEMICOLON) {
                    return;
                }

                if (type == TokenType.RBRACE || STATEMENT_STARTS.contains(type)) {
                    return;
                }
            }

            if (type == TokenType.LBRACE) {
                depth++;
            } else if (type == TokenType.RBRACE) {
                depth--;
            }

            advance();
            first = false;

            if (type == TokenType.RBRACE && depth == 0) {
                return;
            }
        }
    }
}
