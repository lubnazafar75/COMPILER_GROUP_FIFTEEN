
package compiler.ast;

/**
 * Represents a return statement with an optional expression.
 */
public record ReturnStmt(Expr value) implements Stmt {

    @Override
    public <R> R accept(Stmt.Visitor<R> visitor) {
        return visitor.visitReturn(this);
    }
}
