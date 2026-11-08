package compiler.ast;


import compiler.lexer.Token;

/**
 * Represents assigning a value to a variable.
 */
public record AssignStmt(
        Token name,
        Expr value
) implements Stmt {

    @Override
    public <R> R accept(Stmt.Visitor<R> visitor) {
        return visitor.visitAssign(this);
    }
}
