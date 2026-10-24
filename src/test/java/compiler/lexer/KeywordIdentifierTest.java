package compiler.lexer;
import org.junit.jupiter.api.Test;
import java.util.List;
import static compiler.lexer.TokenType.*;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Scanner Tests Part A (1/2): keywords and identifiers.
 */
class KeywordIdentifierTest {
    @Test
    void recognisesEveryKeyword() {
        assertEquals(
                List.of(IF, ELSE, WHILE, RETURN, INT, STRING, PRINT, INPUT, EOF),
                TestUtil.types("if else while return int string print input"));
    }
}