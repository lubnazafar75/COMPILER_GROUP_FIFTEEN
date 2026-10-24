package compiler.lexer;
import compiler.error.ErrorReporter;
import compiler.error.LexicalError;
import org.junit.jupiter.api.Test;
import java.util.List;
import static compiler.lexer.TokenType.*;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Scanner Tests Part B (3/3): error detection and recovery.
 * The scanner must report problems and KEEP GOING.
 *
 * Owner: Lubna Zafar (0167)
 */

class LexicalErrorTest {
    @Test
    void illegalCharacterIsSkippedAndScanningContinues() {
        String src = "int x @ = 42;";
        assertEquals(List.of(INT, IDENTIFIER, ASSIGN, INT_LITERAL, SEMICOLON, EOF), TestUtil.types(src));
        ErrorReporter errors = TestUtil.errorsFor(src);
        assertEquals(1, errors.count());
        LexicalError e = errors.getErrors().get(0);
        assertEquals(LexicalError.Kind.ILLEGAL_CHARACTER, e.getKind());
        assertEquals(1, e.getLine());
        assertEquals(7, e.getColumn());
    }
    @Test
    void singleAmpersandIsIllegal() {
        ErrorReporter errors = TestUtil.errorsFor("a & b");
        assertEquals(1, errors.count());
        assertEquals(LexicalError.Kind.ILLEGAL_CHARACTER, errors.getErrors().get(0).getKind());
    }
    @Test
    void unterminatedStringRecoversOnNextLine() {
        String src = "print \"oops;\nint y;";
        ErrorReporter errors = TestUtil.errorsFor(src);
        assertEquals(1, errors.count());
        assertEquals(LexicalError.Kind.UNTERMINATED_STRING, errors.getErrors().get(0).getKind());
        assertEquals(List.of(PRINT, ERROR, INT, IDENTIFIER, SEMICOLON, EOF), TestUtil.types(src));
    }
    @Test
    void unterminatedBlockCommentIsReported() {
        ErrorReporter errors = TestUtil.errorsFor("x = 1; /* never closed");
        assertEquals(1, errors.count());
        assertEquals(LexicalError.Kind.UNTERMINATED_COMMENT, errors.getErrors().get(0).getKind());
    }
    @Test
    void invalidEscapeIsReported() {
        ErrorReporter errors = TestUtil.errorsFor("\"bad \\q escape\"");
        assertEquals(1, errors.count());
        assertEquals(LexicalError.Kind.INVALID_ESCAPE, errors.getErrors().get(0).getKind());
    }
    @Test
    void multipleErrorsAreAllCollected() {
        ErrorReporter errors = TestUtil.errorsFor("@ # $");
        assertEquals(3, errors.count());
    }
    @Test
    void validProgramHasNoErrors() {
        assertFalse(TestUtil.errorsFor("int x = 5; print x;").hasErrors());
    }
