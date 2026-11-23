package compiler.ir;

public enum IrOp {
    LABEL, JUMP, JUMP_IF_FALSE, JUMP_IF_TRUE,
    PRINT, INPUT, RETURN, COPY,
    ADD, SUB, MUL, DIV, MOD,
    LT, LE, GT, GE, EQ, NE,
    NEG, NOT;

    public boolean isUnary() {
        return this == NEG || this == NOT;
    }

    public String symbol() {
        switch (this) {
            case ADD: return "+";
            case SUB: return "-";
            case MUL: return "*";
            case DIV: return "/";
            case MOD: return "%";
            case LT: return "<";
            case LE: return "<=";
            case GT: return ">";
            case GE: return ">=";
            case EQ: return "==";
            case NE: return "!=";
            case NEG: return "-";
            case NOT: return "!";
            default: return "";
        }
    }
}
