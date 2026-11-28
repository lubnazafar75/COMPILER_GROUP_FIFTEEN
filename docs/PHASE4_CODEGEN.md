
# Phase 4 — Code Generation Design

## 1. Overview

Phase 4 extends the compiler by translating the Intermediate Representation (IR) produced in Phase 3 into executable instructions for a simple stack-based virtual machine.

The complete compilation pipeline is:

Source Code → Scanner → Parser → Semantic Analysis → IR Generation → Code Generation → Virtual Machine

The code generator converts the three-address IR instructions into machine instructions. The virtual machine then executes those instructions.

## 2. Objectives

The objectives of Phase 4 are to:

- Translate valid IR programs into virtual machine instructions.
- Resolve symbolic labels into numeric instruction addresses.
- Implement a stack-based virtual machine.
- Support arithmetic, comparison, logical, branching, input and output operations.
- Execute generated programs and report runtime errors clearly.

## 3. Virtual Machine Architecture

The virtual machine uses a stack-based execution model and maintains three main components:

### Operand Stack
Stores intermediate values used during expression evaluation.

### Named Slots
Stores variables and temporary values by name. Instructions use these slots to load and store values.

### Program Counter
Tracks the address of the next instruction to execute. Jump instructions modify the program counter to control program flow.

## 4. Instruction Set

The virtual machine supports the following instructions:

| Instruction | Purpose |
|---|---|
| PUSH | Push a constant value onto the operand stack |
| LOAD | Load a value from a named slot |
| STORE | Store a value in a named slot |
| ADD | Add two values |
| SUB | Subtract two values |
| MUL | Multiply two values |
| DIV | Divide two values |
| MOD | Calculate the remainder |
| LT | Less-than comparison |
| LE | Less-than-or-equal comparison |
| GT | Greater-than comparison |
| GE | Greater-than-or-equal comparison |
| EQ | Equality comparison |
| NE | Inequality comparison |
| NEG | Negate a numeric value |
| NOT | Perform logical negation |
| JMP | Jump to an instruction address |
| JMP_FALSE | Jump when the top value is false |
| JMP_TRUE | Jump when the top value is true |
| PRINT | Print a value |
| INPUT | Read a value from standard input |
| RET | Return a value from the program |
| HALT | Stop program execution |

Every generated program ends with a HALT instruction.

## 5. Code Generation Design

Code generation takes place in two main stages.

### Stage 1: Label Resolution

The LabelResolver calculates the numeric instruction addresses associated with symbolic labels.

It performs a first pass over the IR instructions, recording the address of each label. These addresses are used to replace symbolic jump targets with numeric addresses.

### Stage 2: Instruction Generation

The CodeGenerator performs the second pass. It translates each IR instruction into one or more virtual machine instructions.

Jump instructions use the numeric addresses calculated during label resolution.

The result is a VmProgram containing the generated instructions.

## 6. Main Components

The implementation is organized into the following packages:

### compiler.codegen

- **LabelResolver:** Resolves symbolic labels into instruction addresses.
- **CodeGenerator:** Converts IR instructions into virtual machine instructions.
- **Backend:** Provides the interface for compiling IR into executable machine code.

### compiler.vm

- **OpCode:** Defines the supported virtual machine operations.
- **Instruction:** Represents an individual machine instruction.
- **VmProgram:** Stores the generated instruction sequence.
- **Value:** Represents values handled by the virtual machine.
- **VmException:** Represents runtime execution errors.
- **VirtualMachine:** Executes the generated instructions.

### compiler.util

- **AsmPrinter:** Formats and displays generated machine instructions as a numbered assembly listing.

## 7. Runtime Error Handling

The virtual machine detects and reports runtime errors, including:

- Division by zero.
- Remainder by zero.
- Accessing an uninitialized variable or slot.
- Missing or invalid input.
- Excessive execution caused by an infinite loop or step-limit violation.

Runtime errors are reported clearly instead of allowing the program to fail silently.

## 8. Command-Line Modes

Phase 4 extends the compiler with two additional command-line modes:

### --asm

Generates and displays the numbered virtual machine assembly instructions without executing the program.

Example:

```text
--asm samples/codegen_demo.mini
```

### --run

Compiles the source program and executes it on the virtual machine.

Example:

```text
--run samples/codegen_demo.mini
```

Programs that request input read from standard input during execution.

## 9. Exit Codes

| Exit Code | Meaning |
|---|---|
| 0 | Successful execution |
| 1 | Lexical errors |
| 2 | Invalid command-line arguments |
| 3 | File input/output error |
| 4 | Syntax errors |
| 5 | Semantic errors |
| 6 | Runtime or code-generation error |

## 10. Testing Strategy

Phase 4 is tested at different levels:

- Unit tests for individual virtual machine instructions.
- Tests for label resolution.
- Tests for IR-to-machine-code translation.
- Tests for virtual machine arithmetic and comparisons.
- Tests for conditional and unconditional jumps.
- Tests for input and output.
- Tests for runtime error handling.
- Integration tests for the `--asm` and `--run` command-line modes.

The tests verify that generated programs execute correctly and that errors are handled as expected.

## 11. Expected Outcome

At the end of Phase 4, the compiler will be able to translate semantically valid source programs into virtual machine instructions and execute them.

This completes the code-generation stage and prepares the compiler for Phase 5, which introduces optimization and the final project demonstration.
