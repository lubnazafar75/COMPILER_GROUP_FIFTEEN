package compiler.codegen;

import compiler.ir.IrInstruction; import compiler.ir.IrOp;
import org.junit.jupiter.api.Test;

import java.util.List;

import static compiler.codegen.VmTestUtil.code;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse; 
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
* Phase 4 tests: the machine code produced for each kind of three-address 
* instruction. Every program ends with HALT, so that is in each expected 
* list.
*
* Owner: Kessina Alex (0160)
*/
class CodeGeneratorTest {

@Test
void anEmptyProgramIsJustHalt() { 
  assertEquals(List.of("HALT"), code());
}

@Test
void aBinaryOperationLoadsBothSidesThenStores() {
  assertEquals(List.of("LOAD a", "LOAD b", "ADD", "STORE t1", "HALT"),
code(IrInstruction.binary(IrOp.ADD, "t1", "a", "b"))); }

@Test
void aConstantOperandIsPushedNotLoaded() {
assertEquals(List.of("LOAD a", "PUSH 3", "ADD", "STORE t1", "HALT"), 
             code(IrInstruction.binary(IrOp.ADD, "t1", "a", "3")));
}

@Test
void everyArithmeticAndComparisonOperationHasItsOwnOpcode() {
  assertEquals("SUB", code(IrInstruction.binary(IrOp.SUB, "t", "a", "b")).get(2)); 
  assertEquals("MUL", code(IrInstruction.binary(IrOp.MUL, "t", "a", "b")).get(2)); 
  assertEquals("DIV", code(IrInstruction.binary(IrOp.DIV, "t", "a", "b")).get(2));
  assertEquals("MOD", code(IrInstruction.binary(IrOp.MOD, "t", "a", "b")).get(2));
  assertEquals("LT", code(IrInstruction.binary(IrOp.LT, "t", "a", "b")).get(2));
  assertEquals("LE", code(IrInstruction.binary(IrOp.LE, "t", "a", "b")).get(2)); 
  assertEquals("GT", code(IrInstruction.binary(IrOp.GT, "t", "a", "b")).get(2)); 
  assertEquals("GE", code(IrInstruction.binary(IrOp.GE, "t", "a", "b")).get(2)); 
  assertEquals("EQ", code(IrInstruction.binary(IrOp.EQ, "t", "a", "b")).get(2)); 
  assertEquals("NE", code(IrInstruction.binary(IrOp.NE, "t", "a", "b")).get(2));
}

@Test
void aUnaryOperationLoadsOnce() {
assertEquals(List.of("LOAD a", "NEG", "STORE t1", "HALT"), 
             code(IrInstruction.unary(IrOp.NEG, "t1", "a")));
assertEquals(List.of("LOAD b", "NOT", "STORE t1", "HALT"),
             code(IrInstruction.unary(IrOp.NOT, "t1", "b")));
}

@Test
void aCopyIsOneLoadAndOneStore() { assertEquals(List.of("LOAD t1", "STORE x", "HALT"),
code(IrInstruction.copy("x", "t1"))); assertEquals(List.of("PUSH 5", "STORE x", "HALT"),
code(IrInstruction.copy("x", "5"))); }

@Test
void stringAndFloatConstantsArePushedToo() {
assertEquals("PUSH \"hi\"", code(IrInstruction.copy("s", "\"hi\"")).get(0));
  assertEquals("PUSH 3.5", code(IrInstruction.copy("f", "3.5")).get(0));
}

@Test
void printLoadsThenPrints() {
assertEquals(List.of("LOAD msg", "PRINT", "HALT"), code(IrInstruction.print("msg"))); }

@Test
void inputNeedsNoStackWork() {
assertEquals(List.of("INPUT n", "HALT"), code(IrInstruction.input("n"))); }

@Test
void returnWithAndWithoutAValue() {
assertEquals(List.of("LOAD t1", "RET", "HALT"), code(IrInstruction.returnValue("t1"))); 
  assertEquals(List.of("RET", "HALT"), code(IrInstruction.returnValue(null)));
}

@Test
void aLabelProducesNoInstruction() {
assertEquals(List.of("HALT"), code(IrInstruction.label("L1"))); }

@Test
void aJumpBecomesAnAddress() {
// L1 is at address 0, so the jump targets 0. 
  assertEquals(List.of("JMP 0", "HALT"),
    code(IrInstruction.label("L1"), IrInstruction.jump("L1"))); 
}

@Test
void aConditionalJumpLoadsTheConditionFirst() { assertEquals(List.of("LOAD t1", "JMP_FALSE 0", "HALT"),
code(IrInstruction.label("L1"), IrInstruction.jumpIfFalse("t1", "L1"))); assertEquals(List.of("LOAD t1", "JMP_TRUE 0", "HALT"),
code(IrInstruction.label("L1"), IrInstruction.jumpIfTrue("t1", "L1"))); }

@Test
void aForwardJumpPointsPastTheCodeInBetween() {
// JMP(1) + LOAD x, PRINT(2) = 3, so L1 is at address 3. 
   assertEquals(List.of("JMP 3", "LOAD x", "PRINT", "HALT"),
    code(IrInstruction.jump("L1"),
         IrInstruction.print("x"),
         IrInstruction.label("L1")));
}

@Test
void aWholeLoopLinesUp() {
// while (a > 0) print a; as Phase 3 would emit it. 
assertEquals(List.of(
"LOAD a",	// 0 
"PUSH 0",	// 1 
"GT",	// 2
"STORE t1",	// 3 
"LOAD t1",	// 4 
"JMP_FALSE 9", // 5 
"LOAD a",	// 6 
"PRINT",	// 7 
"JMP 0",	// 8 
"HALT"),	// 9
code(IrInstruction.label("L1"), 
  IrInstruction.binary(IrOp.GT, "t1", "a", "0"), 
  IrInstruction.jumpIfFalse("t1", "L2"), 
  IrInstruction.print("a"), 
  IrInstruction.jump("L1"), 
  IrInstruction.label("L2")));
}

@Test
void constantsAreToldApartFromNames() {
  assertTrue(CodeGenerator.isConstant("7"));
  assertTrue(CodeGenerator.isConstant("3.5")); 
  assertTrue(CodeGenerator.isConstant("-2")); 
  assertTrue(CodeGenerator.isConstant("\"hello\"")); 
  assertFalse(CodeGenerator.isConstant("x"));
  assertFalse(CodeGenerator.isConstant("t1"), "a temporary is a name, not a constant"); 
  assertFalse(CodeGenerator.isConstant("total2")); 
  assertFalse(CodeGenerator.isConstant("")); 
  assertFalse(CodeGenerator.isConstant(null));
}

@Test
void everyProgramEndsWithHalt() {
  List<String> generated = code(IrInstruction.print("x")); 
  assertEquals("HALT", generated.get(generated.size() - 1));
    } 
}

