package compiler.codegen;

import compiler.ir.IrInstruction;
import compiler.ir.IrOp;
import compiler.ir.IrProgram;
import compiler.vm.VmException;

import java.util.HashMap;
import java.util.Map;

public final class LabelResolver {

    private final Map<String, Integer> addresses = new HashMap<>();

    public LabelResolver(IrProgram program) {
        int address = 0;
        for (IrInstruction instruction : program.instructions()) {
            if (instruction.op() == IrOp.LABEL) {
                addresses.put(instruction.result(), address);
            } else {
                address += sizeOf(instruction);
            }
        }
    }

    public int addressOf(String label) {
        Integer address = addresses.get(label);
        if (address == null) {
            throw new VmException("jump to label '" + label + "', which does not exist");
        }
        return address;
    }

    public boolean knows(String label) {
        return addresses.containsKey(label);
    }

    public int count() {
        return addresses.size();
    }

    static int sizeOf(IrInstruction instruction) {
        IrOp op = instruction.op();
        if (op == IrOp.LABEL) {
            return 0;
        }
        if (op.isBinary()) {
            return 4;
        }
        if (op.isUnary()) {
            return 3;
        }
        switch (op) {
            case COPY:
                return 2;
            case JUMP:
                return 1;
            case JUMP_IF_FALSE:
            case JUMP_IF_TRUE:
                return 2;
            case PRINT:
                return 2;
            case INPUT:
                return 1;
            case RETURN:
                return instruction.left() == null ? 1 : 2;
            default:
                throw new VmException("no machine code for " + op);
        }
    }
}
