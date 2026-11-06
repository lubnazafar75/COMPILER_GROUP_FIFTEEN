# Phase 2 — Parser (Syntax Analysis)

The parser takes the list of tokens produced by the Phase 1 scanner and checks that they follow the grammar of the mini language. If they do, it builds an **abstract syntax tree (AST)**: a tree that shows the structure of the program. If they do not, it reports each syntax error with its line and column and keeps going, so one run finds every mistake.

```
source text --> Scanner --> tokens --> Parser --> syntax tree (AST)
  (Phase 1)                              (Phase 2)      |
                                                        +--> Phase 3: semantic analysis / IR
```

## Grammar

`*` means "zero or more", `?` means "optional", quoted text is a token, UPPERCASE is a token type from the scanner.

```
program      -> statement* EOF

statement    -> varDecl | assignment | inputStmt | printStmt
              | ifStmt | whileStmt | returnStmt | block

varDecl      -> ( "int" | "string" ) IDENTIFIER ( "=" expression )? ";"
assignment   -> IDENTIFIER "=" expression ";"
inputStmt    -> "input" IDENTIFIER ";"
printStmt    -> "print" expression ";"
ifStmt       -> "if" "(" expression ")" statement ( "else" statement )?
whileStmt    -> "while" "(" expression ")" statement
returnStmt   -> "return" expression? ";"
block        -> "{" statement* "}"

expression   -> logicOr
logicOr      -> logicAnd   ( "||" logicAnd )*
logicAnd     -> equality   ( "&&" equality )*
equality     -> comparison ( ( "==" | "!=" ) comparison )*
comparison   -> term       ( ( "<" | "<=" | ">" | ">=" ) term )*
term         -> factor     ( ( "+" | "-" ) factor )*
factor       -> unary      ( ( "*" | "/" | "%" ) unary )*
unary        -> ( "!" | "-" ) unary | primary
primary      -> INT_LITERAL | FLOAT_LITERAL | STRING_LITERAL | IDENTIFIER
              | "(" expression ")"
```

### Operator precedence (tightest first)

| Level | Operators | Grouping |
|---|---|---|
| 1 | `!` `-` (prefix) | right to left |
| 2 | `*` `/` `%` | left to right |
| 3 | `+` `-` | left to right |
| 4 | `<` `<=` `>` `>=` | left to right |
| 5 | `==` `!=` | left to right |
| 6 | `&&` | left to right |
| 7 | `\|\|` | left to right |

So `1 + 2 * 3` is `1 + (2 * 3)` and `10 - 4 - 3` is `(10 - 4) - 3`.

An `else` always belongs to the nearest `if` before it.

## How the parser works

It is a **recursive-descent parser**: one method for each grammar rule. A method looks at the next token, decides which alternative to follow, and calls the methods for the rules inside it. The expression rules are written from the loosest operator (`||`) down to the tightest (literals), which is what gives each operator its precedence.

| Class | Package | Job |
|---|---|---|
| `TokenStream` | `compiler.parser` | Cursor over the tokens: `peek`, `advance`, `match`, `consume`, error reporting, recovery |
| `ExpressionParser` | `compiler.parser` | The expression rules |
| `StatementParser` | `compiler.parser` | The statement rules and the recovery loop |
| `Parser` | `compiler.parser` | Entry point: `new Parser(tokens).parse()` returns a `Program` |
| `ParseException` | `compiler.parser` | Internal: jumps out of a broken statement |
| `SyntaxError`, `SyntaxErrorReporter` | `compiler.error` | One error / the list of all errors |
| `AstPrinter` | `compiler.util` | Prints the tree as an indented outline |

## The syntax tree (`compiler.ast`)

| Expressions (`Expr`) | Statements (`Stmt`) |
|---|---|
| `BinaryExpr(left, operator, right)` | `VarDeclStmt(type, name, initializer)` |
| `UnaryExpr(operator, operand)` | `AssignStmt(name, value)` |
| `LiteralExpr(token)` | `InputStmt(name)` |
| `VariableExpr(name)` | `PrintStmt(keyword, expression)` |
| `GroupingExpr(expression)` | `IfStmt(keyword, condition, thenBranch, elseBranch)` |
| | `WhileStmt(keyword, condition, body)` |
| | `ReturnStmt(keyword, value)` |
| | `BlockStmt(open, statements)` |

`Program(statements)` is the root. Every node keeps its tokens, so later phases know the line and column for their own error messages. Each node has an `accept(visitor)` method; `Expr.Visitor` and `Stmt.Visitor` let Phase 3 and Phase 4 walk the tree without long `if / else` chains.

## Error handling and recovery

When the tokens do not match the grammar, the parser:

1. records `Syntax Error [Line L, Col C]: Expected <what>, but found <token>`;
2. throws `ParseException` to leave the broken statement;
3. **synchronizes**: skips tokens until just after a `;`, or until a keyword that starts a statement (`int string input print if while return`), or until the `}` that closes the current block. A whole `{ ... }` that has to be skipped is skipped as one unit;
4. carries on with the next statement.

A forgotten `;` is reported at the end of the previous token (the place to fix it), not at the start of the next line.

## Usage

```
java -jar target/compiler-group-fifteen-0.1.0-phase1.jar --ast samples/parser_valid.mini
```

In IntelliJ: **Run > Edit Configurations > Program arguments**: `--ast samples/parser_valid.mini`.

| Exit code | Meaning |
|---|---|
| 0 | Success |
| 1 | Lexical errors (the program is not parsed) |
| 2 | Wrong arguments |
| 3 | File could not be read |
| 4 | Syntax errors |

`--tokens` from Phase 1 still works.

## Sample programs

| File | Purpose |
|---|---|
| `samples/hello.mini` | Valid program from Phase 1 |
| `samples/parser_valid.mini` | Valid program using every statement and operator group |
| `samples/syntax_errors.mini` | Scans correctly, but has 5 syntax errors (lines 2, 4, 7, 8, 11) |

## Tests

83 JUnit 5 tests in `src/test/java/compiler/` (ast, parser, util and the top-level package). Together with the 32 scanner tests, the project has 115 tests. They cover every node, operator precedence and associativity, every statement, each kind of syntax error with its message and position, error recovery, and the command-line modes.
