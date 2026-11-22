package compiler.semantic;
import compiler.ast.AssignStmt;
import compiler.ast.BlockStmt;
import compiler.ast.IfStmt;
import compiler.ast.InputStmt;
import compiler.ast.PrintStmt;
import compiler.ast.ReturnStmt;
import compiler.ast.Stmt;
import compiler.ast.VarDeclStmt;
import compiler.ast.WhileStmt;
import compiler.error.SemanticErrorReporter;
import compiler.lexer.Token;
import compiler.lexer.TokenType;

public final class DeclarationChecker implements Stmt.Visitor<Void> {
    private final SymbolTable symbols;
    private final TypeChecker types;
    private final SemanticErrorReporter errors;

    public DeclarationChecker(SymbolTable symbols, TypeChecker types, SemanticErrorReporter errors) {
        this.symbols = symbols;
        this.types = types;
        this.errors = errors;
    }
    public void check(Stmt statement) {
        statement.accept(this);
    }
    @Override
    public Void visitVarDecl(VarDeclStmt stmt) {
        Type declared = typeOfKeyword(stmt.type());
        if (stmt.initializer() != null) {
            Type value = types.typeOf(stmt.initializer());
            if (!TypeRules.canAssign(declared, value)) {
                errors.report(stmt.name(),"Cannot store a " + value + " value in '" + stmt.name().getLexeme() + "', which is declared as " + declared);
            }
        }
        declareOrReportDuplicate(stmt.name(), declared);
        return null;
    }
    @Override
    public Void visitAssign(AssignStmt stmt) {
        Symbol target = lookupOrReport(stmt.name());
        Type value = types.typeOf(stmt.value());
        if (target != null && !TypeRules.canAssign(target.type(), value)) {
            errors.report(stmt.name(),"Cannot store a " + value + " value in '" + stmt.name().getLexeme()+ "', which is declared as " + target.type());
        }
        return null;
    }
    @Override
    public Void visitInput(InputStmt stmt) {
        lookupOrReport(stmt.name());
        return null;
    }
    @Override
    public Void visitPrint(PrintStmt stmt) {
        Type value = types.typeOf(stmt.expression());
        if (value == Type.BOOL) {
            errors.report(stmt.keyword(), "'print' cannot print a bool value");
        }
        return null;
    }
    @Override
    public Void visitIf(IfStmt stmt) {
        requireBoolCondition(stmt.keyword(), types.typeOf(stmt.condition()), "if");
        check(stmt.thenBranch());
        if (stmt.elseBranch() != null) {
          check(stmt.elseBranch());
        }
        return null;
    }
    @Override
    public Void visitWhile(WhileStmt stmt) {
        requireBoolCondition(stmt.keyword(), types.typeOf(stmt.condition()), "while");
        check(stmt.body());
        return null;
    }
    @Override
    public Void visitReturn(ReturnStmt stmt) {
        if (stmt.value() != null) {
            types.typeOf(stmt.value());
        }
        return null;
    }
    @Override
    public Void visitBlock(BlockStmt stmt) {
        symbols.enterScope();
        for (Stmt inner : stmt.statements()) {
            check(inner);
        }
        symbols.exitScope();
        return null;
    }
    private Type typeOfKeyword(Token keyword) {
        if (keyword.getType() == TokenType.INT) {
            return Type.INT;
        }
        if (keyword.getType() == TokenType.STRING) {
            return Type.STRING;
        }
            return Type.ERROR;
    }
    private void declareOrReportDuplicate(Token name, Type type) {
        Symbol added = new Symbol(name.getLexeme(), type, name.getLine(), name.getColumn());
        Symbol existing = symbols.declare(added);
        if (existing != null) {
            errors.report(name,"Variable '" + name.getLexeme() + "' is already declared in this scope as " + existing.describe());
        }
    }
    private Symbol lookupOrReport(Token name) {
        Symbol symbol = symbols.lookup(name.getLexeme());
        if (symbol == null) {
            errors.report(name, "Variable '" + name.getLexeme() + "' is used but never declared");
        }
        return symbol;
    }
    private void requireBoolCondition(Token keyword, Type condition, String what) {
        if (!condition.isError() && condition != Type.BOOL) {
            errors.report(keyword, "The condition of '" + what + "' must be a bool, but it is " + condition);
        }
    }
}
