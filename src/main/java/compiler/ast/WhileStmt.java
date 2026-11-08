
package compiler.ast;

/**
 * Represents a while loop.
 */
public record WhileStmt(
        Expr condition,
        Stmt body
) implements Stmt {

    @Override
    public <R> R accept(Stmt.Visitor<R> visitor) {
        return visitor.visitWhile(this);
    }
}
