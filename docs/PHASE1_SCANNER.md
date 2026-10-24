# Phase 1 — Scanner (Lexical Analysis)
The scanner reads a `.mini` source file character by character and groups the characters into **tokens**. Whitespace
and comments are discarded. Each token records its type, its exact text (lexeme), and the line and column where it
starts.
## Token specification
| Category | Tokens |
|---|---|
| Keywords | `if` `else` `while` `return` `int` `string` `print` `input` |
| Identifiers | letter or `_`, then letters, digits or `_` (case-sensitive) |
| Integer literal | one or more digits — `42` |
| Decimal literal | digits `.` digits — `19.75` |
| String literal | `"..."` on one line; escapes `\"` `\\` `\n` `\t` |
| Operators | `+ - * / % = == ! != < <= > >= && \|\|` |
| Punctuation | `( ) { } [ ] ; ,` |
| Comments | `// to end of line` and `/* block, may span lines */` |
Two-character operators use the **longest match** rule: `<=` is one token, not `<` then `=`.
## Errors and recovery
The scanner never stops at the first error. It records the error, skips past the problem, and keeps going so every
error is reported in one run.
| Error | Example | Recovery |
|---|---|---|
| ILLEGAL_CHARACTER | `@`, `#`, single `&` | character skipped |
| MALFORMED_NUMBER | `12.` | ERROR token, scanning continues |
| UNTERMINATED_STRING | `"abc` then newline | ERROR token, resumes next line |
| UNTERMINATED_COMMENT | `/*` never closed | rest of file skipped |
| INVALID_ESCAPE | `"\q"` | reported, string continues |
## Design
`Lexer` is a dispatcher: it looks at the next character and hands work to one specialist class (`StringScanner`,
`NumberScanner`, `KeywordTable`, `OperatorScanner`, `CommentSkipper`). All movement through the text goes through
`SourceReader.advance()`, the single place where line and column numbers are updated.
## Usage
```
java -jar target/compiler-group-fifteen-0.1.0-phase1.jar --tokens samples/hello.mini
```
Exit codes: `0` success, `1` lexical errors, `2` wrong arguments, `3` file not readable.
## Tests
32 JUnit 5 tests in `src/test/java/compiler/lexer/` cover every token category, line/column tracking, and each error
kind.