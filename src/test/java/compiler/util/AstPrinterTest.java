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
        return new PrintStmt(token(TokenType.PRINT, "print"), expression);
    }

    @Test
    void emptyProgram() {
        assertEquals("Program\n", AstPrinter.print(new Program(List.of())));
    }

    @Test
    void childrenAreIndentedTwoSpacesPerLevel() {
        Expr sum = new BinaryExpr(number("1"), token(TokenType.PLUS, "+"), variable("x"));
        Program program = new Program(List.of(print(sum)));
        assertEquals("""
                Program
                  Print
                    Binary +
                      Literal 1
                      Variable x
                """, AstPrinter.print(program));
    }

    @Test
    void unaryAndGroupingNodes() {
        Expr negated = new UnaryExpr(token(TokenType.MINUS, "-"), new GroupingExpr(variable("a")));
        assertEquals("""
                Program
                  Print
                    Unary -
                      Group
                        Variable a
                """, AstPrinter.print(new Program(List.of(print(negated)))));
    }

    @Test
    void declarationWithAndWithoutInitializer() {
        Token intType = token(TokenType.INT, "int");
        Program program = new Program(List.of(
                new VarDeclStmt(intType, token(TokenType.IDENTIFIER, "a"), null),
                new VarDeclStmt(intType, token(TokenType.IDENTIFIER, "b"), number("7"))));
        assertEquals("""
                Program
                  VarDecl int a
                  VarDecl int b
                    Literal 7
                """, AstPrinter.print(program));
    }

    @Test
    void ifWithoutElsePrintsNoElseSection() {
        Stmt statement = new IfStmt(token(TokenType.IF, "if"), variable("ok"), print(number("1")), null);
        assertEquals("""
                Program
                  If
                    Condition
                      Variable ok
                    Then
                      Print
                        Literal 1
                """, AstPrinter.print(new Program(List.of(statement))));
    }

    @Test
    void ifWithElsePrintsBothBranches() {
        Stmt statement = new IfStmt(token(TokenType.IF, "if"), variable("ok"),
                print(number("1")), print(number("2")));
        assertEquals("""
                Program
                  If
                    Condition
                      Variable ok
                    Then
                      Print
                        Literal 1
                    Else
                      Print
                        Literal 2
                """, AstPrinter.print(new Program(List.of(statement))));
    }

    @Test
    void returnWithAndWithoutValue() {
        Token keyword = token(TokenType.RETURN, "return");
        Program program = new Program(List.of(
                new ReturnStmt(keyword, number("0")),
                new ReturnStmt(keyword, null)));
        assertEquals("""
                Program
                  Return
                    Literal 0
                  Return
                """, AstPrinter.print(program));
    }

    @Test
    void stringLiteralsKeepTheirQuotes() {
        Expr text = new LiteralExpr(token(TokenType.STRING_LITERAL, "\"hello\""));
        assertEquals("Program\n  Print\n    Literal \"hello\"\n",
                AstPrinter.print(new Program(List.of(print(text)))));
    }
}
