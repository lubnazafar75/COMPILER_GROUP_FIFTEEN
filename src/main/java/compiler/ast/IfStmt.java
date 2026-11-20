package compiler.ast;

import compiler.lexer.Token;

/**
 * A conditional:  if (condition) thenBranch  else elseBranch
 *
 * The else part is optional: when the program has no else, elseBranch is null.
 * Either branch can be a single statement or a { block }.
 *
 */
public record IfStmt(Token keyword, Expr condition, Stmt thenBranch, Stmt elseBranch) implements Stmt {

    @Override
    public <R> R accept(Stmt.Visitor<R> visitor) {
        return visitor.visitIf(this);
    }
}
