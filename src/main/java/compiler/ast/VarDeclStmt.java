package compiler.ast;

import compiler.lexer.Token;

/**
 * Declares a variable, with an optional starting value:
 *
 *     int total;            initializer is null
 *     string msg = "hi";    initializer is the expression after '='
 *
 * "type" is the INT or STRING keyword token; "name" is the identifier token.
 *
 */
public record VarDeclStmt(Token type, Token name, Expr initializer) implements Stmt {

    @Override
    public <R> R accept(Stmt.Visitor<R> visitor) {
        return visitor.visitVarDecl(this);
    }
}
