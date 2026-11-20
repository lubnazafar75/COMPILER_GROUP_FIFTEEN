
package compiler.parser;

import compiler.error.SyntaxErrorReporter;
import compiler.lexer.Token;
import compiler.lexer.TokenType;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Manages token access for the parser, including token consumption,
 * syntax error reporting, and error recovery.
 *
 * Owner: Michael Addo (0159)
 */
public final class TokenStream {

    private static final Set<TokenType> STATEMENT_STARTS = EnumSet.of(
            TokenType.INT, TokenType.STRING, TokenType.INPUT, TokenType.PRINT,
            TokenType.IF, TokenType.WHILE, TokenType.RETURN
    );

    private final List<Token> tokens;
    private final SyntaxErrorReporter errors;
    private int position = 0;

    public TokenStream(List<Token> tokens, SyntaxErrorReporter errors) {
        List<Token> copy = new ArrayList<>(tokens);

        if (copy.isEmpty() || copy.get(copy.size() - 1).getType() != TokenType.EOF) {
            int line = copy.isEmpty() ? 1 : copy.get(copy.size() - 1).getLine();
            int column = copy.isEmpty() ? 1 : copy.get(copy.size() - 1).getColumn();

            copy.add(new Token(TokenType.EOF, "", line, column));
        }

        this.tokens = copy;
        this.errors = errors;
    }

    public Token peek() {
        return tokens.get(position);
    }

    public Token previous() {
        return tokens.get(Math.max(0, position - 1));
    }

    public boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    public int getPosition() {
        return position;
    }

    public Token advance() {
        Token current = peek();

        if (!isAtEnd()) {
            position++;
        }

        return current;
    }

    public boolean check(TokenType type) {
        return peek().getType() == type;
    }

    public boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    public Token consume(TokenType type, String expected) {
        if (check(type)) {
            return advance();
        }

        throw expectedError(expected);
    }

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

    public static String describe(Token token) {
        return token.getType() == TokenType.EOF
                ? "end of file"
                : "'" + token.getLexeme() + "'";
    }

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
