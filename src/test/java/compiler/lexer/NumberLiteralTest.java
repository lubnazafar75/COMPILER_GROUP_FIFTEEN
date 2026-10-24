package compiler.lexer;

import compiler.error.ErrorReporter;
import compiler.error.LexicalError;
import org.junit.jupiter.api.Test;
import java.util.List;
import static compiler.lexer.TokenType.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Scanner Tests Part A (2/2): integer and decimal literals.
 *
 * Owner: Egmine James (0163)
 */
class NumberLiteralTest {
    @Test
    void integerAndDecimal() {
        List<Token> tokens = TestUtil.tokenize("842 19.75");
        assertEquals(3, tokens.size());
        assertEquals(INT_LITERAL, tokens.get(0).getType());
        assertEquals("842", tokens.get(0).getLexeme());
        assertEquals(FLOAT_LITERAL, tokens.get(1).getType());
        assertEquals("19.75", tokens.get(1).getLexeme());
    }

    @Test
    void zeroAndLeadingZeroDecimal() {
        assertEquals(List.of(INT_LITERAL, FLOAT_LITERAL, EOF), TestUtil.types("0 0.5"));
    }

    @Test
    void numberDirectlyFollowedByOperator() {
        assertEquals(List.of(INT_LITERAL, PLUS, INT_LITERAL, SEMICOLON, EOF),
            TestUtil.types("3+4;"));
    }

    @Test
    void trailingDotIsMalformed() {
        ErrorReporter errors = TestUtil.errorsFor("12.");
        assertEquals(1, errors.count());
        assertEquals(LexicalError.Kind.MALFORMED_NUMBER, errors.getErrors().get(0).getKind());
        assertEquals(List.of(ERROR, EOF), TestUtil.types("12."));
    }
}
