package compiler.semantic;

import compiler.ast.BinaryExpr;
import compiler.ast.Expr;
import compiler.ast.GroupingExpr;
import compiler.ast.LiteralExpr;
import compiler.ast.UnaryExpr;
import compiler.ast.VariableExpr;
import compiler.error.SemanticErrorReporter;

/**
 * Works out the type of every expression, and reports the mistakes it
 * finds on the way.
 *
 * It is a Visitor over the expression nodes that returns a Type, so
 * typeOf(expression) walks the whole sub-tree and gives back one type.
 * After a mistake it returns Type.ERROR, which every rule accepts, so
 * one mistake gives one message.
 *
 * Owner: Michael Addo (0159)
 */
public final class TypeChecker implements Expr.Visitor<Type> {

    private final SymbolTable symbols;
    private final SemanticErrorReporter errors;

    public TypeChecker(SymbolTable symbols, SemanticErrorReporter errors) {
        this.symbols = symbols;
        this.errors = errors;
    }

    /** The type of an expression, or Type.ERROR if it contains a mistake. */
    public Type typeOf(Expr expression) {
        return expression.accept(this);
    }

    @Override
    public Type visitBinary(BinaryExpr expr) {
        Type left = typeOf(expr.left());
        Type right = typeOf(expr.right());

        Type result = TypeRules.binary(
            expr.operator().getType(),
            left,
            right
        );

        if (result == null) {
            errors.report(
                expr.operator(),
                "Operator '" + expr.operator().getLexeme()
                    + "' cannot be used with " + left + " and " + right
            );

            return Type.ERROR;
        }

        return result;
    }

    @Override
    public Type visitUnary(UnaryExpr expr) {
        Type operand = typeOf(expr.operand());

        Type result = TypeRules.unary(
            expr.operator().getType(),
            operand
        );

        if (result == null) {
            errors.report(
                expr.operator(),
                "Operator '" + expr.operator().getLexeme()
                    + "' cannot be used with " + operand
            );

            return Type.ERROR;
        }

        return result;
    }

    @Override
    public Type visitLiteral(LiteralExpr expr) {
        switch (expr.type()) {
            case INT_LITERAL:
                return Type.INT;

            case FLOAT_LITERAL:
                return Type.FLOAT;

            case STRING_LITERAL:
                return Type.STRING;

            default:
                return Type.ERROR;
        }
    }

    @Override
    public Type visitVariable(VariableExpr expr) {
        Symbol symbol = symbols.lookup(expr.name().getLexeme());

        if (symbol == null) {
            errors.report(
                expr.name(),
                "Variable '" + expr.name().getLexeme()
                    + "' is used but never declared"
            );

            return Type.ERROR;
        }

        return symbol.type();
    }

    @Override
    public Type visitGrouping(GroupingExpr expr) {
        return typeOf(expr.expression());
    }
}