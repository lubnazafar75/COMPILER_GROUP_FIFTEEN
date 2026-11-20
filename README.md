# COMPILER_GROUP_FIFTEEN — Mini-Language Compiler

Term project (Group 15): a compiler for the class mini language, built in five phases in this one repository.

| Phase | Deliverable | Status | Deadline |
|---|---|---|---|
| 1 | Scanner (lexical analysis) | Done | 25 Oct 2026 |
| 2 | Parser (syntax analysis) | Done | 8 Nov 2026 |
| 3 | Semantic analysis / IR | Not started | 22 Nov 2026 |
| 4 | Code generation | Not started | 6 Dec 2026 |
| 5 | Optimizer + final demo | Not started | 13 Dec 2026 |

---

## Quick start — how to run the sample programs

**Requirements:** JDK 17 or newer. Maven is bundled with IntelliJ IDEA Community Edition, so no separate install is needed.

### In IntelliJ IDEA (recommended)

1. **File → Open** and select the project folder. IntelliJ detects `pom.xml` and loads it as a Maven project.
2. If prompted, set the SDK: **File → Project Structure → Project → SDK** → Java 17 or newer.
3. Open `src/main/java/compiler/Main.java` and click the green Run arrow beside `main`. It will print a usage message and stop — this is expected, because no file was given.
4. Click the **Main** dropdown at the top right → **Edit Configurations…** and set:
   - **Program arguments:** `--ast samples/parser_valid.mini`
   - **Working directory:** the project folder (the one containing `pom.xml`)
5. Click **OK**, then the green Run arrow.
6. To run the other samples, change only **Program arguments** and run again. The three runs are listed below.

### From a terminal

```
mvn package
java -jar target/compiler-group-fifteen-0.1.0-phase1.jar --ast samples/parser_valid.mini
```

Run the commands from the project folder. The exact jar file name is whatever appears in `target/` after `mvn package`.

---

## The three sample runs

### 1. Phase 2 — parse a valid program

```
--ast samples/parser_valid.mini
```

`samples/parser_valid.mini` uses every statement kind and every operator group. The parser prints the abstract syntax tree as an indented outline, where indentation shows which node contains which.

Expected output (exit code **0**):

```
Source file: samples/parser_valid.mini
Program
  VarDecl int a
    Literal 7
  VarDecl int b
  VarDecl string name
    Literal "Group 15"
  Assign b
    Binary -
      Binary *
        Group
          Binary +
            Variable a
            Literal 3
        Literal 2
      Unary -
        Literal 1
  If
    Condition
      Binary &&
        Binary >
          Variable a
          Literal 5
        Unary !
          Group
            Binary ==
              Variable b
              Literal 0
    Then
      Block
        Print
          Literal "a is big"
    Else
      Block
        Print
          Binary %
            Binary *
              Literal 3.5
              Variable a
            Literal 2
  While
    Condition
      Binary >
        Variable a
        Literal 0
    Body
      Block
        Assign a
          Binary -
            Variable a
            Literal 1
        If
          Condition
            Binary ==
              Variable a
              Literal 3
          Then
            Print
              Variable name
  Return
Parse completed with no syntax errors.
```

The tree shows that operator precedence is handled correctly. In `b = (a + 3) * 2 - -1;` the bracketed addition is the deepest node, the multiplication sits above it, and the subtraction is at the top. In the `else` branch, `3.5 * a % 2` groups as `(3.5 * a) % 2`, because `*` and `%` have the same precedence and associate left to right.

### 2. Phase 2 — syntax errors and recovery

```
--ast samples/syntax_errors.mini
```

`samples/syntax_errors.mini` scans without a single lexical error, but contains **five deliberate syntax errors**. The parser reports each one with its line and column, recovers, and carries on, so one run finds all five. No cascading or duplicate errors are produced.

Expected output (exit code **4**):

