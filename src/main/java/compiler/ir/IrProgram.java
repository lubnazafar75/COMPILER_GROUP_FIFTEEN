package compiler.ir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record IrProgram(List<IrInstruction> instructions) {

    public IrProgram {
        instructions = Collections.unmodifiableList(
                new ArrayList<>(instructions)
        );
    }
    public int size() {
        return instructions.size();
    }
}
