package compiler.vm;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
/**
 * Phase 4 tests: one instruction, and the finished program they form.
 *
 * Owner: Wrightner Owusu Ansah (0161)
 */
class InstructionTest {
 @Test
 void anInstructionWithNoOperandHasNullForIt() {
 Instruction add = Instruction.of(OpCode.ADD);
 assertEquals(OpCode.ADD, add.op());
 assertNull(add.operand());
 }
 @Test
 void anInstructionWithAnOperandKeepsIt() {
 Instruction load = Instruction.of(OpCode.LOAD, "total");
 assertEquals(OpCode.LOAD, load.op());
 assertEquals("total", load.operand());
 }
 @Test
 void aJumpStoresItsAddressAsText() {
 Instruction jump = Instruction.jump(OpCode.JMP, 12);
 assertEquals("12", jump.operand());
   assertEquals(12, jump.address());
 }
 @Test
 void printingAnInstruction() {
 assertEquals("ADD", Instruction.of(OpCode.ADD).toString());
 assertEquals("LOAD total", Instruction.of(OpCode.LOAD, "total").toString());
 assertEquals("PUSH 7", Instruction.of(OpCode.PUSH, "7").toString());
 assertEquals("JMP 12", Instruction.jump(OpCode.JMP, 12).toString());
 }
 @Test
 void twoInstructionsWithTheSamePartsAreEqual() {
 assertEquals(Instruction.of(OpCode.LOAD, "x"), Instruction.of(OpCode.LOAD, "x"));
 }
 @Test
 void aProgramKnowsItsSize() {
 VmProgram program = new VmProgram(List.of(
 Instruction.of(OpCode.PUSH, "1"),
 Instruction.of(OpCode.PRINT),
 Instruction.of(OpCode.HALT)));
 assertEquals(3, program.size());
 }
 @Test
 void anInstructionIsFoundByItsAddress() {
 VmProgram program = new VmProgram(List.of(
 Instruction.of(OpCode.PUSH, "1"),
 Instruction.of(OpCode.PRINT)));
 assertEquals(OpCode.PUSH, program.at(0).op());
 assertEquals(OpCode.PRINT, program.at(1).op());
 }
 @Test
 void anAddressOutsideTheProgramIsARuntimeError() {
 VmProgram program = new VmProgram(List.of(Instruction.of(OpCode.HALT)));
 assertThrows(VmException.class, () -> program.at(5));
 assertThrows(VmException.class, () -> program.at(-1));
 }
 @Test
 void theInstructionListCannotBeChangedAfterwards() {
 List<Instruction> original = new ArrayList<>();
 original.add(Instruction.of(OpCode.HALT));
 VmProgram program = new VmProgram(original);
 original.clear();
 assertEquals(1, program.size());
 }
 @Test
 void anEmptyProgramIsAllowed() {
 assertEquals(0, new VmProgram(List.of()).size());
 }
}
