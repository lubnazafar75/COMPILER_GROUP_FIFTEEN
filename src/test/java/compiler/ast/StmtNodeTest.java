package compiler.ast;

import compiler.lexer.Token;
import compiler.lexer.TokenType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Phase 2 tests: statement nodes, Program, and their Visitor.
 *
 * Owner: Wrightner Owusu Ansah (0161)
 */
class StmtNodeTest {

    private static Token token(TokenType type, String lexeme) {
        return new Token(type, lexeme, 1, 1);
    }

    /** A visitor that just returns the name of the node it was given. */
    private static final Stmt.Visitor<String> NAMER = new Stmt.Visitor<String>() {
        @Override public String visitVarDecl(VarDeclStmt stmt) { return "varDecl"; }
        @Override public String visitAssign(AssignStmt stmt)   { return "assign"; }
        @Override public String visitInput(InputStmt stmt)     { return "input"; }
        @Override public String visitPrint(PrintStmt stmt)     { return "print"; }
        @Override public String visitIf(IfStmt stmt)           { return "if"; }
        @Override public String visitWhile(WhileStmt stmt)     { return "while"; }
        @Override public String visitReturn(ReturnStmt stmt)   { return "return"; }
        @Override public String visitBlock(BlockStmt stmt)     { return "block"; }
    };

    private final Token x = token(TokenType.IDENTIFIER, "x");
    private final Expr one = new LiteralExpr(token(TokenType.INT_LITERAL, "1"));
    private final Stmt input = new InputStmt(x);

    @Test
    void acceptCallsTheMatchingVisitMethod() {
        assertEquals("varDecl", new VarDeclStmt(token(TokenType.INT, "int"), x, null).accept(NAMER));
        assertEquals("assign", new AssignStmt(x, one).accept(NAMER));
        assertEquals("input", input.accept(NAMER));
        assertEquals("print", new PrintStmt(token(TokenType.PRINT, "print"), one).accept(NAMER));
        assertEquals("if", new IfStmt(token(TokenType.IF, "if"), one, input, null).accept(NAMER));
        assertEquals("while", new WhileStmt(token(TokenType.WHILE, "while"), one, input).accept(NAMER));
        assertEquals("return", new ReturnStmt(token(TokenType.RETURN, "return"), null).accept(NAMER));
        assertEquals("block", new BlockStmt(token(TokenType.LBRACE, "{"), List.of(input)).accept(NAMER));
    }

    @Test
    void optionalPartsAreNullWhenAbsent() {
        VarDeclStmt declaration = new VarDeclStmt(token(TokenType.INT, "int"), x, null);
        IfStmt withoutElse = new IfStmt(token(TokenType.IF, "if"), one, input, null);
        ReturnStmt bareReturn = new ReturnStmt(token(TokenType.RETURN, "return"), null);
        assertNull(declaration.initializer());
        assertNull(withoutElse.elseBranch());
        assertNull(bareReturn.value());
    }

    @Test
    void blockKeepsACopyOfItsStatements() {
        List<Stmt> original = new ArrayList<>();
        original.add(input);
        BlockStmt block = new BlockStmt(token(TokenType.LBRACE, "{"), original);

        original.add(input); // changing the original list must not change the block
        assertEquals(1, block.statements().size());
    }

    @Test
    void programKeepsACopyOfItsStatements() {
        List<Stmt> original = new ArrayList<>();
        original.add(input);
        Program program = new Program(original);

        original.clear();
        assertEquals(1, program.statements().size());
        assertSame(input, program.statements().get(0));
    }
}