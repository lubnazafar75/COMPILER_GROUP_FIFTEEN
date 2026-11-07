package compiler.ast;

import compiler.lexer.Token;

import java.util.List;

/**
 * A group of statements between braces:  { ... }
 *
 * The constructor stores an unmodifiable copy of the list, so a block can
 * never change after the parser has built it.
 *
 */
public record BlockStmt(Token open, List<Stmt> statements) implements Stmt {

    public BlockStmt {
        statements = List.copyOf(statements);
    }

    @Override
    public <R> R accept(Stmt.Visitor<R> visitor) {
        return visitor.visitBlock(this);
    }
}
