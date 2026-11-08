package compiler.ast;


import compiler.lexer.Token;

/**
 * Represents a variable declaration.
 * Examples: int age = 20; or string name;
 */
public record VarDeclStmt(
        Token type,
        Token name,
        Expr initializer
) implements Stmt {

    @Override
    public <R> R accept(Stmt.Visitor<R> visitor) {
        return visitor.visitVarDecl(this);
    }
}
