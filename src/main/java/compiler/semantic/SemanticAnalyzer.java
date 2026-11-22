package compiler.semantic;

import compiler.ast.Program;
import compiler.ast.Stmt;
import compiler.error.SemanticErrorReporter;

/**
 * Phase 3 entry point: checks that a parsed program makes sense.
 *
 * SemanticAnalyzer analyzer = new SemanticAnalyzer();
 * analyzer.analyze(program);
 * if (analyzer.getErrors().hasErrors()) { ... }
 *
 * It puts a SymbolTable, a TypeChecker and a DeclarationChecker together
 * and runs them over every statement in the program. Mistakes are
 * collected, not thrown, so one run reports all of them.
 *
 * Owner: Philip Essel (0157)
 */
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

    /** Checks the whole program. Call getErrors() afterwards. */
    public void analyze(Program program) {
        for (Stmt statement : program.statements()) {
            declarations.check(statement);
        }
    }

    /** The mistakes found, in the order they were found. */
    public SemanticErrorReporter getErrors() {
        return errors;
    }

    /** The symbol table, after analyze() has run. Used by the IR generator. */
    public SymbolTable getSymbols() {
        return symbols;
    }
}