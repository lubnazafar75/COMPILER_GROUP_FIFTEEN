package compiler.ast;

import compiler.lexer.Token;

/**
 * A loop that repeats while its condition is true:  while (n > 0) body
 *
 */
public record WhileStmt(Token keyword, Expr condition, Stmt body) implements Stmt {

    @Override
    public <R> R accept(Stmt.Visitor<R> visitor) {
        return visitor.visitWhile(this);
    }
}
