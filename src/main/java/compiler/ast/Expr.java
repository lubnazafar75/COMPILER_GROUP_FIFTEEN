
package compiler.ast;

/**
 * Base type of every EXPRESSION node in the syntax tree.
 *
 * Expressions produce values, such as 3 + 4, x, "hi" or -y.
 *
 * Later phases walk the tree using a Visitor.
 * Each node's accept() method calls its matching visit method.
 *
 * Owner: Egmine James (0163)
 */
public interface Expr {

    <R> R accept(Visitor<R> visitor);

    /**
     * One visit method for every kind of expression.
     */
    interface Visitor<R> {

        R visitBinary(BinaryExpr expr);

        R visitUnary(UnaryExpr expr);

        R visitLiteral(LiteralExpr expr);

        R visitVariable(VariableExpr expr);

        R visitGrouping(GroupingExpr expr);
    }
}
