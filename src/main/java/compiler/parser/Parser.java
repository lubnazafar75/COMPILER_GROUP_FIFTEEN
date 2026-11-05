
package compiler.parser;

import compiler.ast.Program;
import compiler.ast.Stmt;
import compiler.error.SyntaxErrorReporter;
import compiler.lexer.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 2 entry point: turns the scanner's token list into a syntax tree.
 *
 *   List<Token> tokens = new Lexer(source).tokenize();
 *   Parser  = new Parser(tokens);
 *   Program = parser.parse();
 *   if (parser.getErrors().hasErrors()) { ... }
 *
 * parse() never throws for bad input: syntax errors are collected in the
 * SyntaxErrorReporter and the statements that could not be parsed are left
 * out of the returned Program.
 *
 *   program -> statement* EOF
 *
 * Owner: Philip Essel (0157)
 */
public final class Parser {
    private final TokenStream tokens;
    private final StatementParser statements;
    private final SyntaxErrorReporter errors;

    public Parser(List<Token> tokenList) {
        this(tokenList, new SyntaxErrorReporter());
    }

    public Parser(List<Token> tokenList, SyntaxErrorReporter errors) {
        this.errors = errors;
        this.tokens = new TokenStream(tokenList, errors);
        this.statements = new StatementParser(tokens, new ExpressionParser(tokens));
    }

    /** Parses the whole token list. */
    public Program parse() {
        List<Stmt> program = new ArrayList<>();

        while (!tokens.isAtEnd()) {
            Stmt statement = statements.parseDeclaration();

            if (statement != null) {
                program.add(statement);
            }
        }

        return new Program(program);
    }

    public SyntaxErrorReporter getErrors() {
        return errors;
    }
}