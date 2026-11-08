
package compiler.ast;

/**
 * Represents a print statement.
 */
public record PrintStmt(Expr expression) implements Stmt {

    @Override
    public <R> R accept(Stmt.Visitor<R> visitor) {
        return visitor.visitPrint(this);
    }
}
