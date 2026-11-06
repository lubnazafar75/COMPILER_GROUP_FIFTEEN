package compiler.ast;

/**
 * An expression wrapped in parentheses: (a + b).
 * Keeping this node records that the programmer wrote the brackets.
 *
 * Owner: Egmine James (0163)
 */
public record GroupingExpr(Expr expression) implements Expr {

    @Override
    public <R> R accept(Expr.Visitor<R> visitor) {
        return visitor.visitGrouping(this);
    }
}
