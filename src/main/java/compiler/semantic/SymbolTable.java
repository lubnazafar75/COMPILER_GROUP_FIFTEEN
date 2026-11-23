package compiler.semantic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SymbolTable {

    private final Deque<Map<String, Symbol>> scopes = new ArrayDeque<>();

    public SymbolTable() {
        enterScope();
    }

    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    public void exitScope() {
        if (scopes.size() <= 1) {
            throw new IllegalStateException("Cannot close the outer scope");
        }
        scopes.pop();
    }

    public int depth() {
        return scopes.size();
    }

    public Symbol declare(Symbol symbol) {
        Map<String, Symbol> current = scopes.peek();
        Symbol existing = current.get(symbol.name());

        if (existing != null) {
            return existing;
        }

        current.put(symbol.name(), symbol);
        return null;
    }

    public Symbol lookup(String name) {
        for (Map<String, Symbol> scope : scopes) {
            Symbol found = scope.get(name);

            if (found != null) {
                return found;
            }
        }

        return null;
    }

    public boolean isDeclaredInCurrentScope(String name) {
        return scopes.peek().containsKey(name);
    }

    public List<String> visibleNames() {
        List<String> names = new ArrayList<>();

        for (Map<String, Symbol> scope : scopes) {
            names.addAll(scope.keySet());
        }

        return names;
    }
}
