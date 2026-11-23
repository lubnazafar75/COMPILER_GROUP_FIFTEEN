package compiler.semantic;

/**
 * The types a value can have in the mini language.
 *
 * INT and STRING are the two types a program can declare. FLOAT appears
 * only as the type of a literal like 3.5 or of an arithmetic expression
 * that contains one. BOOL is the type of a comparison such as  a > 5  and
 * of  &&, || and !; it cannot be declared or stored in a variable.
 *
 * ERROR is not a real type. It is returned after a type mistake has been
 * reported, and every rule accepts it silently, so one mistake produces
 * one message instead of a cascade of follow-on messages.
 *
 * Owner: Egmine James (0163)
 */
public enum Type {

    INT("int"),
    FLOAT("float"),
    STRING("string"),
    BOOL("bool"),
    ERROR("<error>");

    private final String text;

    Type(String text) {
        this.text = text;
    }

    /** The name used in error messages, for example "int". */
    @Override
    public String toString() {
        return text;
    }

    /** True for INT and FLOAT: the types arithmetic and comparison accept. */
    public boolean isNumeric() {
        return this == INT || this == FLOAT;
    }

    /** True for a type that a variable may be declared with. */
    public boolean isDeclarable() {
        return this == INT || this == STRING;
    }

    /** True if this type came from an earlier mistake and should be ignored. */
    public boolean isError() {
        return this == ERROR;
    }
}
