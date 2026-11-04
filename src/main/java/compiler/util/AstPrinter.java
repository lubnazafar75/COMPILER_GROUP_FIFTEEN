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
 * Prints a syntax tree as an indented outline, one node per line.
 * Children are indented two spaces more than their parent.
 *
 * It is a Visitor: it implements both visitor interfaces, so each node
 * type has exactly one method here that decides how it is printed.
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
}