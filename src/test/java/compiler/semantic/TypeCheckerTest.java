package compiler.semantic;

import compiler.lexer.TokenType;
import org.junit.jupiter.api.Test;

import static compiler.semantic.SemanticTestUtil.messages;
import static compiler.semantic.SemanticTestUtil.typeOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 3 tests: the type of every kind of expression, and the rules in
 * TypeRules that decide it.
 *
 * Owner: Michael Addo (0159)
 */
class TypeCheckerTest {

    private static final String DECLS = "int i = 1; string s = \"a\";";

    // ---- literals and variables ----------------------------------------

    @Test
    void literalsHaveTheTypeOfTheirToken() {
        assertEquals(Type.INT, typeOf("42"));
        assertEquals(Type.FLOAT, typeOf("3.5"));
        assertEquals(Type.STRING, typeOf("\"hello\""));
    }

    @Test
    void aVariableHasTheTypeItWasDeclaredWith() {
        assertEquals(Type.INT, typeOf(DECLS, "i"));
        assertEquals(Type.STRING, typeOf(DECLS, "s"));
    }

    @Test
    void anUndeclaredVariableIsAnErrorType() {
        assertEquals(Type.ERROR, typeOf("nowhere"));
    }

    @Test
    void bracketsDoNotChangeTheType() {
        assertEquals(Type.INT, typeOf("(1 + 2)"));
        assertEquals(Type.BOOL, typeOf("(1 < 2)"));
    }

    // ---- arithmetic ------------------------------------------------------

    @Test
    void intWithIntGivesInt() {
        assertEquals(Type.INT, typeOf("1 + 2"));
        assertEquals(Type.INT, typeOf("7 % 2"));
    }

    @Test
    void anyFloatMakesTheWholeResultFloat() {
        assertEquals(Type.FLOAT, typeOf("1 + 2.5"));
        assertEquals(Type.FLOAT, typeOf("2.5 * 4"));
        assertEquals(Type.FLOAT, typeOf("1 + 2 * 3.0"));
    }

    @Test
    void plusJoinsTwoStrings() {
        assertEquals(Type.STRING, typeOf("\"a\" + \"b\""));
    }

    @Test
    void plusCannotMixAStringAndANumber() {
        assertEquals(Type.ERROR, typeOf("1 + \"a\""));

        assertEquals(
            java.util.List.of("Operator '+' cannot be used with int and string"),
            messages("print 1 + \"a\";")
        );
    }

    @Test
    void theOtherArithmeticOperatorsRejectStrings() {
        assertEquals(Type.ERROR, typeOf("\"a\" - \"b\""));
        assertEquals(Type.ERROR, typeOf("\"a\" * 2"));
    }

    // ---- comparison and logic -------------------------------------------

    @Test
    void comparingTwoNumbersGivesBool() {
        assertEquals(Type.BOOL, typeOf("1 < 2"));
        assertEquals(Type.BOOL, typeOf("1.5 >= 2"));
    }

    @Test
    void comparingStringsWithLessThanIsNotAllowed() {
        assertEquals(Type.ERROR, typeOf("\"a\" < \"b\""));
    }

    @Test
    void equalityWorksForMatchingTypes() {
        assertEquals(Type.BOOL, typeOf("1 == 2"));
        assertEquals(Type.BOOL, typeOf("\"a\" != \"b\""));
        assertEquals(Type.BOOL, typeOf("(1 < 2) == (3 < 4)"));
    }

    @Test
    void equalityRejectsTypesThatDoNotMatch() {
        assertEquals(Type.ERROR, typeOf("1 == \"a\""));
    }

    @Test
    void andOrNeedBools() {
        assertEquals(Type.BOOL, typeOf("1 < 2 && 3 < 4"));
        assertEquals(Type.BOOL, typeOf("1 < 2 || 3 < 4"));
        assertEquals(Type.ERROR, typeOf("1 && 2"));
    }

    // ---- unary -----------------------------------------------------------

    @Test
    void minusKeepsTheNumericType() {
        assertEquals(Type.INT, typeOf("-5"));
        assertEquals(Type.FLOAT, typeOf("-5.5"));
        assertEquals(Type.ERROR, typeOf("-\"a\""));
    }

    @Test
    void notNeedsABool() {
        assertEquals(Type.BOOL, typeOf("!(1 < 2)"));
        assertEquals(Type.ERROR, typeOf("!5"));
    }

    // ---- the rules table itself ------------------------------------------

    @Test
    void typeRulesReturnNullForCombinationsThatAreNotAllowed() {
        assertNull(TypeRules.binary(TokenType.STAR, Type.STRING, Type.INT));
        assertNull(TypeRules.binary(TokenType.AND, Type.INT, Type.INT));
        assertNull(TypeRules.unary(TokenType.NOT, Type.INT));
    }

    @Test
    void everyRuleAcceptsErrorSilently() {
        assertEquals(
            Type.ERROR,
            TypeRules.binary(TokenType.PLUS, Type.ERROR, Type.INT)
        );

        assertEquals(
            Type.ERROR,
            TypeRules.binary(TokenType.AND, Type.INT, Type.ERROR)
        );

        assertEquals(
            Type.ERROR,
            TypeRules.unary(TokenType.MINUS, Type.ERROR)
        );

        assertTrue(TypeRules.canAssign(Type.INT, Type.ERROR));
    }

    @Test
    void assignmentNeedsAnExactTypeMatch() {
        assertTrue(TypeRules.canAssign(Type.INT, Type.INT));
        assertTrue(TypeRules.canAssign(Type.STRING, Type.STRING));

        assertFalse(
            TypeRules.canAssign(Type.INT, Type.FLOAT),
            "a float would lose its fraction, so it is refused"
        );

        assertFalse(TypeRules.canAssign(Type.INT, Type.STRING));
    }

    @Test
    void oneMistakeGivesOneMessageNotACascade() {
        // The bad + is reported once; the comparison above it stays quiet.
        assertEquals(1, messages("print (1 + \"a\") < 3;").size());
    }
}