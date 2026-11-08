
package compiler.parser;

import compiler.ast.*;
import org.junit.jupiter.api.Test;

import static compiler.parser.ParserTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

class StatementParseTest {

    @Test
    void emptyProgramAndCommentsOnlyGiveAnEmptyTree() {
        assertEquals("Program\n", tree(""));
        assertEquals("Program\n", tree("// nothing here\n/* or here */"));
        assertFalse(errorsFor("").hasErrors());
    }

    @Test
    void declarationWithoutInitializer() {
        assertEquals(
                "Program\n" +
                        "  VarDecl int n\n",
                tree("int n;")
        );
    }

    @Test
    void declarationWithInitializer() {
        assertEquals(
                "Program\n" +
                        "  VarDecl int total\n" +
                        "    Literal 0\n" +
                        "  VarDecl string msg\n" +
                        "    Literal \"hi\"\n",
                tree("int total = 0; string msg = \"hi\";")
        );
    }

    @Test
    void assignment() {
        assertEquals(
                "Program\n" +
                        "  Assign x\n" +
                        "    Binary +\n" +
                        "      Variable x\n" +
                        "      Literal 1\n",
                tree("x = x + 1;")
        );
    }

    @Test
    void inputAndPrint() {
        assertEquals(
                "Program\n" +
                        "  Input n\n" +
                        "  Print\n" +
                        "    Variable n\n",
                tree("input n; print n;")
        );
    }

    @Test
    void ifWithoutElse() {
        assertEquals(
                "Program\n" +
                        "  If\n" +
                        "    Condition\n" +
                        "      Variable a\n" +
                        "    Then\n" +
                        "      Print\n" +
                        "        Literal 1\n",
                tree("if (a) print 1;")
        );
    }

    @Test
    void ifWithElseAndBlocks() {
        assertEquals(
                "Program\n" +
                        "  If\n" +
                        "    Condition\n" +
                        "      Binary >\n" +
                        "        Variable a\n" +
                        "        Literal 0\n" +
                        "    Then\n" +
                        "      Block\n" +
                        "        Print\n" +
                        "          Literal 1\n" +
                        "    Else\n" +
                        "      Block\n" +
                        "        Print\n" +
                        "          Literal 2\n",
                tree("if (a > 0) { print 1; } else { print 2; }")
        );
    }

    @Test
    void elseBelongsToTheNearestIf() {
        assertEquals(
                "Program\n" +
                        "  If\n" +
                        "    Condition\n" +
                        "      Variable a\n" +
                        "    Then\n" +
                        "      If\n" +
                        "        Condition\n" +
                        "          Variable b\n" +
                        "        Then\n" +
                        "          Print\n" +
                        "            Literal 1\n" +
                        "        Else\n" +
                        "          Print\n" +
                        "            Literal 2\n",
                tree("if (a) if (b) print 1; else print 2;")
        );
    }

    @Test
    void whileLoopWithBlockBody() {
        assertEquals(
                "Program\n" +
                        "  While\n" +
                        "    Condition\n" +
                        "      Variable a\n" +
                        "    Body\n" +
                        "      Block\n" +
                        "        Assign a\n" +
                        "          Variable b\n",
                tree("while (a) { a = b; }")
        );
    }

    @Test
    void returnWithAndWithoutValue() {
        assertEquals(
                "Program\n" +
                        "  Return\n" +
                        "    Literal 0\n" +
                        "  Return\n",
                tree("return 0; return;")
        );
    }

    @Test
    void blocksCanBeNestedAndEmpty() {
        assertEquals(
                "Program\n" +
                        "  Block\n" +
                        "    Block\n" +
                        "      Assign x\n" +
                        "        Literal 1\n" +
                        "    Block\n",
                tree("{ { x = 1; } {} }")
        );
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
    void printNodeStoresItsExpression() {
        Program program = parse("print value;");

        PrintStmt print = (PrintStmt) program.statements().get(0);

        assertTrue(print.expression() instanceof VariableExpr);
    }
}
