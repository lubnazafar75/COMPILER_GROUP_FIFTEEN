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
 * Recursive-descent parser for EXPRESSIONS.
 *
 * There is one method per precedence level, from the loosest-binding
 * operator (||) down to the tightest (literals and brackets). A method
 * handles its own operators and calls the next-tighter method for the
 * operands. This is how  1 + 2 * 3  becomes  1 + (2 * 3).
 *
 *   expression -> logicOr
 *   logicOr    -> logicAnd   ( "||" logicAnd )*
 *   logicAnd   -> equality   ( "&&" equality )*
 *   equality   -> comparison ( ( "==" | "!=" ) comparison )*
 *   comparison -> term       ( ( "<" | "<=" | ">" | ">=" ) term )*
 *   term       -> factor     ( ( "+" | "-" ) factor )*
 *   factor     -> unary      ( ( "*" | "/" | "%" ) unary )*
 *   unary      -> ( "!" | "-" ) unary | primary
 *   primary    -> INT_LITERAL | FLOAT_LITERAL | STRING_LITERAL | IDENTIFIER
 *               | "(" expression ")"
 *
 * Owner: Kessina Alex (0160)
 */
public final class ExpressionParser {

    private final TokenStream tokens;

    public ExpressionParser(TokenStream tokens) {
        this.tokens = tokens;
    }

    /** Parses one complete expression, starting at the next token. */
    public Expr parseExpression() {
        return logicOr();
    }

    private Expr logicOr() {
        Expr left = logicAnd();
        while (tokens.match(TokenType.OR)) {
            Token operator = tokens.previous();
            Expr right = logicAnd();
            left = new BinaryExpr(left, operator, right);
        }
        return left;
    }

    private Expr logicAnd() {
        Expr left = equality();
        while (tokens.match(TokenType.AND)) {
            Token operator = tokens.previous();
            Expr right = equality();
            left = new BinaryExpr(left, operator, right);
        }
        return left;
    }

    private Expr equality() {
        Expr left = comparison();
        while (tokens.match(TokenType.EQUAL_EQUAL, TokenType.NOT_EQUAL)) {
            Token operator = tokens.previous();
            Expr right = comparison();
            left = new BinaryExpr(left, operator, right);
        }
        return left;
    }

    private Expr comparison() {
        Expr left = term();
        while (tokens.match(TokenType.LESS, TokenType.LESS_EQUAL,
                TokenType.GREATER, TokenType.GREATER_EQUAL)) {
            Token operator = tokens.previous();
            Expr right = term();
            left = new BinaryExpr(left, operator, right);
        }
        return left;
    }

    private Expr term() {
        Expr left = factor();
        while (tokens.match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = tokens.previous();
            Expr right = factor();
            left = new BinaryExpr(left, operator, right);
        }
        return left;
    }

    private Expr factor() {
        Expr left = unary();
        while (tokens.match(TokenType.STAR, TokenType.SLASH, TokenType.PERCENT)) {
            Token operator = tokens.previous();
            Expr right = unary();
            left = new BinaryExpr(left, operator, right);
        }
        return left;
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
        if (tokens.match(TokenType.INT_LITERAL, TokenType.FLOAT_LITERAL, TokenType.STRING_LITERAL)) {
            return new LiteralExpr(tokens.previous());
        }
        if (tokens.match(TokenType.IDENTIFIER)) {
            return new VariableExpr(tokens.previous());
        }
        if (tokens.match(TokenType.LPAREN)) {
            Expr inner = parseExpression();
            tokens.consume(TokenType.RPAREN, "')' after expression");
            return new GroupingExpr(inner);
        }
        throw tokens.expectedError("an expression");
    }
}
