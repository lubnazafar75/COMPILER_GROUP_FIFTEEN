
# COMPILER_GROUP_FIFTEEN — Mini-Language Compiler

Term project (Group 15): a compiler for the class mini language, built in five phases in this one repository.

| Phase | Deliverable | Status | Deadline |
|---|---|---|---|
| 1 | Scanner (lexical analysis) | Done | 25 Oct 2026 |
| 2 | Parser (syntax analysis) | Done | 8 Nov 2026 |
| 3 | Semantic analysis / IR | Done | 22 Nov 2026 |
| 4 | Code generation | In progress | 6 Dec 2026 |
| 5 | Optimizer + final demo | Not started | 13 Dec 2026 |

---

## Quick start — how to run the sample programs

**Requirements:** JDK 17 or newer. Maven is bundled with IntelliJ IDEA Community Edition, so no separate install is needed.

### In IntelliJ IDEA (recommended)

1. **File → Open** and select the project folder. IntelliJ detects `pom.xml` and loads it as a Maven project.
2. If prompted, set the SDK: **File → Project Structure → Project → SDK** → Java 17 or newer.
3. Open `src/main/java/compiler/Main.java` and click the green Run arrow beside `main`. It will print a usage message and stop — this is expected, because no file was given.
4. Click the **Main** dropdown at the top right → **Edit Configurations…** and set:
   - **Program arguments:** `--ir samples/semantic_valid.mini`
   - **Working directory:** the project folder (the one containing `pom.xml`)
5. Click **OK**, then the green Run arrow.
6. To run the other samples, change only **Program arguments** and run again.

### From a terminal

```text
mvn package
java -jar target/compiler-group-fifteen-0.1.0-phase1.jar --ir samples/semantic_valid.mini
```

Run the commands from the project folder. The exact jar file name is whatever appears in `target/` after `mvn package`.

---

## The six modes

```text
--tokens <file.mini>     Phase 1: print the token report
--ast    <file.mini>     Phase 2: print the syntax tree
--check  <file.mini>     Phase 3: check that the program makes sense
--ir     <file.mini>     Phase 3: print the three-address code
--asm    <file.mini>     Phase 4: print numbered virtual machine instructions
--run    <file.mini>     Phase 4: execute the program on the virtual machine
```

Each mode runs every earlier phase first and stops at the first phase that fails. There is no sense checking a program that does not parse, or generating code for a program that does not make sense.

| Exit code | Meaning |
|---|---|
| 0 | Success |
| 1 | Lexical errors |
| 2 | Wrong arguments |
| 3 | File could not be read |
| 4 | Syntax errors |
| 5 | Semantic errors |
| 6 | Runtime or code-generation errors |

| Sample file | Purpose |
|---|---|
| `samples/hello.mini` | Valid program (Phase 1) |
| `samples/errors.mini` | Deliberate lexical errors (Phase 1) |
| `samples/parser_valid.mini` | Valid program using every construct (Phase 2) |
| `samples/syntax_errors.mini` | Five deliberate syntax errors (Phase 2) |
| `samples/semantic_valid.mini` | Parses and makes sense (Phase 3) |
| `samples/semantic_errors.mini` | Six deliberate semantic errors (Phase 3) |
| `samples/codegen_demo.mini` | Demonstrates code generation and execution (Phase 4) |
| `samples/runtime_error.mini` | Demonstrates runtime error handling (Phase 4) |

---

## Phase 3 — the sample runs

### 1. Check a program that makes sense

```text
--check samples/semantic_valid.mini
```

Expected output (exit code **0**):

```text
Source file: samples/semantic_valid.mini
Semantic analysis completed with no errors.
```

### 2. Report semantic errors

```text
--check samples/semantic_errors.mini
```

`samples/semantic_errors.mini` scans and parses without a single complaint, but it does not make sense. It holds six deliberate semantic errors, one per numbered comment in the file. The checker reports all six in one run, each with its line and column.

Expected output (exit code **5**):

```text
Source file: samples/semantic_errors.mini
6 semantic error(s) found:
Semantic Error [Line 5, Col 5]: Variable 'x' is already declared in this scope as int x (declared on line 4)
Semantic Error [Line 7, Col 1]: Variable 'y' is used but never declared
Semantic Error [Line 10, Col 1]: Cannot store a string value in 'x', which is declared as int
Semantic Error [Line 12, Col 9]: Operator '+' cannot be used with int and string
Semantic Error [Line 14, Col 1]: The condition of 'if' must be a bool, but it is int
Semantic Error [Line 15, Col 11]: Variable 'total' is used but never declared
```

### 3. Generate three-address code

```text
--ir samples/semantic_valid.mini
```

The syntax tree is flattened into instructions that each do one thing. Nested expressions use temporaries `t1`, `t2`, … and `if` / `while` become labels and jumps.

### 4. Generating code is refused for a program that does not make sense

```text
--ir samples/semantic_errors.mini
```

Prints the semantic errors and exits with code **5**. No instructions are generated.

