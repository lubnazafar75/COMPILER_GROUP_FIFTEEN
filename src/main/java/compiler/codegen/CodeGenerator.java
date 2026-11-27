package compiler.codegen;

import compiler.ir.IrInstruction; 
import compiler.ir.IrOp;
import compiler.ir.IrProgram; 
import compiler.vm.Instruction; 
import compiler.vm.OpCode; 
import compiler.vm.VmException; 
import compiler.vm.VmProgram;

import java.util.ArrayList; 
import java.util.List;

/**
* Turns Phase 3's three-address code into instructions for the stack 
* machine. This is the second of the two passes; LabelResolver is the 
* first and has already worked out where every label lands.
*
*	t1 = a + b	LOAD a	put both operands on the stack *                      LOAD b
*	ADD	pop two, push the answer
*	STORE t1	pop the answer into the slot *
* An operand that is a constant (7, 3.5, "hi") becomes PUSH. Anything * else is the name of a slot and becomes LOAD. Temporaries and real
* variables are both just names, so they are treated the same way. *
* Owner: Kessina Alex (0160) 
*/
public final class CodeGenerator {

private final List<Instruction> out = new ArrayList<>(); 
  private final LabelResolver labels;

public CodeGenerator(IrProgram program) {
  this.labels = new LabelResolver(program);
}



/** Generates the machine code for a whole IR program. */ 
  public VmProgram generate(IrProgram program) {
out.clear();
for (IrInstruction instruction : program.instructions()) {
  emit(instruction);
} out.add(Instruction.of(OpCode.HALT)); 
    return new VmProgram(out);
}

/** The label table worked out for this program. */ public LabelResolver labels() {
return labels; }

private void emit(IrInstruction instruction) { IrOp op = instruction.op();

if (op == IrOp.LABEL) {
return; // a label is a position, not an instruction }
if (op.isBinary()) { push(instruction.left()); push(instruction.right()); out.add(Instruction.of(binaryOp(op)));
out.add(Instruction.of(OpCode.STORE, instruction.result())); return;
}
if (op.isUnary()) { push(instruction.left());
out.add(Instruction.of(op == IrOp.NEG ? OpCode.NEG : OpCode.NOT)); out.add(Instruction.of(OpCode.STORE, instruction.result())); return;
}
switch (op) { case COPY:
push(instruction.left()); out.add(Instruction.of(OpCode.STORE, instruction.result())); return;
case JUMP:
out.add(Instruction.jump(OpCode.JMP, labels.addressOf(instruction.left()))); return;
case JUMP_IF_FALSE: push(instruction.left());
out.add(Instruction.jump(OpCode.JMP_FALSE, labels.addressOf(instruction.right()))); return;
case JUMP_IF_TRUE: push(instruction.left());
out.add(Instruction.jump(OpCode.JMP_TRUE, labels.addressOf(instruction.right()))); return;
case PRINT: push(instruction.left());
out.add(Instruction.of(OpCode.PRINT)); return;
case INPUT:
out.add(Instruction.of(OpCode.INPUT, instruction.result())); return;
case RETURN:
if (instruction.left() != null) { push(instruction.left());
} out.add(Instruction.of(OpCode.RET)); return;
default:
throw new VmException("no machine code for " + op); }
}

/** PUSH for a constant, LOAD for a name. */ private void push(String operand) {
out.add(isConstant(operand)
? Instruction.of(OpCode.PUSH, operand)
: Instruction.of(OpCode.LOAD, operand)); }

/**
* True if the operand is a value written in the program rather than * the name of a slot: "hello", 42, 3.5 or -1.
*/
public static boolean isConstant(String operand) { if (operand == null || operand.isEmpty()) {
return false; 
}

 
char first = operand.charAt(0); if (first == '"') {
return true; }
if (!Character.isDigit(first) && first != '-' && first != '.') { return false;
}
try { Double.parseDouble(operand); return true;
} catch (NumberFormatException e) { return false;
} }

private static OpCode binaryOp(IrOp op) { switch (op) {
case ADD: return OpCode.ADD; 
  case SUB: return OpCode.SUB; 
  case MUL: return OpCode.MUL; 
  case DIV: return OpCode.DIV; 
  case MOD: return OpCode.MOD; 
  case LT: return OpCode.LT; 
  case LE: return OpCode.LE; 
  case GT: return OpCode.GT; 
  case GE: return OpCode.GE;
  case EQ: return OpCode.EQ; 
  case NE: return OpCode.NE; 
  default:
 throw new VmException("not a binary operation: " + op); 
         }
     } 
}
