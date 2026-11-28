package compiler.ast;

import compiler.lexer.Token;

public record IfStmt(
        Token keyword,
        Expr condition,
        Stmt thenBranch,
        Stmt elseBranch
) implements Stmt {

    // Keeps compatibility with existing tests and older code.
    public IfStmt(
            Expr condition,
            Stmt thenBranch,
            Stmt elseBranch) {
        this(null, condition, thenBranch, elseBranch);
    }

    @Override
    public <R> R accept(Stmt.Visitor<R> visitor) {
        return visitor.visitIf(this);
    }
}