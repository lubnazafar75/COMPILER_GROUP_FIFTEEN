
package compiler.parser;

/**
 * Exception thrown when the parser encounters invalid syntax.
 */
public class ParseException extends RuntimeException {

    public ParseException(String message) {
        super(message);
    }
}
