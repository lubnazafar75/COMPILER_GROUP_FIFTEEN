
package compiler.ir;

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
import compiler.lexer.TokenType;

public final class IrGenerator implements Expr.Visitor<String>, Stmt.Visitor<Void> {

    private final IrBuilder builder = new IrBuilder();

    public IrProgram generate(Program program) {
        for (Stmt statement : program.statements()) {
            statement.accept(this);
        }
        return builder.build();
    }

    @Override
    public String visitBinary(BinaryExpr expr) {
        TokenType operator = expr.operator().getType();

        if (operator == TokenType.AND || operator == TokenType.OR) {
            return shortCircuit(expr, operator == TokenType.AND);
        }

        String left = expr.left().accept(this);
        String right = expr.right().accept(this);
        String result = builder.newTemp();

        builder.emit(IrInstruction.binary(binaryOp(operator), result, left, right));
        return result;
    }

    @Override
    public String visitUnary(UnaryExpr expr) {
        String operand = expr.operand().accept(this);
        String result = builder.newTemp();
        IrOp op = expr.operator().getType() == TokenType.NOT ? IrOp.NOT : IrOp.NEG;
        builder.emit(IrInstruction.unary(op, result, operand));
        return result;
    }

    @Override
    public String visitLiteral(LiteralExpr expr) {
        return expr.text();
    }

    @Override
    public String visitVariable(VariableExpr expr) {
        return expr.name().getLexeme();
    }

    @Override
    public String visitGrouping(GroupingExpr expr) {
        return expr.expression().accept(this);
    }

    @Override
    public Void visitVarDecl(VarDeclStmt stmt) {
        if (stmt.initializer() != null) {
            String value = stmt.initializer().accept(this);
            builder.emit(IrInstruction.copy(stmt.name().getLexeme(), value));
        }
        return null;
    }

    @Override
    public Void visitAssign(AssignStmt stmt) {
        String value = stmt.value().accept(this);
        builder.emit(IrInstruction.copy(stmt.name().getLexeme(), value));
        return null;
    }

    @Override
    public Void visitInput(InputStmt stmt) {
        builder.emit(IrInstruction.input(stmt.name().getLexeme()));
        return null;
    }

    @Override
    public Void visitPrint(PrintStmt stmt) {
        builder.emit(IrInstruction.print(stmt.expression().accept(this)));
        return null;
    }

    @Override
    public Void visitIf(IfStmt stmt) {
        String condition = stmt.condition().accept(this);
        String afterThen = builder.newLabel();

        builder.emit(IrInstruction.jumpIfFalse(condition, afterThen));
        stmt.thenBranch().accept(this);

        if (stmt.elseBranch() == null) {
            builder.emit(IrInstruction.label(afterThen));
            return null;
        }

        String end = builder.newLabel();
        builder.emit(IrInstruction.jump(end));
        builder.emit(IrInstruction.label(afterThen));
        stmt.elseBranch().accept(this);
        builder.emit(IrInstruction.label(end));
        return null;
    }

    @Override
    public Void visitWhile(WhileStmt stmt) {
        String top = builder.newLabel();
        String end = builder.newLabel();

        builder.emit(IrInstruction.label(top));
        String condition = stmt.condition().accept(this);
        builder.emit(IrInstruction.jumpIfFalse(condition, end));
        stmt.body().accept(this);
        builder.emit(IrInstruction.jump(top));
        builder.emit(IrInstruction.label(end));
        return null;
    }

    @Override
    public Void visitReturn(ReturnStmt stmt) {
        String value = stmt.value() == null ? null : stmt.value().accept(this);
        builder.emit(IrInstruction.returnValue(value));
        return null;
    }

    @Override
    public Void visitBlock(BlockStmt stmt) {
        for (Stmt inner : stmt.statements()) {
            inner.accept(this);
        }
        return null;
    }

    private String shortCircuit(BinaryExpr expr, boolean isAnd) {
        String leftValue = expr.left().accept(this);
        String result = builder.newTemp();
        String end = builder.newLabel();

        builder.emit(IrInstruction.copy(result, leftValue));
        builder.emit(isAnd
                ? IrInstruction.jumpIfFalse(result, end)
                : IrInstruction.jumpIfTrue(result, end));
        builder.emit(IrInstruction.copy(result, expr.right().accept(this)));
        builder.emit(IrInstruction.label(end));
        return result;
    }

    private static IrOp binaryOp(TokenType operator) {
        switch (operator) {
            case PLUS: return IrOp.ADD;
            case MINUS: return IrOp.SUB;
            case STAR: return IrOp.MUL;
            case SLASH: return IrOp.DIV;
            case PERCENT: return IrOp.MOD;
            case LESS: return IrOp.LT;
            case LESS_EQUAL: return IrOp.LE;
            case GREATER: return IrOp.GT;
            case GREATER_EQUAL: return IrOp.GE;
            case EQUAL_EQUAL: return IrOp.EQ;
            case NOT_EQUAL: return IrOp.NE;
            default:
                throw new IllegalStateException("Not a binary operator: " + operator);
        }
    }
}

