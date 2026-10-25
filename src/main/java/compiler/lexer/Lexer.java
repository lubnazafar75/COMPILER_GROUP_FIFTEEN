package compiler.lexer;

import compiler.error.ErrorReporter;

import java.util.ArrayList;
import java.util.List;

/**
 * The scanner's main loop. Looks at the next character and hands the work
 * to the right specialist scanner:
 *
 *   whitespace        -> skipped here
 *   // or slash-star  -> CommentSkipper
 *   "                 -> StringScanner
 *   digit             -> NumberScanner
 *   letter or _       -> KeywordTable (identifier / keyword)
 *   operator/punct    -> OperatorScanner
 *   anything else     -> ILLEGAL_CHARACTER error, skipped, scanning continues
 *
 * Owner: Philip Essel (0157) - Phase 1 Scanner Lead
 */
public final class Lexer {

    private final SourceReader reader;
    private final ErrorReporter errors;

    private final CommentSkipper commentSkipper = new CommentSkipper();
    private final StringScanner stringScanner = new StringScanner();
    private final NumberScanner numberScanner = new NumberScanner();
    private final KeywordTable keywordTable = new KeywordTable();
    private final OperatorScanner operatorScanner = new OperatorScanner();

    public Lexer(String source) {
        this(source, new ErrorReporter());
    }

    public Lexer(String source, ErrorReporter errors) {
        this.reader = new SourceReader(source);
        this.errors = errors;
    }

    /** Returns the next token. After the input is used up, always returns EOF. */
    public Token nextToken() {
        while (!reader.isAtEnd()) {
            char c = reader.peek();

            if (Character.isWhitespace(c)) {
                reader.advance();
                continue;
            }
            if (commentSkipper.isCommentStart(reader)) {
                commentSkipper.skip(reader, errors);
                continue;
            }
            if (c == '"') {
                return stringScanner.scan(reader, errors);
            }
            if (Character.isDigit(c)) {
                return numberScanner.scan(reader, errors);
            }
            if (KeywordTable.isIdentifierStart(c)) {
                return keywordTable.scan(reader);
            }
            if (operatorScanner.canStart(reader)) {
                return operatorScanner.scan(reader);
            }

            // Error recovery: report, skip the bad character, keep scanning.
            int line = reader.getLine();
            int column = reader.getColumn();
            reader.advance();
            errors.illegalCharacter(c, line, column);
        }
        return new Token(TokenType.EOF, "", reader.getLine(), reader.getColumn());
    }

    /** Scans the whole input. The returned list always ends with an EOF token. */
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        Token token;
        do {
            token = nextToken();
            tokens.add(token);
        } while (token.getType() != TokenType.EOF);
        return tokens;
    }

    public ErrorReporter getErrors() {
        return errors;
    }
}