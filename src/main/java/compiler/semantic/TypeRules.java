
package compiler.semantic;

import compiler.lexer.TokenType;

public final class TypeRules {

    private TypeRules() {
    }

    public static boolean canAssign(Type target, Type value) {
        if (target == Type.ERROR || value == Type.ERROR) {
            return true;
        }

        return target == value;
    }

    public static Type unary(TokenType operator, Type operand) {
        if (operand == Type.ERROR) {
            return Type.ERROR;
        }

        switch (operator) {
            case MINUS:
                return operand == Type.INT || operand == Type.FLOAT
                        ? operand : null;

            case NOT:
                return operand == Type.BOOL ? Type.BOOL : null;

            default:
                return null;
        }
    }

    public static Type binary(TokenType operator, Type left, Type right) {
        if (left == Type.ERROR || right == Type.ERROR) {
            return Type.ERROR;
        }

        switch (operator) {
            case PLUS:
                if (left == Type.STRING && right == Type.STRING) {
                    return Type.STRING;
                }
                return numericResult(left, right);

            case MINUS:
            case STAR:
            case SLASH:
            case PERCENT:
                return numericResult(left, right);

            case LESS:
            case LESS_EQUAL:
            case GREATER:
            case GREATER_EQUAL:
                return isNumeric(left) && isNumeric(right)
                        ? Type.BOOL : null;

            case EQUAL_EQUAL:
            case NOT_EQUAL:
                return left == right ? Type.BOOL : null;

            case AND:
            case OR:
                return left == Type.BOOL && right == Type.BOOL
                        ? Type.BOOL : null;

            default:
                return null;
        }
    }

    private static boolean isNumeric(Type type) {
        return type == Type.INT || type == Type.FLOAT;
    }

    private static Type numericResult(Type left, Type right) {
        if (!isNumeric(left) || !isNumeric(right)) {
            return null;
        }

        return left == Type.FLOAT || right == Type.FLOAT
                ? Type.FLOAT : Type.INT;
    }
}
