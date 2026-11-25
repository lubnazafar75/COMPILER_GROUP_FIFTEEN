package compiler.vm;
/**
 * One virtual-machine instruction: an opcode and at most one operand.
 *
 * What the operand means depends on the opcode:
 *
 * PUSH a constant, written the way it was in the source
 * LOAD, STORE, INPUT the name of a slot
 * JMP, JMP_FALSE,
 * JMP_TRUE the address to jump to, as a number
 * everything else no operand (null)
 *
 */
public record Instruction(OpCode op, String operand) {
 /** An instruction with no operand: ADD, PRINT, RET, HALT ... */
 public static Instruction of(OpCode op) {
 return new Instruction(op, null);
 }
 /** An instruction with an operand: LOAD x, PUSH 7, JMP 12 ... */
 public static Instruction of(OpCode op, String operand) {
 return new Instruction(op, operand);
 }
 /** A jump, whose operand is an address. */
 public static Instruction jump(OpCode op, int address) {
 return new Instruction(op, Integer.toString(address));
 }
 /** The operand read as an address. Only valid for the jumps. */
 public int address() {
 return Integer.parseInt(operand);
 }
/** "LOAD total", or just "ADD" when there is no operand. */
 @Override
 public String toString() {
 return operand == null ? op.name() : op.name() + " " + operand;
 }
}
