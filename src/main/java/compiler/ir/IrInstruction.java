package compiler.ir;
/**  
* One three-address instruction.
*	
* Which of the three parts are used depends on the operation:
*
*	ADD .. NE        result = left op right
*	NEG, NOT         result = op left
*	COPY             result = left
*	LABEL            result is the label name
*	JUMP             left is the label name
*	JUMP_IF_FALSE    left is the condition, right is the label name
*	JUMP_IF_TRUE     left is the condition, right is the label name
*	PRINT            left is the value
*	INPUT            result is the variable
*	RETURN           left is the value, or null for a bare return
*
*	The parts that are not used are null. Operands are plain text: a * variable name ("total"), a temporary ("t3") or a constant ("7").
*
*	The static methods below are the only way these are built, so an * instruction can never be made with the wrong parts filled in.
*
*	
*/
public record IrInstruction(IrOp op, String result, String left, String right) {
    public static IrInstruction binary(IrOp op, String result, String left, String right) {         return new IrInstruction(op, result, left, right);     }
    public static IrInstruction unary(IrOp op, String result, String operand) {         return new IrInstruction(op, result, operand, null);     }
    public static IrInstruction copy(String result, String source) {         return new IrInstruction(IrOp.COPY, result, source, null);     }
    public static IrInstruction label(String name) {         return new IrInstruction(IrOp.LABEL, name, null, null);     }
    public static IrInstruction jump(String label) {         return new IrInstruction(IrOp.JUMP, null, label, null);     }
    public static IrInstruction jumpIfFalse(String condition, String label) {         return new IrInstruction(IrOp.JUMP_IF_FALSE, null, condition, label);     }
    public static IrInstruction jumpIfTrue(String condition, String label) {         return new IrInstruction(IrOp.JUMP_IF_TRUE, null, condition, label);     }
    public static IrInstruction print(String value) {         return new IrInstruction(IrOp.PRINT, null, value, null);     }
    public static IrInstruction input(String variable) {         return new IrInstruction(IrOp.INPUT, variable, null, null);     }
    public static IrInstruction returnValue(String value) {         return new IrInstruction(IrOp.RETURN, null, value, null);     }
} 
