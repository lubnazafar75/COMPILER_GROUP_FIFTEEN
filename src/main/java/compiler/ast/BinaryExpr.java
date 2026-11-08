
package compiler.ast;

import compiler.lexer.Token;

/**
 * An expression with two operands and an operator between them.
 * Examples: a + b, total >= 10 && n != 0.
 *
 * Owner: Egmine James (0163)
 */
public record BinaryExpr(
        Expr left,
        Token operator,
        Expr right
) implements Expr {

    @Override
    public <R> R accept(Expr.Visitor<R> visitor) {
        return visitor.visitBinary(this);
    }
}
