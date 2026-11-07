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
/** * Phase 2 tests: the command-line modes and exit codes of Main.
 * Each test writes a small temporary .mini file and runs Main on it.
 *

 */
class MainAstModeTest {
    /** What a run of Main printed and returned. */
    private static final class Result {
        int code;
        String out;
        String err;
    }
    private static Path writeTemp(String content) throws IOException {
        Path file = Files.createTempFile("group15-", ".mini");
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
    void astModePrintsTheTreeForAValidProgram() throws IOException {
        Result result = run("--ast", writeTemp("int n;\nprint n;\n").toString());
        assertEquals(Main.OK, result.code);
        assertTrue(result.out.contains("Program"));
        assertTrue(result.out.contains("VarDecl int n"));
        assertTrue(result.out.contains("Parse completed with no syntax errors."));
        assertEquals("", result.err);
    }
    @Test
    void syntaxErrorsGiveExitCode4AndNoTree() throws IOException {
        Result result = run("--ast", writeTemp("int x = 5\nprint x;\n").toString());
        assertEquals(Main.SYNTAX_ERRORS, result.code);
        assertTrue(result.err.contains("1 syntax error(s) found"));
        assertTrue(result.err.contains("Line 1, Col 10"));
        assertFalse(result.out.contains("Program"));
    }
    @Test
    void lexicalErrorsStopBeforeParsing() throws IOException {
        Result result = run("--ast", writeTemp("int x = 5 @;\n").toString());
        assertEquals(Main.LEXICAL_ERRORS, result.code);
        assertTrue(result.err.contains("lexical error(s)"));
        assertFalse(result.err.contains("syntax error"));
    }
    @Test
    void tokensModeStillWorks() throws IOException {
        Result result = run("--tokens", writeTemp("int n;\n").toString());
        assertEquals(Main.OK, result.code);
        assertTrue(result.out.contains("TOKEN REPORT"));
    }
    @Test
    void unknownOptionIsAUsageError() {
        Result result = run("--parse", "whatever.mini");
        assertEquals(Main.USAGE_ERROR, result.code);
        assertTrue(result.err.contains("--ast"));
    }
    @Test
    void missingArgumentsAreAUsageError() {
        assertEquals(Main.USAGE_ERROR, run("--ast").code);
        assertEquals(Main.USAGE_ERROR, run().code);
    }
    @Test
    void unreadableFileGivesExitCode3() {
        Result result = run("--ast", "this-file-does-not-exist.mini");
        assertEquals(Main.IO_ERROR, result.code);
        assertTrue(result.err.contains("cannot read file"));
    }
}