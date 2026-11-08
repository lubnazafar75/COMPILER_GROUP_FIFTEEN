
package compiler.util;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import compiler.ast.BinaryExpr;
import compiler.ast.Expr;
import compiler.ast.GroupingExpr;
import compiler.ast.IfStmt;
import compiler.ast.LiteralExpr;
import compiler.ast.PrintStmt;
import compiler.ast.Program;
import compiler.ast.ReturnStmt;
import compiler.ast.Stmt;
import compiler.ast.UnaryExpr;
import compiler.ast.VarDeclStmt;
import compiler.ast.VariableExpr;
import compiler.lexer.Token;
import compiler.lexer.TokenType;

class AstPrinterTest {

    private final AstPrinter printer = new AstPrinter();

    private static Token token(TokenType type, String lexeme) {
        return new Token(type, lexeme, 1, 1);
    }

    private static Expr number(String text) {
        return new LiteralExpr(token(TokenType.INT_LITERAL, text));
    }

    private static Expr variable(String name) {
        return new VariableExpr(token(TokenType.IDENTIFIER, name));
    }

    private static Stmt print(Expr expression) {
        return new PrintStmt(expression);
    }

    @Test
    void emptyProgram() {
        assertEquals("Program\n", printer.print(new Program(List.of())));
    }

    @Test
    void childrenArePrintedInTheCorrectOrder() {
        Expr sum = new BinaryExpr(
                number("1"),
                token(TokenType.PLUS, "+"),
                variable("x")
        );

        Program program = new Program(List.of(print(sum)));

        assertEquals(
                "Program\n" +
                        "  Print\n" +
                        "    Binary +\n" +
                        "      Literal 1\n" +
                        "      Variable x\n",
                printer.print(program)
        );
    }

    @Test
    void unaryAndGroupingNodes() {
        Expr negated = new UnaryExpr(
                token(TokenType.MINUS, "-"),
                new GroupingExpr(variable("a"))
        );

        assertEquals(
                "Program\n" +
                        "  Print\n" +
                        "    Unary -\n" +
                        "      Group\n" +
                        "        Variable a\n",
                printer.print(new Program(List.of(print(negated))))
        );
    }

    @Test
    void declarationWithAndWithoutInitializer() {
        Token intType = token(TokenType.INT, "int");

        Program program = new Program(List.of(
                new VarDeclStmt(intType, token(TokenType.IDENTIFIER, "a"), null),
                new VarDeclStmt(intType, token(TokenType.IDENTIFIER, "b"), number("7"))
        ));

        assertEquals(
                "Program\n" +
                        "  VarDecl int a\n" +
                        "  VarDecl int b\n" +
                        "    Literal 7\n",
                printer.print(program)
        );
    }

    @Test
    void ifWithoutElsePrintsNoElseSection() {
        Stmt statement = new IfStmt(
                variable("ok"),
                print(number("1")),
                null
        );

        assertEquals(
                "Program\n" +
                        "  If\n" +
                        "    Condition\n" +
                        "      Variable ok\n" +
                        "    Then\n" +
                        "      Print\n" +
                        "        Literal 1\n",
                printer.print(new Program(List.of(statement)))
        );
    }

    @Test
    void ifWithElsePrintsBothBranches() {
        Stmt statement = new IfStmt(
                variable("ok"),
                print(number("1")),
                print(number("2"))
        );

        assertEquals(
                "Program\n" +
                        "  If\n" +
                        "    Condition\n" +
                        "      Variable ok\n" +
                        "    Then\n" +
                        "      Print\n" +
                        "        Literal 1\n" +
                        "    Else\n" +
                        "      Print\n" +
                        "        Literal 2\n",
                printer.print(new Program(List.of(statement)))
        );
    }

    @Test
    void returnWithAndWithoutValue() {
        Program program = new Program(List.of(
                new ReturnStmt(number("0")),
                new ReturnStmt(null)
        ));

        assertEquals(
                "Program\n" +
                        "  Return\n" +
                        "    Literal 0\n" +
                        "  Return\n",
                printer.print(program)
        );
    }

    @Test
    void stringLiteralsKeepTheirQuotes() {
        Expr text = new LiteralExpr(
                token(TokenType.STRING_LITERAL, "\"hello\"")
        );

        assertEquals(
                "Program\n" +
                        "  Print\n" +
                        "    Literal \"hello\"\n",
                printer.print(new Program(List.of(print(text))))
        );
    }
}
