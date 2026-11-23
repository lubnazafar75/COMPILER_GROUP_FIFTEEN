package compiler.ir;

public record IrInstruction(
        IrOp op,
        String result,
        String left,
        String right
) {
    public static IrInstruction label(String name) {
        return new IrInstruction(IrOp.LABEL, name, null, null);
    }

    public static IrInstruction jump(String target) {
        return new IrInstruction(IrOp.JUMP, null, target, null);
    }

    public static IrInstruction jumpIfFalse(String condition, String target) {
        return new IrInstruction(IrOp.JUMP_IF_FALSE, null, condition, target);
    }

    public static IrInstruction jumpIfTrue(String condition, String target) {
        return new IrInstruction(IrOp.JUMP_IF_TRUE, null, condition, target);
    }

    public static IrInstruction print(String value) {
        return new IrInstruction(IrOp.PRINT, null, value, null);
    }

    public static IrInstruction input(String variable) {
        return new IrInstruction(IrOp.INPUT, variable, null, null);
    }

    public static IrInstruction returnValue(String value) {
        return new IrInstruction(IrOp.RETURN, null, value, null);
    }

    public static IrInstruction copy(String target, String value) {
        return new IrInstruction(IrOp.COPY, target, value, null);
    }

    public static IrInstruction unary(IrOp op, String target, String value) {
        return new IrInstruction(op, target, value, null);
    }

    public static IrInstruction binary(
            IrOp op, String target, String left, String right) {
        return new IrInstruction(op, target, left, right);
    }
}
