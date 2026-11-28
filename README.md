# COMPILER_GROUP_FIFTEEN — Mini-Language Compiler

Group Members 
1. philipessel177-ctr : PS/CSC/23/0157
2. kwadwofokuo88 : PS/CSC/23/0158
3. michael-addo-0x : PS/CSC/23/0159
4. Alexxes : PS/CSC/23/0160
5. LightenStar13 : PS/CSC/23/0161
6. egminejames5-creat : PS/CSC/23/0163
7. missAttaGyamfi : PS/CSC/23/0164
8. lubnazafar75 : PS/CSC/23/0167
9. bariamahmood : PS/CSC/23/0169
10. Oliversedinam : PS/CSC/23/0170








Term project (Group 15): a compiler for the class mini language, built in five phases in this one repository.

| Phase | Deliverable | Status | Deadline |
|---|---|---|---|
| 1 | Scanner (lexical analysis) | Done | 25 Oct 2026 |
| 2 | Parser (syntax analysis) | Done | 8 Nov 2026 |
| 3 | Semantic analysis / IR | Done | 22 Nov 2026 |
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
   - **Program arguments:** `--ir samples/semantic_valid.mini`
   - **Working directory:** the project folder (the one containing `pom.xml`)
5. Click **OK**, then the green Run arrow.
6. To run the other samples, change only **Program arguments** and run again. The runs are listed below.

### From a terminal

```
mvn package
java -jar target/compiler-group-fifteen-0.1.0-phase1.jar --ir samples/semantic_valid.mini
```

Run the commands from the project folder. The exact jar file name is whatever appears in `target/` after `mvn package`.

---

## The four modes

```
--tokens <file.mini>     Phase 1: print the token report
--ast    <file.mini>     Phase 2: print the syntax tree
--check  <file.mini>     Phase 3: check that the program makes sense
--ir     <file.mini>     Phase 3: print the three-address code
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

| Sample file | Purpose |
|---|---|
| `samples/hello.mini` | Valid program (Phase 1) |
| `samples/errors.mini` | Deliberate lexical errors (Phase 1) |
| `samples/parser_valid.mini` | Valid program using every construct (Phase 2) |
| `samples/syntax_errors.mini` | Five deliberate syntax errors (Phase 2) |
| `samples/semantic_valid.mini` | Parses and makes sense (Phase 3) |
| `samples/semantic_errors.mini` | Six deliberate semantic errors (Phase 3) |

---

## Phase 3 — the sample runs

### 1. Check a program that makes sense

```
--check samples/semantic_valid.mini
```

Expected output (exit code **0**):

```
Source file: samples/semantic_valid.mini
Semantic analysis completed with no errors.
```

### 2. Report semantic errors

```
--check samples/semantic_errors.mini
```

`samples/semantic_errors.mini` scans and parses without a single complaint, but it does not make sense. It holds **six deliberate semantic errors**, one per numbered comment in the file. The checker reports all six in one run, each with its line and column.

Expected output (exit code **5**):

```
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

```
--ir samples/semantic_valid.mini
```

The syntax tree is flattened into instructions that each do one thing. Nested expressions use temporaries `t1`, `t2`, … and `if` / `while` become labels and jumps.

Expected output (exit code **0**):

```
Source file: samples/semantic_valid.mini
  count = 0
  label = "Total so far: "
  input limit
L1:
  t1 = count < limit
  t2 = t1
  if_false t2 goto L3
  t3 = limit > 0
  t2 = t3
L3:
  if_false t2 goto L2
  t4 = count + 1
  count = t4
  t5 = count * 2
  step = t5
  print step
  t6 = count == 3
  t7 = t6
  if_true t7 goto L4
  t8 = count == 7
  t7 = t8
L4:
  if_false t7 goto L5
  print label
  goto L6
L5:
  print count
L6:
  goto L1
L2:
  t9 = label + "done"
  print t9
  return count
Generated 33 three-address instruction(s).
```

Two things to notice. `L1`/`L2` are the top and the end of the `while` loop, and the jump back to `L1` is the loop itself. The pair of jumps around `L3` and `L4` is `&&` and `||` **stopping early**: if `count < limit` is already false, `limit > 0` is never evaluated, exactly as in Java.

### 4. Generating code is refused for a program that does not make sense

```
--ir samples/semantic_errors.mini
```

Prints the same six semantic errors and exits with code **5**. No instructions are generated.

---

## Phase 1 and Phase 2 — the sample runs

```
--tokens samples/hello.mini          token report, exit code 0
--tokens samples/errors.mini         lexical errors, exit code 1
--ast    samples/parser_valid.mini   syntax tree, exit code 0
--ast    samples/syntax_errors.mini  five syntax errors, exit code 4
```

`--ast samples/parser_valid.mini` prints the tree as an indented outline. It begins:

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
  ...
Parse completed with no syntax errors.
```

`--ast samples/syntax_errors.mini` reports five syntax errors, recovering after each one so a single run finds them all:

```
5 syntax error(s) found:
Syntax Error [Line 2, Col 10]: Expected ';' after variable declaration, but found 'print'
Syntax Error [Line 4, Col 4]: Expected '(' after 'if', but found 'x'
Syntax Error [Line 7, Col 5]: Expected an expression, but found ';'
Syntax Error [Line 8, Col 14]: Expected ')' after the while condition, but found '{'
Syntax Error [Line 11, Col 13]: Expected ')' after expression, but found ';'
```

---

## Running the tests

In IntelliJ: right-click `src/test/java` → **Run 'All Tests'**.
From a terminal: `mvn test`.

Expected: **226 tests, all passing** — 32 for the scanner, 83 for the parser and 111 for semantic analysis and the IR. They cover every syntax tree node, operator precedence, every statement kind, each kind of syntax and semantic error with its message and position, error recovery, scoping and type rules, the three-address code for every construct, and the command-line modes.

---

## Project layout

```
src/main/java/compiler/
    Main.java                 CLI entry point (--tokens, --ast, --check, --ir)
    lexer/                    Phase 1: scanner
    ast/                      Phase 2: syntax tree nodes
    parser/                   Phase 2: recursive-descent parser
    semantic/                 Phase 3: types, symbol table, checkers
    ir/                       Phase 3: three-address code
    error/                    Lexical, syntax and semantic error collection
    util/                     Token report, syntax tree and IR printers
src/test/java/compiler/       JUnit 5 tests (lexer, ast, parser, semantic, ir, util, Main)
samples/                      Example .mini programs
docs/                         Phase documentation
```

## Documentation

- `docs/PHASE1_SCANNER.md` — token specification
- `docs/PHASE2_PARSER.md` — grammar, operator precedence, syntax tree, error recovery
- `docs/PHASE3_SEMANTIC.md` — types, scopes, semantic rules, three-address code

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
