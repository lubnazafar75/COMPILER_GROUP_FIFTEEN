package compiler.semantic;

public enum Type {
    INT,
    FLOAT,
    STRING,
    BOOL,
    ERROR;

    public boolean isNumeric() {
        return this == INT || this == FLOAT;
    }

    public boolean isError() {
        return this == ERROR;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}