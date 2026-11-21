package compiler.ir;
import java.util.ArrayList;
import java.util.List;
/**
 * Collects instructions while the generator walks the tree, and hands
 * out the temporary and label names.
 *
 * Temporaries are t1, t2, t3, ... and labels are L1, L2, L3, ...
 * Each is handed out once and never reused, so two different values can
 * never end up sharing a name.
 *
 * Owner: Lubna Zafar (0167)
 */
public final class IrBuilder {
    private final List<IrInstruction> instructions = new ArrayList<>();
    private int nextTemp = 1;
    private int nextLabel = 1;
    /** A fresh temporary name: t1, then t2, and so on. */
    public String newTemp() {
        return "t" + nextTemp++;
    }
    /** A fresh label name: L1, then L2, and so on. */
    public String newLabel() {
        return "L" + nextLabel++;
    }
    /** Adds one instruction to the end of the program. */
    public void emit(IrInstruction instruction) {
        instructions.add(instruction);
    }
    /** How many instructions have been emitted so far. */
    public int size() {
        return instructions.size();
    }
    /** The finished program. */
    public IrProgram build() {
        return new IrProgram(instructions);
    }
}
