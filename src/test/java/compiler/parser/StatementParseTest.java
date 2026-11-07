package compiler.parser;

import compiler.ast.AssignStmt;
import compiler.ast.BlockStmt;
import compiler.ast.IfStmt;
import compiler.ast.InputStmt;
import compiler.ast.PrintStmt;
import compiler.ast.Program;
import compiler.ast.ReturnStmt;
import compiler.ast.VarDeclStmt;
import compiler.ast.WhileStmt;
import org.junit.jupiter.api.Test;

import static compiler.parser.ParserTestUtil.errorsFor;
import static compiler.parser.ParserTestUtil.parse;
import static compiler.parser.ParserTestUtil.tree;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatementParseTest {
    @Test
    void emptyProgramAndCommentsOnlyGiveAnEmptyTree() {
        assertEquals("Program\n", tree(""));
        assertEquals("Program\n", tree("// nothing here\n/* or here */"));
        assertFalse(errorsFor("").hasErrors());
    }
    @Test
    void declarationWithoutInitializer() {
        assertEquals("""
                Program
                  VarDecl int n
                """, tree("int n;"));
    }
    @Test
    void declarationWithInitializer() {
        assertEquals("""
                Program
                  VarDecl int total
                    Literal 0
                  VarDecl string msg
                    Literal "hi"
                """, tree("int total = 0; string msg = \"hi\";"));
    }
    @Test
    void assignment() {
        assertEquals("""
                Program
                  Assign x
                    Binary +
                      Variable x
                      Literal 1
                """, tree("x = x + 1;"));
    }
    @Test
    void inputAndPrint() {
        assertEquals("""
                Program
                  Input n
                  Print
                    Variable n
                """, tree("input n; print n;"));
    }
    @Test
    void ifWithoutElse() {
        assertEquals("""
                Program
                  If
                    Condition
                      Variable a
                    Then
                      Print
                        Literal 1
                """, tree("if (a) print 1;"));
    }
    @Test
    void ifWithElseAndBlocks() {
        assertEquals("""
                Program
                  If
                    Condition
                      Binary >
                        Variable a
                        Literal 0
                    Then
                      Block
                        Print
                          Literal 1
                    Else
                      Block
                        Print
                          Literal 2
                """, tree("if (a > 0) { print 1; } else { print 2; }"));
    }
    @Test
    void elseBelongsToTheNearestIf() {
        assertEquals("""
                Program
                  If
                    Condition
                      Variable a
                    Then
                      If
                        Condition
                          Variable b
                        Then
                          Print
                            Literal 1
                        Else
                          Print
                            Literal 2
                """, tree("if (a) if (b) print 1; else print 2;"));
    }
    @Test
    void whileLoopWithBlockBody() {
        assertEquals("""
                Program
                  While
                    Condition
                      Variable a
                    Body
                      Block
                        Assign a
                          Variable b
                """, tree("while (a) { a = b; }"));
    }
    @Test
    void returnWithAndWithoutValue() {
        assertEquals("""
                Program
                  Return
                    Literal 0
                  Return
                """, tree("return 0; return;"));
    }
    @Test
    void blocksCanBeNestedAndEmpty() {
        assertEquals("""
                Program
                  Block
                    Block
                      Assign x
                        Literal 1
                    Block
                """, tree("{ { x = 1; } {} }"));
    }
    @Test
    void statementsKeepTheirSourceOrder() {
        Program program = parse("int a; a = 1; print a;");
        assertEquals(3, program.statements().size());
        assertTrue(program.statements().get(0) instanceof VarDeclStmt);
        assertTrue(program.statements().get(1) instanceof AssignStmt);
        assertTrue(program.statements().get(2) instanceof PrintStmt);
    }
    @Test
    void nodesAreOfTheExpectedClasses() {
        Program program = parse("input n; if (n) {} while (n) {} return;");
        assertTrue(program.statements().get(0) instanceof InputStmt);
        assertTrue(program.statements().get(1) instanceof IfStmt);
        assertTrue(program.statements().get(2) instanceof WhileStmt);
        assertTrue(program.statements().get(3) instanceof ReturnStmt);
        IfStmt ifStmt = (IfStmt) program.statements().get(1);
        assertTrue(ifStmt.thenBranch() instanceof BlockStmt);
    }
    @Test
    void nodesRememberTheirPosition() {
        Program program = parse("int a;\n  print a;");
        PrintStmt print = (PrintStmt) program.statements().get(1);
        assertEquals(2, print.keyword().getLine());
        assertEquals(3, print.keyword().getColumn());
    }
}
