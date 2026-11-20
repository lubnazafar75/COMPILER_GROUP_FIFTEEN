package compiler.ast;

import compiler.lexer.Token;

/**
 * Stores a value in an existing variable:  n = n - 1;
 *
 */
public record AssignStmt(Token name, Expr value) implements Stmt {

    @Override
    public <R> R accept(Stmt.Visitor<R> visitor) {
        return visitor.visitAssign(this);
    }
}
