package compiler.ast;

import compiler.lexer.Token;
import compiler.lexer.TokenType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Phase 2 tests: expression nodes and their Visitor.
 *
 * Owner: Egmine James (0163)
 */
class ExprNodeTest {

    private static Token token(TokenType type, String lexeme) {
        return new Token(type, lexeme, 1, 1);
    }

    /** A visitor that just returns the name of the node it was given. */
    private static final Expr.Visitor<String> NAMER = new Expr.Visitor<String>() {

        @Override
        public String visitBinary(BinaryExpr expr) {
            return "binary";
        }

        @Override
        public String visitUnary(UnaryExpr expr) {
            return "unary";
        }

        @Override
        public String visitLiteral(LiteralExpr expr) {
            return "literal";
        }

        @Override
        public String visitVariable(VariableExpr expr) {
            return "variable";
        }

        @Override
        public String visitGrouping(GroupingExpr expr) {
            return "grouping";
        }
    };

    private final LiteralExpr one =
            new LiteralExpr(token(TokenType.INT_LITERAL, "1"));

    private final Token plus = token(TokenType.PLUS, "+");

    @Test
    void literalExposesTypeAndText() {
        LiteralExpr literal =
                new LiteralExpr(token(TokenType.FLOAT_LITERAL, "19.75"));

        assertEquals(TokenType.FLOAT_LITERAL, literal.type());
        assertEquals("19.75", literal.text());
    }

    @Test
    void acceptCallsTheMatchingVisitMethod() {
        assertEquals("binary", new BinaryExpr(one, plus, one).accept(NAMER));

        assertEquals("unary",
                new UnaryExpr(token(TokenType.MINUS, "-"), one).accept(NAMER));

        assertEquals("literal", one.accept(NAMER));

        assertEquals("variable",
                new VariableExpr(token(TokenType.IDENTIFIER, "x")).accept(NAMER));

        assertEquals("grouping", new GroupingExpr(one).accept(NAMER));
    }

    @Test
    void binaryExprKeepsItsParts() {
        BinaryExpr sum = new BinaryExpr(one, plus, one);

        assertEquals(one, sum.left());
        assertEquals(plus, sum.operator());
        assertEquals(one, sum.right());
    }

    @Test
    void nodesWithTheSameContentAreEqual() {
        // The remaining code was cut off in your message.
    }
}
