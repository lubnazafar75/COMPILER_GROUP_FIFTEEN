package compiler.parser;

import compiler.ast.AssignStmt;
import compiler.ast.BlockStmt;
import compiler.ast.Expr;
import compiler.ast.IfStmt;
import compiler.ast.InputStmt;
import compiler.ast.PrintStmt;
import compiler.ast.ReturnStmt;
import compiler.ast.Stmt;
import compiler.ast.VarDeclStmt;
import compiler.ast.WhileStmt;
import compiler.lexer.Token;
import compiler.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public final class StatementParser {
    private final TokenStream tokens;
    private final ExpressionParser expressions;

    public StatementParser(TokenStream tokens, ExpressionParser expressions) {
        this.tokens = tokens;
        this.expressions = expressions;
    }
    public Stmt parseDeclaration() {
        int start = tokens.getPosition();
        try {
            return parseStatement();
        } catch (ParseException error) {
            tokens.synchronize();
            if (tokens.getPosition() == start && !tokens.isAtEnd()) {
                tokens.advance();
            }
            return null;
        }
    }
    public Stmt parseStatement() {
        if (tokens.match(TokenType.INT, TokenType.STRING)) return varDeclaration();
        if (tokens.match(TokenType.INPUT))  return inputStatement();
        if (tokens.match(TokenType.PRINT))  return printStatement();
        if (tokens.match(TokenType.IF))     return ifStatement();
        if (tokens.match(TokenType.WHILE))  return whileStatement();
        if (tokens.match(TokenType.RETURN)) return returnStatement();
        if (tokens.match(TokenType.LBRACE)) return blockStatement();
        if (tokens.check(TokenType.IDENTIFIER)) return assignment();
        throw tokens.expectedError("a statement");
    }
    private Stmt varDeclaration() {
        Token type = tokens.previous();
        Token name = tokens.consume(TokenType.IDENTIFIER, "a variable name after '" + type.getLexeme() + "'");
        Expr initializer = null;
        if (tokens.match(TokenType.ASSIGN)) {
            initializer = expressions.parseExpression();
        }
        tokens.consume(TokenType.SEMICOLON, "';' after variable declaration");
        return new VarDeclStmt(type, name, initializer);
    }
    private Stmt assignment() {
        Token name = tokens.advance();
        tokens.consume(TokenType.ASSIGN, "'=' after '" + name.getLexeme() + "'");
        Expr value = expressions.parseExpression();
        tokens.consume(TokenType.SEMICOLON, "';' after assignment");
        return new AssignStmt(name, value);
    }
    private Stmt inputStatement() {
        Token name = tokens.consume(TokenType.IDENTIFIER, "a variable name after 'input'");
        tokens.consume(TokenType.SEMICOLON, "';' after input");
        return new InputStmt(name);
    }
    private Stmt printStatement() {
        Token keyword = tokens.previous();
        Expr value = expressions.parseExpression();
        tokens.consume(TokenType.SEMICOLON, "';' after print");
        return new PrintStmt(keyword, value);
    }
    private Stmt ifStatement() {
        Token keyword = tokens.previous();
        tokens.consume(TokenType.LPAREN, "'(' after 'if'");
        Expr condition = expressions.parseExpression();
        tokens.consume(TokenType.RPAREN, "')' after the if condition");
        Stmt thenBranch = parseStatement();
        Stmt elseBranch = null;
        if (tokens.match(TokenType.ELSE)) {
            elseBranch = parseStatement();
        }
        return new IfStmt(keyword, condition, thenBranch, elseBranch);
    }
    private Stmt whileStatement() {
        Token keyword = tokens.previous();
        tokens.consume(TokenType.LPAREN, "'(' after 'while'");
        Expr condition = expressions.parseExpression();
        tokens.consume(TokenType.RPAREN, "')' after the while condition");
        Stmt body = parseStatement();
        return new WhileStmt(keyword, condition, body);
    }
    private Stmt returnStatement() {
        Token keyword = tokens.previous();
        Expr value = null;
        if (!tokens.check(TokenType.SEMICOLON)) {
            value = expressions.parseExpression();
        }
        tokens.consume(TokenType.SEMICOLON, "';' after return");
        return new ReturnStmt(keyword, value);
    }
    private Stmt blockStatement() {
        Token open = tokens.previous();
        List<Stmt> statements = new ArrayList<>();
        while (!tokens.check(TokenType.RBRACE) && !tokens.isAtEnd()) {
            Stmt statement = parseDeclaration();
            if (statement != null) {
                statements.add(statement);
            }
        }
        tokens.consume(TokenType.RBRACE, "'}' to close the block opened on line " + open.getLine());
        return new BlockStmt(open, statements);
    }
}
