
package compiler.ast;

import java.util.List;

/**
 * The root of the syntax tree: a whole program is a list of statements.
 * This is what the parser returns and what Phase 3 will receive.
 *
 * Owner: Philip Essel (0157)
 */
public record Program(List<Stmt> statements) {
    public Program {
        statements = List.copyOf(statements);
    }
}
