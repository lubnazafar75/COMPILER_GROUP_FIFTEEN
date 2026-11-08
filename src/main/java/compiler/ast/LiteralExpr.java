
package compiler.ast;

import compiler.lexer.Token;
import compiler.lexer.TokenType;

/**
 * A literal value written directly in the program:
 * 42, 19.75 or "hello".
 *
 * The original token is retained so later phases know
 * the exact text and source position.
 *
 * Owner: Egmine James (0163)
 */
public record LiteralExpr(Token token) implements Expr {

    /**
     * Returns INT_LITERAL, FLOAT_LITERAL or STRING_LITERAL.
     */
    public TokenType type() {
        return token.getType();
    }

    /**
     * Returns the literal exactly as written in the source.
     */
    public String text() {
        return token.getLexeme();
    }

    @Override
    public <R> R accept(Expr.Visitor<R> visitor) {
        return visitor.visitLiteral(this);
    }
}
