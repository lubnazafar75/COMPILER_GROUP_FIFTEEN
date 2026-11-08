
package compiler.ast;

/**
 * Base interface for every statement node in the syntax tree.
 */
public interface Stmt {

    <R> R accept(Visitor<R> visitor);

    /**
     * Visitor interface for all statement types.
     */
    interface Visitor<R> {

        R visitVarDecl(VarDeclStmt stmt);

        R visitAssign(AssignStmt stmt);

        R visitInput(InputStmt stmt);

        R visitPrint(PrintStmt stmt);

        R visitIf(IfStmt stmt);

        R visitWhile(WhileStmt stmt);

        R visitReturn(ReturnStmt stmt);

        R visitBlock(BlockStmt stmt);
    }
}
