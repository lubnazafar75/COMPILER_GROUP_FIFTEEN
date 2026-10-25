package compiler.lexer;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SourceReaderTest {

    @Test
    void advanceTracksLinesAndColumns() {
        SourceReader r = new SourceReader("ab\nc");

        assertEquals(1, r.getLine());
        assertEquals(1, r.getColumn());

        r.advance();
        r.advance();

        assertEquals(3, r.getColumn());

        r.advance();

        assertEquals(2, r.getLine());
        assertEquals(1, r.getColumn());
        assertEquals('c', r.peek());
    }

    @Test
    void peekAtEndReturnsEndMarker() {
        SourceReader r = new SourceReader("");

        assertTrue(r.isAtEnd());
        assertEquals(SourceReader.END, r.peek());
        assertEquals(SourceReader.END, r.peekNext());
    }

    @Test
    void tokenPositionsAreCorrectAcrossLines() {
        List<Token> tokens = TestUtil.tokenize("int x;\n  print x;");

        Token print = tokens.get(3);

        assertEquals(TokenType.PRINT, print.getType());
        assertEquals(2, print.getLine());
        assertEquals(3, print.getColumn());
    }

    @Test
    void emptyInputGivesOnlyEof() {
        List<Token> tokens = TestUtil.tokenize("   \n\t ");

        assertEquals(1, tokens.size());
        assertEquals(TokenType.EOF, tokens.get(0).getType());
    }
}