```
Source file: samples/syntax_errors.mini
5 syntax error(s) found:
Syntax Error [Line 2, Col 10]: Expected ';' after variable declaration, but found 'print'
Syntax Error [Line 4, Col 4]: Expected '(' after 'if', but found 'x'
Syntax Error [Line 7, Col 5]: Expected an expression, but found ';'
Syntax Error [Line 8, Col 14]: Expected ')' after the while condition, but found '{'
Syntax Error [Line 11, Col 13]: Expected ')' after expression, but found ';'
```

The five mistakes in the file are: a missing `;` (line 2), missing brackets after `if` (line 4), a missing expression after `=` (line 7), a missing `)` in the `while` condition (line 8), and a missing `)` in a `print` (line 11).

A missing `;` is reported at the **end of the previous token**, which is where it should be typed, rather than at the start of the next line.

### 3. Phase 1 — the scanner

```
--tokens samples/hello.mini
```

Prints the token report for a valid program (exit code **0**). `--tokens samples/errors.mini` shows lexical error reporting instead (exit code **1**).

---

## Command line and exit codes

```
java -jar <jar> --tokens <file.mini>     Phase 1: print the token report
java -jar <jar> --ast    <file.mini>     Phase 2: print the syntax tree
```

| Exit code | Meaning |
|---|---|
| 0 | Success |
| 1 | Lexical errors (in `--ast` mode the program is not parsed) |
| 2 | Wrong arguments |
| 3 | File could not be read |
| 4 | Syntax errors |

| Sample file | Purpose |
|---|---|
| `samples/hello.mini` | Valid program (Phase 1) |
| `samples/errors.mini` | Deliberate lexical errors (Phase 1) |
| `samples/parser_valid.mini` | Valid program using every construct (Phase 2) |
| `samples/syntax_errors.mini` | Five deliberate syntax errors (Phase 2) |

---

## Running the tests

In IntelliJ: right-click `src/test/java` → **Run 'All Tests'**.
From a terminal: `mvn test`.

Expected: **115 tests, all passing** — 32 for the scanner and 83 for the parser. They cover every syntax tree node, operator precedence and associativity, every statement kind, each kind of syntax error with its message and position, error recovery, and the command-line modes.

---

## Project layout

```
src/main/java/compiler/
    Main.java                 CLI entry point (--tokens, --ast)
    lexer/                    Phase 1: scanner
    ast/                      Phase 2: syntax tree nodes
    parser/                   Phase 2: recursive-descent parser
    error/                    Lexical and syntax error collection
    util/                     Token report printer, syntax tree printer
src/test/java/compiler/       JUnit 5 tests (lexer, ast, parser, util, Main)
samples/                      Example .mini programs
docs/                         Phase documentation
```

## Documentation

- `docs/PHASE1_SCANNER.md` — token specification
- `docs/PHASE2_PARSER.md` — grammar, operator precedence, syntax tree, error recovery

## Team (Group 15)

| Member | Index | Phase 1 | Phase 2 |
|---|---|---|---|
| Mercy Atta Gyamfi | 0164 | Team lead — repo, build, `Main` | Team lead — grammar doc, `Main --ast`, release |
| Philip Essel | 0157 | `Token`, `TokenType`, `SourceReader`, `Lexer` | `Program`, `Parser`, `PipelineTest` |
| Osei Yeboah | 0158 | `StringScanner`, `CommentSkipper` | `StatementParser`, `StatementParseTest` |
| Michael Addo | 0159 | `KeywordTable` | `TokenStream`, `TokenStreamTest` |
| Kessina Alex | 0160 | `NumberScanner`, `OperatorScanner` | `ExpressionParser`, `ExpressionParseTest` |
| Wrightner Owusu Ansah | 0161 | `LexicalError`, `ErrorReporter` | `StmtNodeTest`, `AstPrinterTest` |
| Egmine James | 0163 | Tests part A | Expression nodes, `ExprNodeTest` |
| Lubna Zafar | 0167 | Tests part B, docs | `ParserErrorTest`, `syntax_errors.mini`, final README; merges into `draft` |
| Baria Mahmood | 0169 | `TokenPrinter` | Statement nodes, `AstPrinter` |
| Oliver Sedinam Agbenyadzi | 0170 | `TestUtil`, samples | Syntax error classes, `ParserTestUtil`, `parser_valid.mini` |


