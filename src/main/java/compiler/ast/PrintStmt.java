package compiler.ast;

import compiler.lexer.Token;

/**
 * Shows the value of an expression:  print total + 1;
 *
 */
public record PrintStmt(Token keyword, Expr expression) implements Stmt {

    @Override
    public <R> R accept(Stmt.Visitor<R> visitor) {
        return visitor.visitPrint(this);
    }
}
