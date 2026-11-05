package compiler.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static compiler.parser.ParserTestUtil.exprTree;
import static compiler.parser.ParserTestUtil.messages;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Phase 2 tests: expressions - literals, precedence, associativity,
 * grouping, unary operators and expression errors.
 *
 * Owner: Kessina Alex (0160)
 */
class ExpressionParseTest {

    @Test
    void literalsAndVariables() {
        assertEquals("Literal 42\n", exprTree("42"));
        assertEquals("Literal 19.75\n", exprTree("19.75"));
        assertEquals("Literal \"hi\"\n", exprTree("\"hi\""));
        assertEquals("Variable total\n", exprTree("total"));
    }

    @Test
    void multiplicationBindsTighterThanAddition() {
        assertEquals("""
                Binary +
                  Literal 1
                  Binary *
                    Literal 2
                    Literal 3
                """, exprTree("1 + 2 * 3"));
    }

    @Test
    void subtractionIsLeftAssociative() {
        // 10 - 4 - 3 must mean (10 - 4) - 3, not 10 - (4 - 3)
        assertEquals("""
                Binary -
                  Binary -
                    Literal 10
                    Literal 4
                  Literal 3
                """, exprTree("10 - 4 - 3"));
    }

    @Test
    void multiplyDivideAndModuloShareALevelAndGroupLeftToRight() {
        assertEquals("""
                Binary %
                  Binary *
                    Variable a
                    Variable b
                  Variable c
                """, exprTree("a * b % c"));
    }

    @Test
    void bracketsOverridePrecedence() {
        assertEquals("""
                Binary *
                  Group
                    Binary +
                      Literal 1
                      Literal 2
                  Literal 3
                """, exprTree("(1 + 2) * 3"));
    }

    @Test
    void unaryMinusAndNot() {
        assertEquals("""
                Unary -
                  Variable x
                """, exprTree("-x"));
        assertEquals("""
                Unary !
                  Unary !
                    Variable done
                """, exprTree("!!done"));
    }

    @Test
    void unaryBindsTighterThanMultiplication() {
        assertEquals("""
                Binary *
                  Unary -
                    Variable a
                  Variable b
                """, exprTree("-a * b"));
    }

    @Test
    void minusCanBeBothBinaryAndUnary() {
        assertEquals("""
                Binary -
                  Variable a
                  Unary -
                    Literal 1
                """, exprTree("a - -1"));
    }

    @Test
    void comparisonBindsTighterThanEquality() {
        assertEquals("""
                Binary ==
                  Binary <
                    Variable a
                    Variable b
                  Binary >
                    Variable c
                    Variable d
                """, exprTree("a < b == c > d"));
    }

    @Test
    void andBindsTighterThanOr() {
        assertEquals("""
                Binary ||
                  Variable a
                  Binary &&
                    Variable b
                    Variable c
                """, exprTree("a || b && c"));
    }

    @Test
    void allSixComparisonOperatorsAreAccepted() {
        for (String operator : List.of("<", "<=", ">", ">=", "==", "!=")) {
            assertEquals("Binary " + operator + "\n  Variable a\n  Variable b\n",
                    exprTree("a " + operator + " b"));
        }
    }

    @Test
    void missingOperandIsReported() {
        assertEquals(List.of("Expected an expression, but found ';'"), messages("print 1 + ;"));
    }

    @Test
    void operatorWithNoLeftOperandIsReported() {
        assertEquals(List.of("Expected an expression, but found '*'"), messages("print * 3;"));
    }

    @Test
    void unclosedBracketIsReported() {
        assertEquals(List.of("Expected ')' after expression, but found ';'"), messages("print (1 + 2;"));
    }
}
