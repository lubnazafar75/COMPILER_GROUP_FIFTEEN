# Phase 3: Semantic Analysis and Intermediate Representation

## 1. Introduction

Phase 3 extends the Group 15 Mini-Language Compiler by introducing semantic analysis and intermediate representation (IR) generation.

Semantic analysis checks whether a program is meaningful according to the language rules after lexical and syntax analysis have been completed.

Intermediate representation converts the validated program into a simpler, structured form that can be used by later compiler phases.

## 2. Objectives

The objectives of Phase 3 are to:

- Detect semantic errors in source programs.
- Maintain symbol tables for variable declarations and scope management.
- Perform type checking on expressions and statements.
- Generate three-address intermediate code.
- Support control-flow representation using labels and jumps.
- Provide clear semantic error messages.

## 3. Semantic Analysis

Semantic analysis verifies that the program follows the language's meaning and type rules.

### 3.1 Supported Types

The compiler recognizes the following types:

- `int`
- `float`
- `string`
- `bool`
- `<error>` — used internally to prevent repeated error reporting.

### 3.2 Semantic Rules

The semantic analyzer must enforce the following rules:

1. Every variable must be declared before it is used.
2. A variable cannot be declared more than once within the same scope.
3. Assignment types must match.
4. Operators must be used with compatible operand types.
5. Conditions in `if` and `while` statements must have Boolean type.
6. Boolean expressions cannot be printed.
7. Variables declared in inner scopes must not overwrite declarations in outer scopes.

### 3.3 Scope Management

The compiler uses nested scopes to manage variable declarations.

- Opening a block with `{` creates a new scope.
- Closing a block with `}` exits the current scope.
- Variable lookup begins in the innermost scope and continues outward.
- An inner declaration may hide a variable with the same name in an outer scope.

### 3.4 Semantic Components

The semantic analysis package is:

`compiler.semantic`

It contains:

- `Type` — represents supported data types.
- `Symbol` — represents a declared variable.
- `SymbolTable` — manages declarations and scopes.
- `TypeRules` — defines type compatibility rules.
- `TypeChecker` — checks expression types.
- `DeclarationChecker` — validates declarations.
- `SemanticAnalyzer` — coordinates semantic analysis.

Semantic error reporting is handled in:

`compiler.error`

It includes:

- `SemanticError`
- `SemanticErrorReporter`

## 4. Intermediate Representation (IR)

The compiler generates an intermediate representation using three-address code.

Three-address code breaks complex expressions into simpler instructions, making the program easier to translate into machine code in later phases.

### 4.1 Temporary Variables

Temporary variables are used to store intermediate expression results.

Examples:

- `t1`
- `t2`
- `t3`

### 4.2 Labels and Control Flow

Labels represent positions in the generated code.

Examples:

- `L1`
- `L2`

Conditional and unconditional jumps are used to represent control flow.

Short-circuit evaluation must be preserved when generating logical expressions.

### 4.3 IR Components

The intermediate representation package is:

`compiler.ir`

It contains:

- `IrOp` — defines IR operation types.
- `IrInstruction` — represents an individual IR instruction.
- `IrProgram` — stores the generated IR instructions.
- `IrBuilder` — assists in constructing IR.
- `IrGenerator` — generates IR from the program's syntax tree.

The `IrPrinter` class in `compiler.util` displays the generated intermediate representation.

## 5. Command-Line Interface

The compiler will support the following command-line modes:

- `--tokens` — performs lexical analysis.
- `--ast` — displays the abstract syntax tree.
- `--check` — performs semantic analysis.
- `--ir` — generates and displays intermediate representation.

### Expected Exit Codes

| Exit Code | Meaning |
|---|---|
| 0 | Successful execution |
| 1 | Lexical error |
| 2 | Incorrect command-line arguments |
| 3 | File reading error |
| 4 | Syntax error |
| 5 | Semantic error |

## 6. Testing

Phase 3 testing will cover:

- Variable declaration and usage.
- Duplicate declarations.
- Scope handling.
- Type compatibility.
- Invalid assignments.
- Invalid conditions.
- Semantic error reporting.
- Intermediate representation generation.
- Temporary variable generation.
- Labels and control-flow instructions.

All new functionality must be tested alongside the existing scanner and parser tests.

## 7. Conclusion

Phase 3 builds on the scanner and parser developed in the earlier phases. By introducing semantic analysis and intermediate representation, the compiler becomes capable of checking program meaning and preparing valid programs for code generation in Phase 4.