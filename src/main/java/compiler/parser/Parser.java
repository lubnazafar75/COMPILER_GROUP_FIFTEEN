
package compiler.parser;

import compiler.ast.Program;
import compiler.ast.Stmt;
import compiler.error.SyntaxErrorReporter;
import compiler.lexer.Token;
import compiler.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

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
        this.statements = new StatementParser(
                tokens,
                new ExpressionParser(tokens)
        );
    }

    public Program parse() {
        List<Stmt> program = new ArrayList<>();

        while (!tokens.isAtEnd()) {
            Stmt statement = statements.parseDeclaration();

            if (statement != null) {
                program.add(statement);
            } else if (tokens.check(TokenType.RBRACE)) {
                // Prevent an unexpected closing brace at the
                // top level from causing an infinite loop.
                tokens.advance();
            }
        }

        return new Program(program);
    }

    public SyntaxErrorReporter getErrors() {
        return errors;
    }
}
