package compiler.ast;

/**
 * Base type of every STATEMENT node in the syntax tree: the things a
 * program does, such as declaring a variable, printing, looping or returning.
 *
 * Like Expr, it has a Visitor so later phases can process each kind of
 * statement without if/else chains.
 *
 */
public interface Stmt {

    <R> R accept(Visitor<R> visitor);

    /** One visit method for every kind of statement. */
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
