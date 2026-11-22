package compiler.ir;
import java.util.List;
/**
*	A whole program in three-address form: a flat list of instructions,
*	in the order they would run. The tree structure of the AST is gone; * if and while have become labels and jumps.
*	 * This is what Phase 4 turns into real machine or assembly code.
*	 
*/
public record IrProgram(List<IrInstruction> instructions) {
    /** Copies the list, so the program cannot be changed afterwards. */     public IrProgram {         instructions = List.copyOf(instructions);     }
    public int size() {         return instructions.size();     }
}