---

## Phase 4 — code generation

Phase 4 translates the three-address Intermediate Representation (IR) produced in Phase 3 into instructions for a stack-based virtual machine.

The pipeline is:

```text
Source → Scanner → Parser → Semantic Analysis → IR Generator → Code Generator → Virtual Machine
```

### 1. Generate assembly instructions

```text
--asm samples/codegen_demo.mini
```

This compiles the source program and displays a numbered assembly listing. It does not execute the program.

Example format:

```text
0000  PUSH 10
0001  STORE count
0002  LOAD count
...
```

The actual instructions depend on the source program.

### 2. Execute a program

```text
--run samples/codegen_demo.mini
```

This compiles the source program and executes the generated instructions on the virtual machine. If the program requests input, enter the requested value in the Run console.

### 3. Runtime errors

```text
--run samples/runtime_error.mini
```

Runtime errors include division by zero, remainder by zero, accessing an uninitialized slot, missing input, and execution exceeding the virtual machine's step limit.

Runtime errors are reported with exit code **6**.

For a detailed explanation of the architecture, instruction set, label resolution and testing strategy, see `docs/PHASE4_CODEGEN.md`.

---

## Phase 1 and Phase 2 — the sample runs

```text
--tokens samples/hello.mini          token report, exit code 0
--tokens samples/errors.mini         lexical errors, exit code 1
--ast    samples/parser_valid.mini   syntax tree, exit code 0
--ast    samples/syntax_errors.mini  five syntax errors, exit code 4
```

`--ast samples/parser_valid.mini` prints the tree as an indented outline.

---

## Running the tests

In IntelliJ: right-click `src/test/java` → **Run 'All Tests'**.

From a terminal:

```text
mvn test
```

The Phase 3 baseline is **226 tests**: 32 for the scanner, 83 for the parser and 111 for semantic analysis and the IR.

Phase 4 adds tests for code generation, label resolution, virtual machine instructions, execution, input/output, runtime errors and command-line modes.

The expected total after all Phase 4 work is **329 tests**, all passing.

---

## Project layout

```text
src/main/java/compiler/
    Main.java                 CLI entry point
    lexer/                    Phase 1: scanner
    ast/                      Phase 2: syntax tree nodes
    parser/                   Phase 2: recursive-descent parser
    semantic/                 Phase 3: types, symbol table, checkers
    ir/                       Phase 3: three-address code
    codegen/                  Phase 4: code generation and label resolution
    vm/                       Phase 4: virtual machine and instructions
    error/                    Lexical, syntax and semantic error collection
    util/                     Token, AST, IR and assembly printers

src/test/java/compiler/       JUnit 5 tests
samples/                      Example .mini programs
docs/                         Phase documentation
```

## Documentation

- `docs/PHASE1_SCANNER.md` — token specification
- `docs/PHASE2_PARSER.md` — grammar, operator precedence, syntax tree and error recovery
- `docs/PHASE3_SEMANTIC.md` — types, scopes, semantic rules and three-address code
- `docs/PHASE4_CODEGEN.md` — code generation, virtual machine architecture and instruction set

---

## Team (Group 15)

| Member | Index | Phase 1 | Phase 2 | Phase 3 |
|---|---|---|---|---|
| Mercy Atta Gyamfi | 0164 | Team lead — repo, build, `Main` | Grammar doc, `Main --ast`, release | Design doc, `Main --check/--ir` |
| Philip Essel | 0157 | `Token`, `TokenType`, `SourceReader`, `Lexer` | `Program`, `Parser`, `PipelineTest` | `SemanticAnalyzer`, `IrPipelineTest` |
| Osei Yeboah | 0158 | `StringScanner`, `CommentSkipper` | `StatementParser`, `StatementParseTest` | `DeclarationChecker` and its tests |
| Michael Addo | 0159 | `KeywordTable` | `TokenStream`, `TokenStreamTest` | `TypeChecker` and its tests |
| Kessina Alex | 0160 | `NumberScanner`, `OperatorScanner` | `ExpressionParser`, `ExpressionParseTest` | `TypeRules`, `IrGenerator` |
| Wrightner Owusu Ansah | 0161 | `LexicalError`, `ErrorReporter` | `StmtNodeTest`, `AstPrinterTest` | IR instruction types and their tests |
| Egmine James | 0163 | Tests part A | Expression nodes, `ExprNodeTest` | `Type`, `Symbol`, and the table tests |
| Lubna Zafar | 0167 | Tests part B, docs | `ParserErrorTest`, `syntax_errors.mini` | `IrBuilder`, `SemanticErrorTest`; merges into `draft` |
| Baria Mahmood | 0169 | `TokenPrinter` | Statement nodes, `AstPrinter` | `SymbolTable`, `IrPrinter` |
| Oliver Sedinam Agbenyadzi | 0170 | `TestUtil`, samples | Syntax error classes, `ParserTestUtil` | Semantic error classes, `SemanticTestUtil` |
