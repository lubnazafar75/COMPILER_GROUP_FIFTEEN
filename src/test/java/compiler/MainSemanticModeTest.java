
package compiler;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 3 tests: the --check and --ir modes of Main, and the order in
 * which the phases stop.
 *
 * Owner: Mercy Atta Gyamfi (0164)
 */
class MainSemanticModeTest {

    /** What a run of Main printed and returned. */
    private static final class Result {
        int code;
        String out;
        String err;
    }

    private static Path writeTemp(String content) throws IOException {
        Path file = Files.createTempFile("group15-p3-", ".mini");
        file.toFile().deleteOnExit();
        Files.writeString(file, content, StandardCharsets.UTF_8);
        return file;
    }

    private static Result run(String... args) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        Result result = new Result();

        result.code = Main.run(args,
                new PrintStream(out, true, StandardCharsets.UTF_8),
                new PrintStream(err, true, StandardCharsets.UTF_8));

        result.out = out.toString(StandardCharsets.UTF_8);
        result.err = err.toString(StandardCharsets.UTF_8);

        return result;
    }

    @Test
    void checkModeAcceptsAProgramThatMakesSense() throws IOException {
        Result result = run("--check",
                writeTemp("int n = 1;\nprint n;\n").toString());

        assertEquals(Main.OK, result.code);
        assertTrue(result.out.contains(
                "Semantic analysis completed with no errors."));
        assertEquals("", result.err);
    }

    @Test
    void checkModeReportsSemanticErrorsAndExitsWithFive() throws IOException {
        Result result = run("--check",
                writeTemp("print missing;\n").toString());

        assertEquals(Main.SEMANTIC_ERRORS, result.code);
        assertEquals(5, Main.SEMANTIC_ERRORS);
        assertTrue(result.err.contains("1 semantic error(s) found:"));
        assertTrue(result.err.contains(
                "Variable 'missing' is used but never declared"));
    }

    @Test
    void irModePrintsThreeAddressCode() throws IOException {
        Result result = run("--ir",
                writeTemp("int x = 1 + 2;\nprint x;\n").toString());

        assertEquals(Main.OK, result.code);
        assertTrue(result.out.contains("t1 = 1 + 2"));
        assertTrue(result.out.contains("x = t1"));
        assertTrue(result.out.contains("print x"));
        assertTrue(result.out.contains(
                "Generated 3 three-address instruction(s)."));
    }

    @Test
    void irModeRefusesAProgramWithSemanticErrors() throws IOException {
        Result result = run("--ir",
                writeTemp("int x = \"text\";\n").toString());

        assertEquals(Main.SEMANTIC_ERRORS, result.code);
        assertFalse(result.out.contains("x ="),
                "no code is generated for a bad program");
    }

    @Test
    void syntaxErrorsStopTheRunBeforeTheChecker() throws IOException {
        Result result = run("--check",
                writeTemp("int x = 5\nprint x;\n").toString());

        assertEquals(Main.SYNTAX_ERRORS, result.code);
        assertTrue(result.err.contains("syntax error(s) found:"));
        assertFalse(result.err.contains("semantic"));
    }

    @Test
    void lexicalErrorsStopTheRunBeforeTheParser() throws IOException {
        Result result = run("--ir",
                writeTemp("int x = \"unterminated;\n").toString());

        assertEquals(Main.LEXICAL_ERRORS, result.code);
        assertTrue(result.err.contains("lexical error(s) found"));
    }

    @Test
    void theEarlierModesStillWork() throws IOException {
        Path file = writeTemp("int n = 1;\nprint n;\n");

        assertEquals(Main.OK, run("--tokens", file.toString()).code);
        assertEquals(Main.OK, run("--ast", file.toString()).code);
    }

    @Test
    void anUnknownModeIsAUsageError() throws IOException {
        Result result = run("--semantic",
                writeTemp("print 1;\n").toString());

        assertEquals(Main.USAGE_ERROR, result.code);
        assertTrue(result.err.contains("--check"));
        assertTrue(result.err.contains("--ir"));
    }

    @Test
    void aMissingFileIsAnIoError() {
        assertEquals(Main.IO_ERROR,
                run("--check", "no/such/file.mini").code);
    }
}

