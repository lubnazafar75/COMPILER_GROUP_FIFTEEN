package compiler.ast;

import compiler.lexer.Token;

/**
 * The use of a variable inside an expression: the x in x + 1.
 * Phase 3 (semantic analysis) will check that the variable was declared.
 *
 * Owner: Egmine James (0163)
 */
public record VariableExpr(Token name) implements Expr {

    @Override
    public <R> R accept(Expr.Visitor<R> visitor) {
        return visitor.visitVariable(this);
    }
}
