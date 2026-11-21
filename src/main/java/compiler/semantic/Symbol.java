package compiler.semantic;

/**
 * One entry in the symbol table: a variable the program declared.
 *
 * The line and column are those of the name in the declaration, so a
 * "already declared" message can point at where the first one was.
 *
 * Owner: Egmine James (0163)
 */
public record Symbol(String name, Type type, int line, int column) {

    /**
     * "int x (declared on line 3)" - used in error messages.
     */
    public String describe() {
        return type + " " + name + " (declared on line " + line + ")";
    }
}
