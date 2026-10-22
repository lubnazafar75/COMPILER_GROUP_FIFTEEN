# COMPILER_GROUP_ONE — Mini-Language Compiler
Term project: a compiler for the class mini language, built in five phases in this one repository.
| Phase | Deliverable | Status | Deadline |
|---|---|---|---|
| 1 | Scanner (lexical analysis) | Done | 25 Oct 2026 |
| 2 | Parser | Not started | 8 Nov 2026 |
| 3 | Semantic analysis / IR | Not started | 22 Nov 2026 |
| 4 | Code generation | Not started | 6 Dec 2026 |
| 5 | Optimizer + final demo | Not started | 13 Dec 2026 |
## Requirements- JDK 17 or newer- Maven (bundled with IntelliJ IDEA Community Edition — no separate install needed)
## Run in IntelliJ IDEA
1. Open the project folder (IntelliJ detects `pom.xml` and loads it as a Maven project).
2. Open `src/main/java/compiler/Main.java` and click the green Run arrow beside `main`.
3. Run  >  Edit Configurations  >  **Program arguments**: `--tokens samples/hello.mini`, then run again.
4. Run all tests: right-click `src/test/java`  >  **Run 'All Tests'**.
## Run from a terminal
```
mvn package
java -jar target/compiler-group-one-0.1.0-phase1.jar --tokens samples/hello.mini
```
## Project layout
```
src/main/java/compiler/
    Main.java                 CLI entry point (--tokens)
    lexer/                    Phase 1 scanner
    error/                    Lexical error collection
    util/                     Token report printer
src/test/java/compiler/lexer/ JUnit 5 tests for the scanner
samples/                      Example .mini programs
docs/                         Phase documentation
```
## Team (Group 15)
| Member | Index | Phase 1 responsibility |
|---|---|---|
| Mercy Atta Gyamfi | 0164 | Team lead — repo, build, `Main.java` |
| Philip Essel | 0157 | Scanner lead — `Token`, `TokenType`, `SourceReader`, `Lexer` |
| Osei Yeboah | 0158 | Co-lead — `StringScanner`, `CommentSkipper` |
| Michael Addo | 0159 | `KeywordTable` |
| Kessina Alex | 0160 | `NumberScanner`, `OperatorScanner` |
| Wrightner Owusu Ansah | 0161 | `LexicalError`, `ErrorReporter` |
| Baria Mahmood | 0169 | `TokenPrinter` |
| Oliver Sedinam Agbenyadzi | 0170 | `TestUtil`, `SourceReaderTest`, samples |
| Egmine James | 0163 | Tests part A — keywords, identifiers, numbers |
| Lubna Zafar | 0167 | Tests part B — strings, comments, operators, errors; docs |
See `docs/PHASE1_SCANNER.md` for the token specification.