package compiler.util;

import compiler.ast.AssignStmt;
import compiler.ast.BinaryExpr;
import compiler.ast.BlockStmt;
import compiler.ast.Expr;
import compiler.ast.GroupingExpr;
import compiler.ast.IfStmt;
import compiler.ast.InputStmt;
import compiler.ast.LiteralExpr;
import compiler.ast.PrintStmt;
import compiler.ast.Program;
import compiler.ast.ReturnStmt;
import compiler.ast.Stmt;
import compiler.ast.UnaryExpr;
import compiler.ast.VarDeclStmt;
import compiler.ast.VariableExpr;
import compiler.ast.WhileStmt;

/**
 * Prints a syntax tree as an indented outline, one node per line. Children
 * are indented two spaces more than their parent.
 *
 *   Program
 *     VarDecl int total
 *       Literal 0
 *     While
 *       Condition
 *         Binary >
 *           Variable n
 *           Literal 0
 *       Body
 *         ...
 *
 * It is a Visitor: it implements both visitor interfaces, so each node
 * type has exactly one method here that decides how it is printed.
 *
 */
public final class AstPrinter implements Expr.Visitor<Void>, Stmt.Visitor<Void> {

    private final StringBuilder out = new StringBuilder();
    private int depth = 0;

    private AstPrinter() { }

    /** Returns the whole tree as text; every line ends with a newline. */
    public static String print(Program program) {
        AstPrinter printer = new AstPrinter();
        printer.line("Program");
        printer.depth++;
        for (Stmt statement : program.statements()) {
            statement.accept(printer);
        }
        return printer.out.toString();
    }

    private void line(String text) {
        out.append("  ".repeat(depth)).append(text).append('\n');
    }

    /** Prints a label line, then the child node one level deeper. */
    private void labelledStmt(String label, Stmt child) {
        line(label);
        depth++;
        child.accept(this);
        depth--;
    }

    private void labelledExpr(String label, Expr child) {
        line(label);
        depth++;
        child.accept(this);
        depth--;
    }

    // ---- Statements -----------------------------------------------------------

    @Override
    public Void visitVarDecl(VarDeclStmt stmt) {
        line("VarDecl " + stmt.type().getLexeme() + " " + stmt.name().getLexeme());
        if (stmt.initializer() != null) {
            depth++;
            stmt.initializer().accept(this);
            depth--;
        }
        return null;
    }

    @Override
    public Void visitAssign(AssignStmt stmt) {
        line("Assign " + stmt.name().getLexeme());
        depth++;
        stmt.value().accept(this);
        depth--;
        return null;
    }

    @Override
    public Void visitInput(InputStmt stmt) {
        line("Input " + stmt.name().getLexeme());
        return null;
    }

    @Override
    public Void visitPrint(PrintStmt stmt) {
        line("Print");
        depth++;
        stmt.expression().accept(this);
        depth--;
        return null;
    }

    @Override
    public Void visitIf(IfStmt stmt) {
        line("If");
        depth++;
        labelledExpr("Condition", stmt.condition());
        labelledStmt("Then", stmt.thenBranch());
        if (stmt.elseBranch() != null) {
            labelledStmt("Else", stmt.elseBranch());
        }
        depth--;
        return null;
    }

    @Override
    public Void visitWhile(WhileStmt stmt) {
        line("While");
        depth++;
        labelledExpr("Condition", stmt.condition());
        labelledStmt("Body", stmt.body());
        depth--;
        return null;
    }

    @Override
    public Void visitReturn(ReturnStmt stmt) {
        line("Return");
        if (stmt.value() != null) {
            depth++;
            stmt.value().accept(this);
            depth--;
        }
        return null;
    }

    @Override
    public Void visitBlock(BlockStmt stmt) {
        line("Block");
        depth++;
        for (Stmt inner : stmt.statements()) {
            inner.accept(this);
        }
        depth--;
        return null;
    }

    // ---- Expressions ----------------------------------------------------------

    @Override
    public Void visitBinary(BinaryExpr expr) {
        line("Binary " + expr.operator().getLexeme());
        depth++;
        expr.left().accept(this);
        expr.right().accept(this);
        depth--;
        return null;
    }

    @Override
    public Void visitUnary(UnaryExpr expr) {
        line("Unary " + expr.operator().getLexeme());
        depth++;
        expr.operand().accept(this);
        depth--;
        return null;
    }

    @Override
    public Void visitLiteral(LiteralExpr expr) {
        line("Literal " + expr.text());
        return null;
    }

    @Override
    public Void visitVariable(VariableExpr expr) {
        line("Variable " + expr.name().getLexeme());
        return null;
    }

    @Override
    public Void visitGrouping(GroupingExpr expr) {
        line("Group");
        depth++;
        expr.expression().accept(this);
        depth--;
        return null;
    }
}
