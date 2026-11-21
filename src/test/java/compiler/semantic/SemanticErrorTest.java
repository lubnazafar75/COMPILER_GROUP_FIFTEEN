package compiler.semantic;
import compiler.error.SemanticError;
import compiler.error.SemanticErrorReporter;
import org.junit.jupiter.api.Test;
import java.util.List;
import static compiler.semantic.SemanticTestUtil.check;
import static compiler.semantic.SemanticTestUtil.messages;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
/**
 * Phase 3 tests: the error classes themselves, the exact position of
 * each message, and the sample file samples/semantic_errors.mini.mini.
 *
 * Owner: Lubna Zafar (0167)
 */
class SemanticErrorTest {
    // The same text as samples/semantic_errors.mini.mini.
    private static final String SAMPLE = """
 // semantic_errors.mini - scans and parses correctly, but does not make
 // sense. Six deliberate semantic errors, one per numbered comment.
 int x = 5;
 int x = 7; // 1. x is already declared in this scope
 y = 10; // 2. y was never declared
 string name = "Lubna";
 x = name; // 3. a string value cannot go in an int variable
 print x + name; // 4. + cannot be used with int and string
 if (x) { // 5. the condition must be a bool, not an int
 print total; // 6. total was never declared
 }
 return x;
 """;
// ---- the error classes ---------------------------------------------
@Test
void anErrorKeepsItsMessageAndPosition() {
    SemanticError error = new SemanticError("something is wrong", 4, 9);
    assertEquals("something is wrong", error.getMessage());
    assertEquals(4, error.getLine());
    assertEquals(9, error.getColumn());
}
    @Test
    void anErrorPrintsInTheStandardFormat() {
        assertEquals("Semantic Error [Line 4, Col 9]: something is wrong",
                new SemanticError("something is wrong", 4, 9).toString());
    }
    @Test
    void aNewReporterIsEmpty() {
        SemanticErrorReporter reporter = new SemanticErrorReporter();
        assertFalse(reporter.hasErrors());
        assertEquals(0, reporter.count());
        assertTrue(reporter.getErrors().isEmpty());
    }
    @Test
    void theReporterKeepsErrorsInOrder() {
        SemanticErrorReporter reporter = new SemanticErrorReporter();
        reporter.report(1, 1, "first");
        reporter.report(2, 1, "second");
        assertTrue(reporter.hasErrors());
        assertEquals(2, reporter.count());
        assertEquals("first", reporter.getErrors().get(0).getMessage());
        assertEquals("second", reporter.getErrors().get(1).getMessage());
    }
    @Test
    void theErrorListCannotBeChangedFromOutside() {
        SemanticErrorReporter reporter = new SemanticErrorReporter();
        reporter.report(1, 1, "only");
        assertThrows(UnsupportedOperationException.class, () -> reporter.getErrors().clear());
    }
    // ---- positions -------------------------------------------------------
    @Test
    void anUndeclaredVariableIsReportedWhereItIsUsed() {
        SemanticError error = check("int a = 1;\nprint b;").getErrors().get(0);
        assertEquals(2, error.getLine());
        assertEquals(7, error.getColumn());
    }
    @Test
    void aDuplicateIsReportedAtTheSecondNameNotTheFirst() {
        SemanticError error = check("int x = 1;\nint x = 2;").getErrors().get(0);
        assertEquals(2, error.getLine());
        assertTrue(error.getMessage().contains("declared on line 1"),
                "the message points back at the first one");
    }
    @Test
    void aTypeMismatchIsReportedAtTheVariableName() {
        SemanticError error = check("int x = \"a\";").getErrors().get(0);
        assertEquals(1, error.getLine());
        assertEquals(5, error.getColumn());
    }
    @Test
    void aBadOperatorIsReportedAtTheOperator() {
        SemanticError error = check("print 1 + \"a\";").getErrors().get(0);
        assertEquals(1, error.getLine());
        assertEquals(9, error.getColumn());
    }
    @Test
    void aBadConditionIsReportedAtTheKeyword() {
        SemanticError error = check("int x = 1;\nif (x) print x;").getErrors().get(0);
        assertEquals(2, error.getLine());
        assertEquals(1, error.getColumn());
    }
    // ---- the sample file --------------------------------------------------
    @Test
    void theSampleFileHasExactlySixErrors() {
        assertEquals(6, check(SAMPLE).count());
    }
    @Test
    void theSampleFileReportsThemOnTheExpectedLines() {
        assertEquals(List.of(5, 7, 10, 12, 14, 15),
                check(SAMPLE).getErrors().stream().map(SemanticError::getLine).toList());
    }
    @Test
    void theSampleFileGivesTheExpectedMessages() {
        assertEquals(List.of(
                        "Variable 'x' is already declared in this scope as int x (declared on line 4)",
                        "Variable 'y' is used but never declared",
                        "Cannot store a string value in 'x', which is declared as int",
                        "Operator '+' cannot be used with int and string",
                        "The condition of 'if' must be a bool, but it is int",
                        "Variable 'total' is used but never declared"),
                messages(SAMPLE));
    }
    @Test
    void checkingNeverStopsAtTheFirstMistake() {
        // Three separate mistakes, three messages: the checker keeps going.
        assertEquals(3, check("print a;\nprint b;\nprint c;").count());
    }
}
