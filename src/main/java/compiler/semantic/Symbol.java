package compiler.semantic;

public record Symbol(String name, Type type, int line, int column) {

    public String describe() {
        return type + " " + name + " (declared on line " + line + ")";
    }
}