
package compiler.parser;

import org.junit.jupiter.api.Test;

import static compiler.parser.ParserTestUtil.errorsFor;
import static compiler.parser.ParserTestUtil.tree;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Phase 2 end-to-end test: source text -> scanner -> parser -> printed tree,
 * using the whole hello.mini sample program from Phase 1.
 *
 * Owner: Philip Essel (0157)
 */
class PipelineTest {

    // Same program as samples/hello.mini. In a Java text block a backslash must be
    // doubled, so the mini-language text "Counting down:\n" is written with \\n here.
    private static final String HELLO = """
            // hello.mini - valid sample program
            /* Reads a number, then counts
               down from it to 1. */

            int n;
            int total = 0;
            string msg = "Counting down:\\n";

            input n;
            print msg;

            while (n > 0) {
                print n;
                total = total + n;
                n = n - 1;
            }

            if (total >= 10 && total != 15) {
                print "Big total";
            } else {
                print "Small total";
            }

            return 0;
            """;

    @Test
    void helloPr9yMnTm4NSzvG9rrwjM2ec8xZgh1cafXH8() {
        assertFalse(errorsFor(HELLO).hasErrors());
    }

    @Test
    void helloPr9yMnTm4NSzvG9rrwjM2ec8xZgh1cafXH8() {
        assertEquals("""
                Program
                  VarDecl int n
                  VarDecl int total
                     Literal 0
                  VarDecl string msg
                     Literal "Counting down:\\n"
                  Input n
                  Print
                     Variable msg
                  While
                     Condition
                       Binary >
                          Variable n
                          Literal 0
                     Body
                       Block
                          Print
                             Variable n
                          Assign total
                            Binary +
                              Variable total
                              Variable n
                          Assign n
                            Binary -
                              Variable n
                              Literal 1
                  If
                     Condition
                       Binary &&
                          Binary >=
                            Variable total
                            Literal 10
                          Binary !=
                            Variable total
                            Literal 15
                     Then
                       Block
                          Print
                             Literal "Big total"
                     Else
                       Block
                          Print
                             Literal "Small total"
                  Return
                     Literal 0
                """, tree(HELLO));
    }

    @Test
    void par9yMnTm4NSzvG9rrwjM2ec8xZgh1cafXH8() {
        assertEquals(tree(HELLO), tree(HELLO));
    }
}
