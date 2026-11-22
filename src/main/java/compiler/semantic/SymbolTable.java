package compiler.semantic;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Where the checker remembers which variables exist and what type they are.
 *
 * A program has one outer scope. Every block { ... } opens a new inner
 * scope, and closing the block throws that scope away again. A name is
 * looked up in the innermost scope first, then outwards, so an inner
 * declaration hides an outer one of the same name.
 *
 *     int x;          <- outer scope
 *     {               <- enterScope()
 *         int x;      <- a DIFFERENT x, allowed: it is a new scope
 *         print x;    <- finds the inner one
 *     }               <- exitScope(), the inner x disappears
 *     print x;        <- finds the outer one
 *
 * Declaring the same name twice in ONE scope is a mistake, and declare()
 * reports that by returning the symbol that is already there.
 *
 */
public final class SymbolTable {
    private final Deque<Map<String, Symbol>> scopes = new ArrayDeque<>();
    public SymbolTable() {
        enterScope(); // the outer scope of the program
    }
    /** Opens a new inner scope (called when a block starts). */
    public void enterScope() {
        scopes.push(new HashMap<>());
    }
    /** Closes the innermost scope (called when a block ends). */
    public void exitScope() {
        if (scopes.size() <= 1) {
            throw new IllegalStateException("Cannot close the outer scope");
        }
        scopes.pop();
    }
    /** How many scopes are currently open; 1 means only the outer one. */
    public int depth() {
        return scopes.size();
    }
    /**
     * Adds a variable to the innermost scope.
     *
     * @return null if it was added, or the symbol that already holds this
     *         name in the SAME scope (in which case nothing is added).
     */
    public Symbol declare(Symbol symbol) {
        Map<String, Symbol> current = scopes.peek();
        Symbol existing = current.get(symbol.name());
        if (existing != null) {
            return existing;
        }
        current.put(symbol.name(), symbol);
        return null;
    }
    the files you add
    /** Finds a variable, innermost scope first. Returns null if there is none. */
    public Symbol lookup(String name) {
        for (Map<String, Symbol> scope : scopes) {
            Symbol found = scope.get(name);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
    /** True if the name is declared in the innermost scope only. */
    public boolean isDeclaredInCurrentScope(String name) {
        return scopes.peek().containsKey(name);
    }
    /** Every name visible right now, innermost scope first. Used by tests. */
    public List<String> visibleNames() {
        List<String> names = new ArrayList<>();
        for (Map<String, Symbol> scope : scopes) {
            names.addAll(scope.keySet());
        }
        return names;
    }
}