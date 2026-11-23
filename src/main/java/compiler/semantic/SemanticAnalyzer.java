
package compiler.semantic;

import compiler.ast.Program;
import compiler.ast.Stmt;
import compiler.error.SemanticErrorReporter;

public final class SemanticAnalyzer {

    private final SymbolTable symbols = new SymbolTable();
    private final SemanticErrorReporter errors;
    private final TypeChecker types;
    private final DeclarationChecker declarations;

    public SemanticAnalyzer() {
        this(new SemanticErrorReporter());
    }

    public SemanticAnalyzer(SemanticErrorReporter errors) {
        this.errors = errors;
        this.types = new TypeChecker(symbols, errors);
        this.declarations = new DeclarationChecker(symbols, types, errors);
    }

    public void analyze(Program program) {
        for (Stmt statement : program.statements()) {
            declarations.check(statement);
        }
    }

    public SemanticErrorReporter getErrors() {
        return errors;
    }

    public SymbolTable getSymbols() {
        return symbols;
    }
}
