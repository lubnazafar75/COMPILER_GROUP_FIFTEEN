package compiler.util;
import compiler.ir.IrInstruction;
import compiler.ir.IrProgram;
/**
 * Prints three-address code in a readable form.
 *
 * Labels start at the left margin; every other instruction is indented,
 * so the blocks between labels are easy to see:
 *
 *     t1 = a + 3
 *     t2 = t1 * 2
 *     b = t2
 *   L1:
 *     t3 = a > 0
 *     if_false t3 goto L2
 *     print a
 *     goto L1
 *   L2:
 *
 */
public final class IrPrinter {
    private static final String INDENT = "  ";
    private IrPrinter() { }
    /** The whole program, one instruction per line, ending with a newline. */
    public static String print(IrProgram program) {
        StringBuilder out = new StringBuilder();
        for (IrInstruction instruction : program.instructions()) {
            out.append(line(instruction)).append('\n');
        }
        return out.toString();
    }
    the files you add
    /** One instruction, with its indentation but without a newline. */
    public static String line(IrInstruction instruction) {
        switch (instruction.op()) {
            case LABEL:
                return instruction.result() + ":";
            case JUMP:
                return INDENT + "goto " + instruction.left();
            case JUMP_IF_FALSE:
                return INDENT + "if_false " + instruction.left() + " goto " + instruction.right();
            case JUMP_IF_TRUE:
                return INDENT + "if_true " + instruction.left() + " goto " + instruction.right();
            case PRINT:
                return INDENT + "print " + instruction.left();
            case INPUT:
                return INDENT + "input " + instruction.result();
            case RETURN:
                return INDENT + (instruction.left() == null ? "return" : "return " + instruction.left());
            case COPY:
                return INDENT + instruction.result() + " = " + instruction.left();
            default:
                break;
        }
        if (instruction.op().isUnary()) {
            return INDENT + instruction.result() + " = "
                    + instruction.op().symbol() + instruction.left();
        }
        return INDENT + instruction.result() + " = " + instruction.left()
                + " " + instruction.op().symbol() + " " + instruction.right();
    }
}