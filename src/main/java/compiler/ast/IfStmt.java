
package compiler.ast;

/**
 * Represents an if statement with an optional else branch.
 */
public record IfStmt(
        Expr condition,
        Stmt thenBranch,
        Stmt elseBranch
) implements Stmt {

    @Override
    public <R> R accept(Stmt.Visitor<R> visitor) {
        return visitor.visitIf(this);
    }
}
