
package compiler.parser;

import compiler.error.SyntaxError;
import compiler.error.SyntaxErrorReporter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static compiler.parser.ParserTestUtil.errorsFor;
import static compiler.parser.ParserTestUtil.messages;
import static compiler.parser.ParserTestUtil.tree;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 2 tests: syntax errors, their messages and positions, and recovery.
 * The parser must report a mistake and KEEP GOING.
 *
 * Owner: Lubna Zafar (0167)
 */
class ParserErrorTest {

    @Test
    void validProgramHasNoErrors() {
        assertFalse(errorsFor("int x = 5; print x;").hasErrors());
    }

    @Test
    void missingSemicolonIsReportedAtTheEndOfThePreviousLine() {
        SyntaxErrorReporter errors = errorsFor("int x = 5\nprint x;");

        assertEquals(1, errors.count());

        SyntaxError error = errors.getErrors().get(0);

        assertEquals(
                "Expected ';' after variable declaration, but found 'print'",
                error.getMessage()
        );
        assertEquals(1, error.getLine());
        assertEquals(10, error.getColumn());
    }

    @Test
    void parsingContinuesAfterAMissingSemicolon() {
        assertEquals("""
                Program
                  Print
                    Variable x
                """, tree("int x = 5\nprint x;"));
    }

    @Test
    void missingOpeningBracketAfterIfSkipsTheWholeBlock() {
        assertEquals(
                List.of("Expected '(' after 'if', but found 'x'"),
                messages("if x > 3 { print 1; }")
        );

        assertEquals("Program\n", tree("if x > 3 { print 1; }"));
    }

    @Test
    void missingClosingBracketAfterWhileCondition() {
        assertEquals(
                List.of("Expected ')' after the while condition, but found '{'"),
                messages("while (x > 0 { x = x - 1; }")
        );
    }

    @Test
    void missingExpressionInAssignment() {
        SyntaxErrorReporter errors = errorsFor("y = ;");

        assertEquals(1, errors.count());
        assertEquals(
                "Expected an expression, but found ';'",
                errors.getErrors().get(0).getMessage()
        );
        assertEquals(5, errors.getErrors().get(0).getColumn());
    }

    @Test
    void missingVariableNameInDeclaration() {
        assertEquals(
                List.of("Expected a variable name after 'int', but found '='"),
                messages("int = 5;")
        );
    }

    @Test
    void assignmentWithoutEqualsSign() {
        assertEquals(
                List.of("Expected '=' after 'x', but found '+'"),
                messages("x + 1;")
        );
    }

    @Test
    void unclosedBlockIsReportedAtEndOfFile() {
        assertEquals(
                List.of("Expected '}' to close the block opened on line 1, but found end of file"),
                messages("{ print 1;")
        );
    }

    @Test
    void strayClosingBraceIsReportedOnce() {
        assertEquals(
                List.of("Expected a statement, but found '}'"),
                messages("print 1; }")
        );

        assertEquals(
                "Program\n" +
                        "  Print\n" +
                        "    Literal 1\n",
                tree("print 1; }")
        );
    }

    @Test
    void elseWithoutIfIsReported() {
        assertEquals(
                List.of("Expected a statement, but found 'else'"),
                messages("else print 1;")
        );
    }

    @Test
    void incompleteIfAtEndOfFile() {
        assertEquals(
                List.of("Expected a statement, but found end of file"),
                messages("if (x)")
        );
    }

    @Test
    void severalMistakesAreAllReportedInOrder() {
        String source = """
                int x = 5
                print x;
                if x > 3 {
                print "big";
                }
                y = ;
                while (x > 0 {
                x = x - 1;
                }
                print (x + 1;
                print "done";
                """;
        SyntaxErrorReporter errors = errorsFor(source);

        assertEquals(5, errors.count());

        assertEquals(
                List.of(1, 3, 6, 7, 10),
                errors.getErrors().stream()
                        .map(SyntaxError::getLine)
                        .toList()
        );
    }

    @Test
    void goodStatementsBetweenBadOnesAreKept() {
        assertEquals("""
                Program
                  Print
                    Literal 1
                """, tree("int x = ;\nprint 1;\nint y = ;"));

        assertEquals(
                2,
                errorsFor("int x = ;\nprint 1;\nint y = ;").count()
        );
    }

    @Test
    void anErrorInsideABlockDoesNotLoseTheRestOfTheBlock() {
        assertEquals("""
                Program
                  Block
                    Assign y
                      Literal 2
                """, tree("{ x = ; y = 2; }"));

        assertEquals(1, errorsFor("{ x = ; y = 2; }").count());
    }

    @Test
    void missingSemicolonBeforeTheClosingBraceKeepsTheBlockClosed() {
        assertEquals(
                List.of("Expected ';' after assignment, but found '}'"),
                messages("{ x = 5 }")
        );
    }

    @Test
    void garbageNeverCausesAnInfiniteLoop() {
        for (String garbage : List.of(
                "} } ) ) ;;",
                ";;;",
                "(((",
                "{{{",
                "else else else",
                "= = =",
                "5 5 5 5"
        )) {
            assertTrue(errorsFor(garbage).hasErrors(), garbage);
        }
    }
}
