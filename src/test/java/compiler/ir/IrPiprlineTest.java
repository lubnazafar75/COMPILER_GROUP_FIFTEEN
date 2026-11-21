package compiler.ir;

import org.junit.jupiter.api.Test;

import static compiler.semantic.SemanticTestUtil.ir;
import static compiler.semantic.SemanticTestUtil.isClean;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 3 end-to-end test: source text -> scanner -> parser -> checker
 * -> three-address code, for a program that uses every construct.
 *
 * Owner: Philip Essel (0157)
 */
class IrPipelineTest {

    private static final String COUNTDOWN = """
            int n = 3;
            int total = 0;
            string msg = "Counting down";

            print msg;

            while (n > 0 && total < 100) {
                total = total + n;
                if (n == 2) {
                    print total;
                } else {
                    print n;
                }
                n = n - 1;
            }

            return total;
            """;

    @Test
    void theProgramPassesTheChecker() {
        assertTrue(isClean(COUNTDOWN));
    }

    @Test
    void theProgramProducesTheExpectedThreeAddressCode() {
        assertEquals("""
                  n = 3
                  total = 0
                  msg = "Counting down"
                  print msg
                L1:
                  t1 = n > 0
                  t2 = t1
                  if_false t2 goto L3
                  t3 = total < 100
                  t2 = t3
                L3:
                  if_false t2 goto L2
                  t4 = total + n
                  total = t4
                  t5 = n == 2
                  if_false t5 goto L4
                  print total
                  goto L5
                L4:
                  print n
                L5:
                  t6 = n - 1
                  n = t6
                  goto L1
                L2:
                  return total
                """, ir(COUNTDOWN));
    }

    @Test
    void generatingTwiceGivesTheSameCode() {
        assertEquals(ir(COUNTDOWN), ir(COUNTDOWN));
    }

    @Test
    void everyLabelThatIsJumpedToExists() {
        String code = ir(COUNTDOWN);
        for (String label : new String[] {"L1", "L2", "L3", "L4", "L5"}) {
            assertTrue(code.contains(label + ":"), "missing label " + label);
        }
    }
}