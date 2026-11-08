
package compiler.util;

import compiler.ast.*;

public final class AstPrinter
        implements Expr.Visitor<String>, Stmt.Visitor<String> {

    private int depth = 0;

    private String indent() {
        return "  ".repeat(depth);
    }

    private String node(String name) {
        return indent() + name + "\n";
    }

    private String child(Object child) {
        depth++;
        try {
            if (child instanceof Expr expression) {
                return expression.accept(this);
            }
            if (child instanceof Stmt statement) {
                return statement.accept(this);
            }
            return "";
        } finally {
            depth--;
        }
    }

    public String print(Program program) {
        depth = 0;
        StringBuilder result = new StringBuilder("Program\n");

        for (Stmt statement : program.statements()) {
            result.append(child(statement));
        }

        return result.toString();
    }

    public String printExpression(Expr expression) {
        depth = 0;
        return expression.accept(this);
    }

    // Expression visitors

    @Override
    public String visitBinary(BinaryExpr expr) {
        return node("Binary " + expr.operator().getLexeme())
                + child(expr.left())
                + child(expr.right());
    }

    @Override
    public String visitUnary(UnaryExpr expr) {
        return node("Unary " + expr.operator().getLexeme())
                + child(expr.operand());
    }

    @Override
    public String visitLiteral(LiteralExpr expr) {
        return node("Literal " + expr.text());
    }

    @Override
    public String visitVariable(VariableExpr expr) {
        return node("Variable " + expr.name().getLexeme());
    }

    @Override
    public String visitGrouping(GroupingExpr expr) {
        return node("Group") + child(expr.expression());
    }

    // Statement visitors

    @Override
    public String visitVarDecl(VarDeclStmt stmt) {
        String result = node(
                "VarDecl " + stmt.type().getLexeme()
                        + " " + stmt.name().getLexeme()
        );

        if (stmt.initializer() != null) {
            result += child(stmt.initializer());
        }

        return result;
    }

    @Override
    public String visitAssign(AssignStmt stmt) {
        return node("Assign " + stmt.name().getLexeme())
                + child(stmt.value());
    }

    @Override
    public String visitInput(InputStmt stmt) {
        return node("Input " + stmt.name().getLexeme());
    }

    @Override
    public String visitPrint(PrintStmt stmt) {
        return node("Print") + child(stmt.expression());
    }

    @Override
    public String visitIf(IfStmt stmt) {
        StringBuilder result = new StringBuilder(node("If"));

        result.append(nodeAtChild("Condition", stmt.condition()));
        result.append(nodeAtChild("Then", stmt.thenBranch()));

        if (stmt.elseBranch() != null) {
            depth++;
            try {
                result.append(node("Else"));
                result.append(child(stmt.elseBranch()));
            } finally {
                depth--;
            }
        }

        return result.toString();
    }

    @Override
    public String visitWhile(WhileStmt stmt) {
        StringBuilder result = new StringBuilder(node("While"));

        result.append(nodeAtChild("Condition", stmt.condition()));
        result.append(nodeAtChild("Body", stmt.body()));

        return result.toString();
    }

    private String nodeAtChild(String label, Object value) {
        depth++;
        try {
            String result = node(label);
            depth++;
            try {
                if (value instanceof Expr expression) {
                    result += expression.accept(this);
                } else if (value instanceof Stmt statement) {
                    result += statement.accept(this);
                }
            } finally {
                depth--;
            }
            return result;
        } finally {
            depth--;
        }
    }

    @Override
    public String visitReturn(ReturnStmt stmt) {
        String result = node("Return");

        if (stmt.value() != null) {
            result += child(stmt.value());
        }

        return result;
    }

    @Override
    public String visitBlock(BlockStmt stmt) {
        StringBuilder result = new StringBuilder(node("Block"));

        for (Stmt statement : stmt.statements()) {
            result.append(child(statement));
        }

        return result.toString();
    }
}
