# COMPILER_GROUP_FIFTEEN — Mini-Language Compiler

Term project (Group 15): a compiler for the class mini language, developed in five phases in one repository.

| Phase | Deliverable | Status | Deadline |
|---|---|---|---|
| 1 | Scanner (lexical analysis) | Done | 25 Oct 2026 |
| 2 | Parser (syntax analysis) | In progress | 8 Nov 2026 |
| 3 | Semantic analysis / IR | In progress | 22 Nov 2026 |
| 4 | Code generation | Not started | 6 Dec 2026 |
| 5 | Optimizer + final demo | Not started | 13 Dec 2026 |

## Requirements

- JDK 17 or newer
- Maven (bundled with IntelliJ IDEA Community Edition; no separate installation needed)

## Run in IntelliJ IDEA

1. Open the project folder. IntelliJ will detect `pom.xml` and load it as a Maven project.
2. Open `src/main/java/compiler/Main.java` and click the green Run arrow beside `main`.
3. Select Run > Edit Configurations > Program arguments.
4. To run the Phase 1 scanner, enter:

   `--tokens samples/hello.mini`

5. Run all tests by right-clicking `src/test/java` and selecting **Run 'All Tests'**.

## Run from a terminal

Build the project:

```bash
mvn package
```

Run the Phase 1 scanner:

```bash
java -jar target/COMPILER_GROUP_FIFTEEN-0.1.0-phase1.jar --tokens samples/hello.mini
```

## Project layout

```text
src/main/java/compiler/
    Main.java                 CLI entry point
    ast/                      Abstract syntax tree components
    lexer/                    Phase 1: lexical analysis
    parser/                   Phase 2: syntax analysis
    semantic/                 Phase 3: semantic analysis
    ir/                       Phase 3: intermediate representation
    error/                    Error handling and reporting
    util/                     Compiler utility classes

src/test/java/compiler/
    ast/                      AST tests
    lexer/                    Scanner tests
    parser/                   Parser tests
    semantic/                 Semantic analysis tests
    ir/                       Intermediate representation tests

samples/                      Example .mini programs
docs/                         Phase documentation
```

## Documentation

- `docs/PHASE1_SCANNER.md` — scanner token specification.
- `docs/PHASE2_PARSER.md` — parser grammar, operator precedence, AST structure, and syntax error recovery.
- `docs/PHASE3_SEMANTIC.md` — semantic analysis rules, scope management, type checking, and intermediate representation design.

## Team — Group 15

| Member | Index | Phase 1 responsibility |
|---|---|---|
| Mercy Atta Gyamfi | 0164 | Team lead — repository, build, Main.java |
| Philip Essel | 0157 | Token, TokenType, SourceReader, Lexer |
| Osei Yeboah | 0158 | StringScanner, CommentSkipper |
| Michael Addo | 0159 | KeywordTable |
| Kessina Alex | 0160 | NumberScanner, OperatorScanner |
| Wrightner Owusu Ansah | 0161 | LexicalError, ErrorReporter |
| Baria Mahmood | 0169 | TokenPrinter |
| Oliver Sedinam Agbenyadzi | 0170 | TestUtil, SourceReaderTest, samples |
| Egmine James | 0163 | Tests part A — keywords, identifiers, numbers |
| Lubna Zafar | 0167 | Tests part B — strings, comments, operators, errors; documentation |

For details about the scanner, see `docs/PHASE1_SCANNER.md`.

For the grammar and parser design, see `docs/PHASE2_PARSER.md`.

For semantic analysis and intermediate representation, see `docs/PHASE3_SEMANTIC.md`.