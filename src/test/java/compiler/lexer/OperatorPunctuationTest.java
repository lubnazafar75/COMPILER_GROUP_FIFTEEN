package compiler.lexer;
import org.junit.jupiter.api.Test;
import java.util.List;
import static compiler.lexer.TokenType.*;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Scanner Tests Part B (2/3): operators and punctuation.
 *
 * Owner: Lubna Zafar (0167)
 */
class OperatorPunctuationTest {
    @Test
    void singleCharacterOperators() {
        assertEquals(List.of(PLUS, MINUS, STAR, SLASH, PERCENT, ASSIGN, NOT, LESS, GREATER, EOF),
                TestUtil.types("+ - * / % = ! < >"));
    }
    @Test
    void twoCharacterOperatorsUseLongestMatch() {
        assertEquals(List.of(EQUAL_EQUAL, NOT_EQUAL, LESS_EQUAL, GREATER_EQUAL, AND, OR, EOF),
                TestUtil.types("== != <= >= && ||"));
    }
    @Test
    void operatorsWithoutSpaces() {
        assertEquals(List.of(IDENTIFIER, LESS_EQUAL, INT_LITERAL, AND, IDENTIFIER, NOT_EQUAL, INT_LITERAL, EOF),
                TestUtil.types("a<=10&&b!=0"));
    }
    @Test
    void punctuation() {
        assertEquals(List.of(LPAREN, RPAREN, LBRACE, RBRACE, LBRACKET, RBRACKET, SEMICOLON, COMMA, EOF),
                TestUtil.types("( ) { } [ ] ; ,"));
    }
    @Test
    void categoriesAreCorrect() {
        List<Token> tokens = TestUtil.tokenize("== ;");
        assertEquals(TokenType.Category.OPERATOR, tokens.get(0).getType().getCategory());
        assertEquals(TokenType.Category.PUNCTUATION, tokens.get(1).getType().getCategory());
    }
}