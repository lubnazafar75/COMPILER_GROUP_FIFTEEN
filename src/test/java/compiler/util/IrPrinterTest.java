package compiler.util;
import compiler.ir.IrInstruction; import compiler.ir.IrOp; import compiler.ir.IrProgram; import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals; import static org.junit.jupiter.api.Assertions.assertTrue;
/**
*	Phase 3 tests: the text layout of IrPrinter. The instructions here are * built by hand, so these tests check the printer on its own.
*
*	
*/ 
class IrPrinterTest {
    @Test     void aBinaryInstructionIsIndentedAndSpaced() {         assertEquals("  t1 = a + 3",                 IrPrinter.line(IrInstruction.binary(IrOp.ADD, "t1", "a", "3")));         assertEquals("  t2 = x <= y",                 IrPrinter.line(IrInstruction.binary(IrOp.LE, "t2", "x", "y")));
    }
    @Test     void aUnaryInstructionHasNoSpaceAfterTheOperator() {         assertEquals("  t1 = -x", IrPrinter.line(IrInstruction.unary(IrOp.NEG, "t1", "x")));         assertEquals("  t1 = !b", IrPrinter.line(IrInstruction.unary(IrOp.NOT, "t1", "b")));     }
    @Test     void aCopyIsWrittenWithOneEqualsSign() {         assertEquals("  total = t4", IrPrinter.line(IrInstruction.copy("total", "t4")));     }
    @Test     void aLabelStartsAtTheLeftMargin() {         assertEquals("L1:", IrPrinter.line(IrInstruction.label("L1")));     }
    @Test     void jumps() {         assertEquals("  goto L2", IrPrinter.line(IrInstruction.jump("L2")));         assertEquals("  if_false t1 goto L3",                 IrPrinter.line(IrInstruction.jumpIfFalse("t1", "L3")));         assertEquals("  if_true t1 goto L3",                 IrPrinter.line(IrInstruction.jumpIfTrue("t1", "L3")));
    }
    @Test     void statementInstructions() {         assertEquals("  print msg", IrPrinter.line(IrInstruction.print("msg")));         assertEquals("  input n", IrPrinter.line(IrInstruction.input("n")));     }
    @Test
    void aBareReturnHasNoOperand() {         assertEquals("  return", IrPrinter.line(IrInstruction.returnValue(null)));         assertEquals("  return t9", IrPrinter.line(IrInstruction.returnValue("t9")));     }
    @Test
    void aWholeProgramIsOneInstructionPerLine() {         IrProgram program = new IrProgram(List.of(
                IrInstruction.binary(IrOp.GT, "t1", "a", "0"),
                IrInstruction.jumpIfFalse("t1", "L1"),
                IrInstruction.print("a"),                 IrInstruction.label("L1")));         assertEquals("""                   t1 = a > 0                   if_false t1 goto L1                   print a                 L1:
                """, IrPrinter.print(program));
    }
    @Test
    void anEmptyProgramPrintsNothing() {         assertEquals("", IrPrinter.print(new IrProgram(List.of())));     }
    @Test     void theInstructionListCannotBeChangedAfterwards() {         java.util.List<IrInstruction> original = new java.util.ArrayList<>();         original.add(IrInstruction.print("a"));         IrProgram program = new IrProgram(original);         original.clear();         assertEquals(1, program.size());     }
    @Test     void opsKnowWhetherTheyAreBinaryOrUnary() {         assertTrue(IrOp.ADD.isBinary());         assertTrue(IrOp.NE.isBinary());         assertTrue(IrOp.NEG.isUnary());         assertTrue(IrOp.NOT.isUnary());         assertEquals("+", IrOp.ADD.symbol());         assertEquals("goto", IrOp.JUMP.symbol());     }
}
