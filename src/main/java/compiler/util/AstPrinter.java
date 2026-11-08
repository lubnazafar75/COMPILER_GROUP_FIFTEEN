
package compiler.util;

import compiler.ast.*;

public final class AstPrinter
        implements Expr.Visitor<String>, Stmt.Visitor<String> {

    public String print(Program program) {
        StringBuilder result = new StringBuilder("Program\n");

        for (Stmt statement : program.statements()) {
            result.append(statement.accept(this));
        }

        return result.toString();
    }

    private String line(String name, String child) {
        return name + "\n" + child;
    }

    @Override
    public String visitBinary(BinaryExpr expr) {
        return line(
                "Binary " + expr.operator().getLexeme(),
                expr.left().accept(this) + expr.right().accept(this)
        );
    }

    @Override
    public String visitUnary(UnaryExpr expr) {
        return line(
                "Unary " + expr.operator().getLexeme(),
                expr.operand().accept(this)
        );
    }

    @Override
    public String visitLiteral(LiteralExpr expr) {
        return "Literal " + expr.text() + "\n";
    }

    @Override
    public String visitVariable(VariableExpr expr) {
        return "Variable " + expr.name().getLexeme() + "\n";
    }

    @Override
    public String visitGrouping(GroupingExpr expr) {
        return line("Grouping", expr.expression().accept(this));
    }

    @Override
    public String visitVarDecl(VarDeclStmt stmt) {
        String result = "VarDecl " + stmt.name().getLexeme() + "\n";

        if (stmt.initializer() != null) {
            result += stmt.initializer().accept(this);
        }

        return result;
    }

    @Override
    public String visitAssign(AssignStmt stmt) {
        return line(
                "Assign " + stmt.name().getLexeme(),
                stmt.value().accept(this)
        );
    }

    @Override
    public String visitInput(InputStmt stmt) {
        return "Input " + stmt.name().getLexeme() + "\n";
    }

    @Override
    public String visitPrint(PrintStmt stmt) {
        return line("Print", stmt.expression().accept(this));
    }

    @Override
    public String visitIf(IfStmt stmt) {
        String result = line("If", stmt.condition().accept(this));
        result += stmt.thenBranch().accept(this);

        if (stmt.elseBranch() != null) {
            result += "Else\n";
            result += stmt.elseBranch().accept(this);
        }

        return result;
    }

    @Override
    public String visitWhile(WhileStmt stmt) {
        return line("While", stmt.condition().accept(this))
                + stmt.body().accept(this);
    }

    @Override
    public String visitReturn(ReturnStmt stmt) {
        String result = "Return\n";

        if (stmt.value() != null) {
            result += stmt.value().accept(this);
        }

        return result;
    }

    @Override
    public String visitBlock(BlockStmt stmt) {
        StringBuilder result = new StringBuilder("Block\n");

        for (Stmt statement : stmt.statements()) {
            result.append(statement.accept(this));
        }

        return result.toString();
    }
}
