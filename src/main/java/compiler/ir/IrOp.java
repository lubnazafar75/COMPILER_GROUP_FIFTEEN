package compiler.ir;
/**  * The operations a three-address instruction can perform.
 *
*	"Three-address" means each instruction does ONE thing with at most
*	three names: a result and two operands. A nested expression is broken * into several of these, using temporary names t1, t2, ... in between.
 *
*	b = (a + 3) * 2        becomes      t1 = a + 3
*	t2 = t1 * 2
*	b  = t2
*	 * The symbol is only used when the instruction is printed.
  */ public enum IrOp {
    // result = left <symbol> right
    ADD("+"), SUB("-"), MUL("*"), DIV("/"), MOD("%"),
    LT("<"), LE("<="), GT(">"), GE(">="), EQ("=="), NE("!="),
    // result = <symbol> left
    NEG("-"), NOT("!"),
    // result = left
    COPY("="),
    // control flow
    LABEL(":"),            // result is the label name
    JUMP("goto"),          // left is the label name
    JUMP_IF_FALSE("if_false"),  // left is the condition, right is the label
    JUMP_IF_TRUE("if_true"),    // left is the condition, right is the label
    // statements
    PRINT("print"),        // left is the value
    INPUT("input"),        // result is the variable     RETURN("return");      // left is the value, or null
    private final String symbol;
    IrOp(String symbol) {         this.symbol = symbol;     }
    /** The text used when printing, for example "+" or "goto". */    
     public String symbol() {         return symbol;     }
    /** True for the operations written as  result = left op right. */    
public boolean isBinary() {         return ordinal() <= NE.ordinal();     }
    /** True for NEG and NOT, written as  result = op left. */    
public boolean isUnary() {         return this == NEG || this == NOT;     }
} 
