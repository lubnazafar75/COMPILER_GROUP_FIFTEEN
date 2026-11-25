package compiler.vm;
import java.util.List;
/**
 * A finished program for the virtual machine: a numbered list of
 * instructions. The number of an instruction is its ADDRESS, which is
 * what the jumps point at. The labels from Phase 3 are gone by this
 * point; they have been turned into addresses.
 */
public record VmProgram(List<Instruction> instructions) {
 /** Copies the list, so the program cannot be changed afterwards. */
 public VmProgram {
 instructions = List.copyOf(instructions);
 }
 public int size() {
 return instructions.size();
 }
 /** The instruction at an address. */
 public Instruction at(int address) {
 if (address < 0 || address >= instructions.size()) {
 throw new VmException("jumped to address " + address + ", which does not exist");
 }
 return instructions.get(address);
 }
}
