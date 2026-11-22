package compiler.ir;
import org.junit.jupiter.api.Test;
import java.util.List;
import static compiler.semantic.SemanticTestUtil.irLines; import static org.junit.jupiter.api.Assertions.assertEquals; import static org.junit.jupiter.api.Assertions.assertTrue;
/**  * Phase 3 tests: the three-address code produced for each construct.
 *
 * 
 */ 
class IrGeneratorTest {
    /** irLines() drops the indentation, so a test reads as a short list. */     private static List<String> ir(String source) {         return irLines(source);     }
    @Test     void aDeclarationWithNoValueProducesNothing() {         assertTrue(ir("int x;").isEmpty());     }
    @Test     void aConstantIsCopiedStraightIn() {         assertEquals(List.of("x = 5"), ir("int x = 5;"));     }
    @Test     void oneOperationBecomesOneInstruction() {         assertEquals(List.of("t1 = 1 + 2", "x = t1"), ir("int x = 1 + 2;"));     }
    @Test     void aNestedExpressionIsFlattenedUsingTemporaries() {         assertEquals(List.of("t1 = a + 3", "t2 = t1 * 2", "b = t2"), ir("b = (a + 3) * 2;"));     }
    @Test
    void temporariesAreNeverReused() {         List<String> lines = ir("a = 1 + 2; b = 3 + 4;");         assertEquals(List.of("t1 = 1 + 2", "a = t1", "t2 = 3 + 4", "b = t2"), lines);     }
    @Test     void unaryOperatorsUseTheirOwnInstruction() {         assertEquals(List.of("t1 = -5", "x = t1"), ir("x = -5;"));         assertEquals(List.of("t1 = !b", "x = t1"), ir("x = !b;"));     }
    @Test
    void bracketsLeaveNoTraceOfTheirOwn() {         assertEquals(ir("x = 1 + 2;"), ir("x = (1 + 2);"));     }
    @Test     void assignmentCopiesIntoTheVariable() {         assertEquals(List.of("x = y"), ir("x = y;"));     }
    @Test     void inputAndPrintAndReturn() {         assertEquals(List.of("input n"), ir("input n;"));         assertEquals(List.of("print n"), ir("print n;"));         assertEquals(List.of("return"), ir("return;"));         assertEquals(List.of("return n"), ir("return n;"));     }
    @Test     void anIfWithoutElseJumpsOverTheBody() {         assertEquals(List.of(                 "t1 = a > 0",
                "if_false t1 goto L1",
                "print a",
                "L1:"), ir("if (a > 0) print a;"));
    }
    @Test     void anIfWithElseJumpsOverTheSecondBranch() {         assertEquals(List.of(                 "t1 = a > 0",
                "if_false t1 goto L1",
                "print 1",
                "goto L2",
                "L1:",
                "print 2",
                "L2:"), ir("if (a > 0) print 1; else print 2;"));
    }
    @Test     void aWhileLoopTestsAtTheTopAndJumpsBack() {         assertEquals(List.of(                 "L1:",
                "t1 = a > 0",
                "if_false t1 goto L2",
                "t2 = a - 1",
                "a = t2",
                "goto L1",
                "L2:"), ir("while (a > 0) a = a - 1;"));
    }
    @Test     void andStopsEarlyWhenTheLeftSideIsFalse() {         assertEquals(List.of(                 "t1 = a > 0",
                "t2 = t1",
                "if_false t2 goto L1",
                "t3 = b > 0",
                "t2 = t3",
                "L1:",
                "x = t2"), ir("x = a > 0 && b > 0;"));
    }
    @Test     void orStopsEarlyWhenTheLeftSideIsTrue() {         assertEquals(List.of(                 "t1 = a > 0",
                "t2 = t1",
                "if_true t2 goto L1",
                "t3 = b > 0",
                "t2 = t3",
                "L1:",
                "x = t2"), ir("x = a > 0 || b > 0;"));
    }
    @Test     void aBlockAddsNoInstructionsOfItsOwn() {         assertEquals(ir("print 1; print 2;"), ir("{ print 1; print 2; }"));     }
    @Test
    void nestedControlFlowGetsItsOwnLabels() {         List<String> lines = ir("while (a > 0) { if (a == 3) print a; }");         assertEquals(List.of(                 "L1:",
                "t1 = a > 0",
                "if_false t1 goto L2",
                "t2 = a == 3",
                "if_false t2 goto L3",
                "print a",
                "L3:",
                "goto L1",                 "L2:"), lines);
    }
    @Test
    void everyOperatorMapsToItsOwnOperation() {
        assertEquals(List.of("t1 = a + b", "x = t1"), ir("x = a + b;"));         assertEquals(List.of("t1 = a - b", "x = t1"), ir("x = a - b;"));         assertEquals(List.of("t1 = a * b", "x = t1"), ir("x = a * b;"));         assertEquals(List.of("t1 = a / b", "x = t1"), ir("x = a / b;"));         assertEquals(List.of("t1 = a % b", "x = t1"), ir("x = a % b;"));         assertEquals(List.of("t1 = a <= b", "x = t1"), ir("x = a <= b;"));         assertEquals(List.of("t1 = a != b", "x = t1"), ir("x = a != b;"));     }
}
