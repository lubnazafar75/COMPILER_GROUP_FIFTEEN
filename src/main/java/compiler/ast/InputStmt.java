package compiler.ast;

import compiler.lexer.Token;

/**
 * Reads a value from the user into a variable:  input n;
 *
 */
public record InputStmt(Token name) implements Stmt {

    @Override
    public <R> R accept(Stmt.Visitor<R> visitor) {
        return visitor.visitInput(this);
    }
}
