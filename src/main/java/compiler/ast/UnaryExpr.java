
package compiler.ast;

import compiler.lexer.Token;

/**
 * An expression with one operand and a prefix operator.
 * Examples: -x or !done.
 *
 * Owner: Egmine James (0163)
 */
public record UnaryExpr(
        Token operator,
        Expr operand
) implements Expr {

    @Override
    public <R> R accept(Expr.Visitor<R> visitor) {
        return visitor.visitUnary(this);
    }
}
