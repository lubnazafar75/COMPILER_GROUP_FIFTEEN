
package compiler.parser;

import compiler.ast.*;
import compiler.lexer.Token;
import compiler.lexer.TokenType;

/**
 * Parses declarations and statements in the Mini language.
 */
public final class StatementParser {

    private final TokenStream tokens;
    private final ExpressionParser expressions;

    public StatementParser(TokenStream tokens, ExpressionParser expressions) {
        this.tokens = tokens;
        this.expressions = expressions;
    }

    public Stmt parseDeclaration() {
        try {
            return parseStatement();
        } catch (ParseException error) {
            tokens.synchronize();
            return null;
        }
    }

    private Stmt parseStatement() {
        if (tokens.match(TokenType.INT, TokenType.STRING)) {
            return variableDeclaration();
        }

        if (tokens.match(TokenType.INPUT)) {
            return inputStatement();
        }

        if (tokens.match(TokenType.PRINT)) {
            return printStatement();
        }

        if (tokens.match(TokenType.IF)) {
            return ifStatement();
        }

        if (tokens.match(TokenType.WHILE)) {
            return whileStatement(tokens.previous());
        }

        if (tokens.match(TokenType.RETURN)) {
            return returnStatement();
        }

        if (tokens.match(TokenType.LBRACE)) {
            return blockStatement();
        }

        if (tokens.check(TokenType.IDENTIFIER)) {
            return assignmentStatement();
        }

        throw tokens.expectedError("a statement");
    }

    private Stmt variableDeclaration() {
        Token type = tokens.previous();

        Token name = tokens.consume(
                TokenType.IDENTIFIER,
                "a variable name after '" + type.getLexeme() + "'"
        );

        Expr initializer = null;

        if (tokens.match(TokenType.ASSIGN)) {
            initializer = expressions.parseExpression();
        }

        tokens.consume(
                TokenType.SEMICOLON,
                "';' after variable declaration"
        );

        return new VarDeclStmt(type, name, initializer);
    }

    private Stmt assignmentStatement() {
        Token name = tokens.advance();

        tokens.consume(
                TokenType.ASSIGN,
                "'=' after '" + name.getLexeme() + "'"
        );

        Expr value = expressions.parseExpression();

        tokens.consume(
                TokenType.SEMICOLON,
                "';' after assignment"
        );

        return new AssignStmt(name, value);
    }

    private Stmt inputStatement() {
        Token name = tokens.consume(
                TokenType.IDENTIFIER,
                "a variable name after 'input'"
        );

        tokens.consume(
                TokenType.SEMICOLON,
                "';' after input statement"
        );

        return new InputStmt(name);
    }

    private Stmt printStatement() {
        Expr expression = expressions.parseExpression();

        tokens.consume(
                TokenType.SEMICOLON,
                "';' after print statement"
        );

        return new PrintStmt(expression);
    }

    private Stmt ifStatement() {
        tokens.consume(
                TokenType.LPAREN,
                "'(' after 'if'"
        );

        Expr condition = expressions.parseExpression();

        tokens.consume(
                TokenType.RPAREN,
                "')' after the if condition"
        );

        Stmt thenBranch = parseStatement();

        Stmt elseBranch = null;

        if (tokens.match(TokenType.ELSE)) {
            elseBranch = parseStatement();
        }

        return new IfStmt(condition, thenBranch, elseBranch);
    }

    private Stmt whileStatement(Token keyword) {
        tokens.consume(
                TokenType.LPAREN,
                "'(' after 'while'"
        );

        Expr condition = expressions.parseExpression();

        tokens.consume(
                TokenType.RPAREN,
                "')' after the while condition"
        );

        Stmt body = parseStatement();

        return new WhileStmt(keyword, condition, body);
    }

    private Stmt returnStatement() {
        Expr value = null;

        if (!tokens.check(TokenType.SEMICOLON)) {
            value = expressions.parseExpression();
        }

        tokens.consume(
                TokenType.SEMICOLON,
                "';' after return statement"
        );

        return new ReturnStmt(value);
    }

    private Stmt blockStatement() {
        java.util.List<Stmt> statements = new java.util.ArrayList<>();

        while (!tokens.check(TokenType.RBRACE) && !tokens.isAtEnd()) {
            Stmt statement = parseDeclaration();

            if (statement != null) {
                statements.add(statement);
            }
        }

        tokens.consume(
                TokenType.RBRACE,
                "'}' to close the block opened on line "
                        + tokens.previous().getLine()
        );

        return new BlockStmt(statements);
    }
}
