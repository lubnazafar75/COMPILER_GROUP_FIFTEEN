package compiler.ast;

import compiler.lexer.Token;

/**
 * An expression with two operands and an operator between them,
 * for example a + b or total >= 10 && n != 0.
 *
 * A record is a short way of writing a class that only holds data.
 * Java automatically generates the constructor, getters
 * (left(), operator(), right()), equals(), hashCode(), and toString()
 * for us.
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