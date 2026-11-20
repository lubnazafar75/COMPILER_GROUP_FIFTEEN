package compiler.ast;

import compiler.lexer.Token;

/**
 * Ends the program (or later, a function), optionally with a value:
 *
 *     return 0;     value is the expression
 *     return;       value is null
 *
 */
public record ReturnStmt(Token keyword, Expr value) implements Stmt {

    @Override
    public <R> R accept(Stmt.Visitor<R> visitor) {
        return visitor.visitReturn(this);
    }
}
