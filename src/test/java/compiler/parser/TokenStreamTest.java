package compiler.parser;

import compiler.error.SyntaxError;
import compiler.error.SyntaxErrorReporter;
import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.lexer.TokenType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 2 tests: the TokenStream cursor and error recovery.
 *
 * Owner: Michael Addo (0159)
 */


class TokenStreamTest {

    private static TokenStream streamFor(
            String source, SyntaxErrorReporter errors) {
        return new TokenStream(new Lexer(source).tokenize(), errors);
    }

    private static TokenStream streamFor(String source) {
        return streamFor(source, new SyntaxErrorReporter());
    }

    @Test
    void peekDoesNotConsumeButAdvanceDoes() {
        TokenStream stream = streamFor("int x;");

        assertEquals(TokenType.INT, stream.peek().getType());
        assertEquals(TokenType.INT, stream.peek().getType());
        assertEquals(TokenType.INT, stream.advance().getType());
        assertEquals(TokenType.IDENTIFIER, stream.peek().getType());
        assertEquals(TokenType.INT, stream.previous().getType());
    }

    @Test
    void matchConsumesOnlyWhenTheTypeIsOneOfTheGivenOnes() {
        TokenStream stream = streamFor("int x;");

        assertFalse(stream.match(TokenType.IF, TokenType.WHILE));
        assertEquals(0, stream.getPosition());

        assertTrue(stream.match(TokenType.STRING, TokenType.INT));
        assertEquals(1, stream.getPosition());
    }

    @Test
    void advanceNeverMovesPastEof() {
        TokenStream stream = streamFor("x");

        stream.advance(); // x

        assertTrue(stream.isAtEnd());

        stream.advance();
        stream.advance();

        assertTrue(stream.isAtEnd());
        assertEquals(TokenType.EOF, stream.peek().getType());
    }

    @Test
    void anEofIsAddedWhenTheListHasNone() {
        TokenStream stream = new TokenStream(
                List.of(), new SyntaxErrorReporter());

        assertTrue(stream.isAtEnd());
    }

    @Test
    void consumeReturnsTheTokenWhenItMatches() {
        TokenStream stream = streamFor("x;");

        Token name = stream.consume(
                TokenType.IDENTIFIER, "a name");

        assertEquals("x", name.getLexeme());
    }

    @Test
    void consumeReportsAndThrowsWhenItDoesNotMatch() {
        SyntaxErrorReporter errors = new SyntaxErrorReporter();
        TokenStream stream = streamFor("5", errors);

        assertThrows(ParseException.class,
                () -> stream.consume(
                        TokenType.IDENTIFIER, "a variable name"));

        assertEquals(1, errors.count());

        SyntaxError error = errors.getErrors().get(0);

        assertEquals(
                "Expected a variable name, but found '5'",
                error.getMessage());
        assertEquals(1, error.getLine());
        assertEquals(1, error.getColumn());
    }

    @Test
    void errorOnALaterLineIsReportedAtTheEndOfThePreviousToken() {
        SyntaxErrorReporter errors = new SyntaxErrorReporter();
        TokenStream stream = streamFor("int x\nprint", errors);

        stream.advance(); // int
        stream.advance(); // x

        assertThrows(ParseException.class,
                () -> stream.consume(
                        TokenType.SEMICOLON, "';'"));

        SyntaxError error = errors.getErrors().get(0);

        assertEquals(1, error.getLine());
        assertEquals(6, error.getColumn());
        assertEquals(
                "Expected ';', but found 'print'",
                error.getMessage());
    }

    @Test
    void endOfFileIsDescribedInWords() {
        SyntaxErrorReporter errors = new SyntaxErrorReporter();
        TokenStream stream = streamFor("", errors);

        assertThrows(ParseException.class,
                () -> stream.consume(
                        TokenType.SEMICOLON, "';'"));

        assertEquals(
                "Expected ';', but found end of file",
                errors.getErrors().get(0).getMessage());
    }

    @Test
    void synchronizeStopsJustAfterASemicolon() {
        TokenStream stream = streamFor("5 + 3; x = 1;");

        stream.synchronize();

        assertEquals(TokenType.IDENTIFIER, stream.peek().getType());
        assertEquals("x", stream.peek().getLexeme());
    }

    @Test
    void synchronizeStopsBeforeAStatementKeyword() {
        TokenStream stream = streamFor("5 + 3 print x;");

        stream.synchronize();

        assertEquals(TokenType.PRINT, stream.peek().getType());
    }

    @Test
    void synchronizeDoesNotConsumeTheBraceThatClosesTheBlock() {
        TokenStream stream = streamFor("5 + 3 } print x;");

        stream.synchronize();

        assertEquals(TokenType.RBRACE, stream.peek().getType());
    }

    @Test
    void synchronizeSkipsAWholeBlockInOneGo() {
        TokenStream stream = streamFor(
                "x > 3 { print 1; } y = 2;");

        stream.synchronize();

        assertEquals("y", stream.peek().getLexeme());
    }
}