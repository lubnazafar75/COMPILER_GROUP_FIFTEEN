package compiler.semantic;

import org.junit.jupiter.api.Test;

import java.util.List;

import static compiler.semantic.SemanticTestUtil.check;
import static compiler.semantic.SemanticTestUtil.isClean;
import static compiler.semantic.SemanticTestUtil.messages;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeclarationCheckerTest {
    @Test
    void aCorrectProgramHasNoErrors() {
        assertTrue(isClean("int x = 1; string s = \"a\"; x = x + 1; print s;"));
    }
    @Test
    void aDeclarationWithNoValueIsFine() {
        assertTrue(isClean("int x; x = 5; print x;"));
    }
    @Test
    void usingAVariableBeforeItIsDeclaredIsReported() {
        assertEquals(List.of("Variable 'x' is used but never declared"), messages("print x;"));
    }
    @Test
    void aVariableCannotBeUsedInsideItsOwnDeclaration() {
        assertEquals(List.of("Variable 'x' is used but never declared"), messages("int x = x + 1;"));
    }
    @Test
    void declaringTheSameNameTwiceInOneScopeIsReported() {
        assertEquals(List.of("Variable 'x' is already declared in this scope as int x (declared on line 1)"),
            messages("int x = 1; int x = 2;"));
    }
    @Test
    void theSameNameMayBeDeclaredInsideABlock() {
        assertTrue(isClean("int x = 1; { int x = 2; print x; } print x;"));
    }
    @Test
    void aVariableDeclaredInABlockIsGoneAfterIt() {
        assertEquals(List.of("Variable 'inner' is used but never declared"),
            messages("{ int inner = 1; } print inner;"));
    }
    @Test
    void assigningTheWrongTypeIsReported() {
        assertEquals(List.of("Cannot store a string value in 'x', which is declared as int"),
            messages("int x; string s = \"a\"; x = s;"));
    }
    @Test
    void initializingWithTheWrongTypeIsReported() {
        assertEquals(List.of("Cannot store a string value in 'x', which is declared as int"),
            messages("int x = \"hello\";"));
    }
    @Test
    void aFloatCannotBeStoredInAnInt() {
        assertEquals(List.of("Cannot store a float value in 'x', which is declared as int"),
            messages("int x = 2.5;"));
    }
    @Test
    void assigningToAnUndeclaredVariableIsReported() {
        assertEquals(List.of("Variable 'y' is used but never declared"), messages("y = 1;"));
    }
    @Test
    void inputNeedsADeclaredVariable() {
        assertTrue(isClean("int n; input n;"));
        assertEquals(List.of("Variable 'n' is used but never declared"), messages("input n;"));
    }
    @Test
    void conditionsMustBeBool() {
        assertTrue(isClean("int x = 1; if (x > 0) print x;"));
        assertEquals(List.of("The condition of 'if' must be a bool, but it is int"),
            messages("int x = 1; if (x) print x;"));
        assertEquals(List.of("The condition of 'while' must be a bool, but it is string"),
            messages("string s = \"a\"; while (s) print s;"));
    }
    @Test
    void printWillNotPrintABool() {
        assertEquals(List.of("'print' cannot print a bool value"), messages("print 1 < 2;"));
    }
    @Test
    void bothBranchesOfAnIfAreChecked() {
        assertEquals(2, check("if (1 < 2) print a; else print b;").count());
    }
    @Test
    void theBodyOfAWhileIsChecked() {
        assertEquals(List.of("Variable 'missing' is used but never declared"),
            messages("while (1 < 2) { print missing; }"));
    }
    @Test
    void returnMayHaveAValueOrNot() {
        assertTrue(isClean("return;"));
        assertTrue(isClean("int x = 1; return x;"));
        assertEquals(List.of("Variable 'gone' is used but never declared"), messages("return gone;"));
    }
    @Test
    void errorsAreReportedInSourceOrder() {
        assertEquals(List.of(1, 2, 3),
            check("print a;\nprint b;\nprint c;").getErrors().stream()
                .map(compiler.error.SemanticError::getLine).toList());
    }
}
