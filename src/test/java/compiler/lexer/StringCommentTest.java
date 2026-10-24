package compiler.lexer;
import org.junit.jupiter.api.Test;
import java.util.List;
import static compiler.lexer.TokenType.*;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Scanner Tests Part B (1/3): string literals and comments.
 *
 * Owner: Lubna Zafar (0167)
 */
class StringCommentTest {
    @Test
    void simpleStringKeepsQuotes() {
        List<Token> tokens = TestUtil.tokenize("\"hello mini\"");
        assertEquals(STRING_LITERAL, tokens.get(0).getType());
        assertEquals("\"hello mini\"", tokens.get(0).getLexeme());
    }
    @Test
    void stringWithEscapes() {
        List<Token> tokens = TestUtil.tokenize("\"say \\\"hi\\\"\\n\"");
        assertEquals(STRING_LITERAL, tokens.get(0).getType());
        assertEquals(0, TestUtil.errorsFor("\"say \\\"hi\\\"\\n\"").count());
    }
    @Test
    void lineCommentIsSkipped() {
        assertEquals(List.of(INT, IDENTIFIER, SEMICOLON, EOF),
                TestUtil.types("int x; // this is ignored"));
    }
    @Test
    void blockCommentIsSkipped() {
        assertEquals(List.of(INT_LITERAL, INT_LITERAL, EOF),
                TestUtil.types("1 /* ignored */ 2"));
    }
    @Test
    void lineNumbersCorrectAfterMultiLineBlockComment() {
        List<Token> tokens = TestUtil.tokenize("/* line1\nline2\nline3 */ x");
        assertEquals(IDENTIFIER, tokens.get(0).getType());
        assertEquals(3, tokens.get(0).getLine());
        assertEquals(10, tokens.get(0).getColumn());
    }
    @Test
    void stringsCommentsAndOperatorsTogether() {
        List<Token> tokens = TestUtil.tokenize("\"hello mini\" // plain comment \n == /* block */ !=");
        assertEquals(4, tokens.size());
        assertEquals(STRING_LITERAL, tokens.get(0).getType());
        assertEquals("==", tokens.get(1).getLexeme());
        assertEquals("!=", tokens.get(2).getLexeme());
        assertEquals(2, tokens.get(1).getLine());
    }
    @Test
    void commentMarkersInsideStringAreNotComments() {
        List<Token> tokens = TestUtil.tokenize("\"a // b\"");
        assertEquals(2, tokens.size());
        assertEquals("\"a // b\"", tokens.get(0).getLexeme());
    }
}
