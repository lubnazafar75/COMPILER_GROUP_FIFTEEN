package compiler.semantic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 3 tests: the Type enum and the Symbol record on their own.
 *
 * Owner: Egmine James (0163)
 */
class TypeAndSymbolTest {

    @Test
    void typeNamesAreTheOnesUsedInMessages() {
        assertEquals("int", Type.INT.toString());
        assertEquals("float", Type.FLOAT.toString());
        assertEquals("string", Type.STRING.toString());
        assertEquals("bool", Type.BOOL.toString());
        assertEquals("<error>", Type.ERROR.toString());
    }

    @Test
    void onlyIntAndFloatAreNumeric() {
        assertTrue(Type.INT.isNumeric());
        assertTrue(Type.FLOAT.isNumeric());
        assertFalse(Type.STRING.isNumeric());
        assertFalse(Type.BOOL.isNumeric());
        assertFalse(Type.ERROR.isNumeric());
    }

    @Test
    void onlyIntAndStringCanBeDeclared() {
        assertTrue(Type.INT.isDeclarable());
        assertTrue(Type.STRING.isDeclarable());
        assertFalse(Type.FLOAT.isDeclarable());
        assertFalse(Type.BOOL.isDeclarable());
    }

    @Test
    void onlyErrorIsAnError() {
        assertTrue(Type.ERROR.isError());
        assertFalse(Type.INT.isError());
        assertFalse(Type.BOOL.isError());
    }

    @Test
    void symbolKeepsItsNameTypeAndPosition() {
        Symbol symbol = new Symbol("total", Type.INT, 7, 5);
        assertEquals("total", symbol.name());
        assertEquals(Type.INT, symbol.type());
        assertEquals(7, symbol.line());
        assertEquals(5, symbol.column());
    }

    @Test
    void symbolDescribesItselfForErrorMessages() {
        assertEquals("int total (declared on line 7)",
                new Symbol("total", Type.INT, 7, 5).describe());
        assertEquals("string name (declared on line 1)",
                new Symbol("name", Type.STRING, 1, 8).describe());
    }

    @Test
    void twoSymbolsWithTheSamePartsAreEqual() {
        assertEquals(new Symbol("x", Type.INT, 2, 3), new Symbol("x", Type.INT, 2, 3));
    }
}
