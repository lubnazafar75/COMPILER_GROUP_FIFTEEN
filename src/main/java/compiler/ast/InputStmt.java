
package compiler.ast;

import compiler.lexer.Token;

/**
 * Represents an input statement.
 */
public record InputStmt(Token name) implements Stmt {

    @Override
    public <R> R accept(Stmt.Visitor<R> visitor) {
        return visitor.visitInput(this);
    }
}
