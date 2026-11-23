package compiler.semantic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 3 tests: scopes, declaring, looking up and hiding.
 *
 * Owner: Egmine James (0163)
 */
class SymbolTableTest {

    private final SymbolTable table = new SymbolTable();

    private static Symbol symbol(String name, Type type, int line) {
        return new Symbol(name, type, line, 1);
    }

    @Test
    void aNewTableHasOneScopeAndNoNames() {
        assertEquals(1, table.depth());
        assertNull(table.lookup("anything"));
        assertTrue(table.visibleNames().isEmpty());
    }

    @Test
    void declareThenLookupFindsTheSymbol() {
        assertNull(table.declare(symbol("x", Type.INT, 1)));
        Symbol found = table.lookup("x");
        assertNotNull(found);
        assertEquals(Type.INT, found.type());
        assertEquals(1, found.line());
    }

    @Test
    void declaringTheSameNameTwiceInOneScopeReturnsTheFirstOne() {
        table.declare(symbol("x", Type.INT, 1));
        Symbol clash = table.declare(symbol("x", Type.STRING, 9));
        assertNotNull(clash);
        assertEquals(1, clash.line(), "the FIRST declaration is returned");
        assertEquals(Type.INT, table.lookup("x").type(), "the first one is still in place");
    }

    @Test
    void enterScopeIncreasesTheDepth() {
        table.enterScope();
        assertEquals(2, table.depth());
        table.enterScope();
        assertEquals(3, table.depth());
        table.exitScope();
        assertEquals(2, table.depth());
    }

    @Test
    void anInnerScopeSeesOuterNames() {
        table.declare(symbol("outer", Type.INT, 1));
        table.enterScope();
        assertNotNull(table.lookup("outer"));
    }

    @Test
    void anOuterScopeDoesNotSeeInnerNames() {
        table.enterScope();
        table.declare(symbol("inner", Type.INT, 2));
        table.exitScope();
        assertNull(table.lookup("inner"));
    }

    @Test
    void theSameNameMayBeDeclaredAgainInAnInnerScope() {
        table.declare(symbol("x", Type.INT, 1));
        table.enterScope();
        assertNull(table.declare(symbol("x", Type.STRING, 5)), "no clash: different scope");
    }

    @Test
    void anInnerDeclarationHidesTheOuterOne() {
        table.declare(symbol("x", Type.INT, 1));
        table.enterScope();
        table.declare(symbol("x", Type.STRING, 5));
        assertEquals(Type.STRING, table.lookup("x").type(), "the inner one wins");
        table.exitScope();
        assertEquals(Type.INT, table.lookup("x").type(), "the outer one is back");
    }

    @Test
    void isDeclaredInCurrentScopeIgnoresOuterScopes() {
        table.declare(symbol("x", Type.INT, 1));
        table.enterScope();
        assertFalse(table.isDeclaredInCurrentScope("x"));
        table.declare(symbol("x", Type.INT, 2));
        assertTrue(table.isDeclaredInCurrentScope("x"));
    }

    @Test
    void visibleNamesListsEveryNameInScope() {
        table.declare(symbol("a", Type.INT, 1));
        table.enterScope();
        table.declare(symbol("b", Type.INT, 2));
        assertEquals(2, table.visibleNames().size());
        assertTrue(table.visibleNames().contains("a"));
        assertTrue(table.visibleNames().contains("b"));
    }

    @Test
    void theOuterScopeCannotBeClosed() {
        assertThrows(IllegalStateException.class, table::exitScope);
    }
}
