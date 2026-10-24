package compiler.lexer;
import org.junit.jupiter.api.Test;
import java.util.List;
import static compiler.lexer.TokenType.*;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Scanner Tests Part A (1/2): keywords and identifiers.
 *
 * Owner: Egmine James (0163)
 */
class KeywordIdentifierTest {
    @Test
    void recognisesEveryKeyword() {
        assertEquals(
                List.of(IF, ELSE, WHILE, RETURN, INT, STRING, PRINT, INPUT, EOF),
                TestUtil.types("if else while return int string print input"));
    }
    @Test
    void identifierWithUnderscoreAndDigits() {
        List<Token> tokens = TestUtil.tokenize("variable_name _tmp count2");
        assertEquals(IDENTIFIER, tokens.get(0).getType());
        assertEquals("variable_name", tokens.get(0).getLexeme());
        assertEquals("_tmp", tokens.get(1).getLexeme());
        assertEquals("count2", tokens.get(2).getLexeme());
    }
    @Test
    void keywordsAreCaseSensitive() {
        assertEquals(List.of(IDENTIFIER, IDENTIFIER, EOF), TestUtil.types("If WHILE"));
    }
    @Test
    void keywordPrefixIsStillAnIdentifier() {
        // "iffy" starts with "if" but is a whole different word
        assertEquals(List.of(IDENTIFIER, IDENTIFIER, EOF), TestUtil.types("iffy printer"));
    }
    @Test
    void mixedKeywordAndIdentifierSequence() {
        assertEquals(List.of(IF, IDENTIFIER, WHILE, EOF), TestUtil.types("if variable_name while"));
    }
}