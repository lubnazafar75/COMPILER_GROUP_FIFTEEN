
package compiler.parser;

import compiler.ast.BinaryExpr;
import compiler.ast.Expr;
import compiler.ast.GroupingExpr;
import compiler.ast.LiteralExpr;
import compiler.ast.UnaryExpr;
import compiler.ast.VariableExpr;
import compiler.lexer.Token;
import compiler.lexer.TokenType;

/**
 * Parses expressions using recursive descent.
 * Operator precedence follows the Phase 2 grammar.
 */
public final class ExpressionParser {

    private final TokenStream tokens;

    public ExpressionParser(TokenStream tokens) {
        this.tokens = tokens;
    }

    public Expr parseExpression() {
        return logicOr();
    }

    // OR has the lowest precedence.
    private Expr logicOr() {
        Expr expr = logicAnd();

        while (tokens.match(TokenType.OR)) {
            Token operator = tokens.previous();
            Expr right = logicAnd();
            expr = new BinaryExpr(expr, operator, right);
        }

        return expr;
    }

    private Expr logicAnd() {
        Expr expr = equality();

        while (tokens.match(TokenType.AND)) {
            Token operator = tokens.previous();
            Expr right = equality();
            expr = new BinaryExpr(expr, operator, right);
        }

        return expr;
    }

    private Expr equality() {
        Expr expr = comparison();

        while (tokens.match(
                TokenType.EQUAL_EQUAL,
                TokenType.NOT_EQUAL)) {

            Token operator = tokens.previous();
            Expr right = comparison();
            expr = new BinaryExpr(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = term();

        while (tokens.match(
                TokenType.LESS,
                TokenType.LESS_EQUAL,
                TokenType.GREATER,
                TokenType.GREATER_EQUAL)) {

            Token operator = tokens.previous();
            Expr right = term();
            expr = new BinaryExpr(expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        while (tokens.match(
                TokenType.PLUS,
                TokenType.MINUS)) {

            Token operator = tokens.previous();
            Expr right = factor();
            expr = new BinaryExpr(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {
        Expr expr = unary();

        while (tokens.match(
                TokenType.STAR,
                TokenType.SLASH,
                TokenType.PERCENT)) {

            Token operator = tokens.previous();
            Expr right = unary();
            expr = new BinaryExpr(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (tokens.match(TokenType.NOT, TokenType.MINUS)) {
            Token operator = tokens.previous();
            Expr operand = unary();

            return new UnaryExpr(operator, operand);
        }

        return primary();
    }

    private Expr primary() {

        if (tokens.match(
                TokenType.INT_LITERAL,
                TokenType.FLOAT_LITERAL,
                TokenType.STRING_LITERAL)) {

            return new LiteralExpr(tokens.previous());
        }

        if (tokens.match(TokenType.IDENTIFIER)) {
            return new VariableExpr(tokens.previous());
        }

        if (tokens.match(TokenType.LPAREN)) {
            Expr expression = parseExpression();

            tokens.consume(
                    TokenType.RPAREN,
                    "')' after expression"
            );

            return new GroupingExpr(expression);
        }

        throw tokens.expectedError("an expression");
    }
}
