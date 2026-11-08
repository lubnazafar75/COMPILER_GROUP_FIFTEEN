
package compiler.ast;

import java.util.List;

/**
 * Represents a block containing multiple statements.
 */
public record BlockStmt(List<Stmt> statements) implements Stmt {

    public BlockStmt {
        statements = List.copyOf(statements);
    }

    @Override
    public <R> R accept(Stmt.Visitor<R> visitor) {
        return visitor.visitBlock(this);
    }
}
