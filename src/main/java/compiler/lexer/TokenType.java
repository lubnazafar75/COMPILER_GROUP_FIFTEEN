package compiler.lexer;

/**
 * Every kind of token the scanner can produce.
 *
 * Each constant also carries a broad {@link Category}, which the token report
 * prints and which keeps the Phase 1 output readable. The fine-grained types
 * (IF, PLUS, LPAREN ...) are what the Phase 2 parser will match on.
 *
 * Owner: Philip Essel (0157) - Phase 1 Scanner Lead
 */
public enum TokenType {

    // ---- Keywords ---------------------------------------------------------
    IF(Category.KEYWORD),
    ELSE(Category.KEYWORD),
    WHILE(Category.KEYWORD),
    RETURN(Category.KEYWORD),
    INT(Category.KEYWORD),
    STRING(Category.KEYWORD),
    PRINT(Category.KEYWORD),
    INPUT(Category.KEYWORD),

    // ---- Names and literals -----------------------------------------------
    IDENTIFIER(Category.IDENTIFIER),
    INT_LITERAL(Category.LITERAL),
    FLOAT_LITERAL(Category.LITERAL),
    STRING_LITERAL(Category.LITERAL),

    // ---- Operators --------------------------------------------------------
    PLUS(Category.OPERATOR),        // +
    MINUS(Category.OPERATOR),       // -
    STAR(Category.OPERATOR),        // *
    SLASH(Category.OPERATOR),       // /
    PERCENT(Category.OPERATOR),     // %
    ASSIGN(Category.OPERATOR),      // =
    EQUAL_EQUAL(Category.OPERATOR), // ==
    NOT(Category.OPERATOR),         // !
    NOT_EQUAL(Category.OPERATOR),   // !=
    LESS(Category.OPERATOR),        // <
    LESS_EQUAL(Category.OPERATOR),  // <=
    GREATER(Category.OPERATOR),     // >
    GREATER_EQUAL(Category.OPERATOR), // >=
    AND(Category.OPERATOR),         // &&
    OR(Category.OPERATOR),          // ||

    // ---- Punctuation ------------------------------------------------------
    LPAREN(Category.PUNCTUATION),   // (
    RPAREN(Category.PUNCTUATION),   // )
    LBRACE(Category.PUNCTUATION),   // {
    RBRACE(Category.PUNCTUATION),   // }
    LBRACKET(Category.PUNCTUATION), // [
    RBRACKET(Category.PUNCTUATION), // ]
    SEMICOLON(Category.PUNCTUATION),// ;
    COMMA(Category.PUNCTUATION),    // ,

    // ---- Special ----------------------------------------------------------
    ERROR(Category.ERROR),
    EOF(Category.EOF);

    /** Broad grouping used for the printed report. */
    public enum Category { KEYWORD, IDENTIFIER, LITERAL, OPERATOR, PUNCTUATION, ERROR, EOF }

    private final Category category;

    TokenType(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isKeyword() {
        return category == Category.KEYWORD;
    }
}