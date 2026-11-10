package compiler.semantic;

import compiler.lexer.TokenType;

/**
* The type rules of the mini language, in one place. 
*
* Each method answers "if the operands have these types, what type does
* the result have?" and returns null when the combination is not allowed. 
* Keeping the rules here means TypeChecker only has to walk the tree; it 
* does not also have to remember the rules.
*
* The rules:
*
* +	int/float with int/float -> float if either is float, else int 
*	string with string	-> string (joins them together)
* - * / %	int/float with int/float -> float if either is float, else int
* < <= > >=	int/float with int/float -> bool
* == !=	two numbers, two strings or two bools -> bool 
* && ||	bool with bool -> bool
* - (prefix) int -> int, float -> float 
* ! (prefix) bool -> bool
*
* ERROR is accepted everywhere and produces ERROR, so a mistake that was * already reported does not cause a second message further up the tree. *
* Owner: Kessina Alex (0160) 
*/
public final class TypeRules {

private TypeRules() { }

/**
* The result type of left op right, or null if the operator cannot * be used with those two types.
*/
public static Type binary(TokenType operator, Type left, Type right) { if (left.isError() || right.isError()) {
return Type.ERROR; }
switch (operator) { case PLUS:
if (left == Type.STRING && right == Type.STRING) { return Type.STRING;
}
return arithmetic(left, right);

case MINUS: case STAR: case SLASH: case PERCENT:
return arithmetic(left, right);

case LESS:
case LESS_EQUAL: case GREATER:
case GREATER_EQUAL:
return (left.isNumeric() && right.isNumeric()) ? Type.BOOL : null;

case EQUAL_EQUAL: case NOT_EQUAL:
if (left.isNumeric() && right.isNumeric()) { return Type.BOOL;
}
return (left == right) ? Type.BOOL : null;

case AND: case OR:
return (left == Type.BOOL && right == Type.BOOL) ? Type.BOOL : null;

default:
return null; }
}

/**
* The result type of op operand, or null if the operator cannot be 
* used with that type.

*/
public static Type unary(TokenType operator, Type operand) { if (operand.isError()) {
return Type.ERROR; }
if (operator == TokenType.MINUS) {
return operand.isNumeric() ? operand : null; }
if (operator == TokenType.NOT) {
return operand == Type.BOOL ? Type.BOOL : null; }
return null; }

/**
* True if a value of type 'value' may be stored in a variable declared
* as type 'target'. A float is NOT accepted by an int variable, because 
* that would silently throw the fraction away.
*/
public static boolean canAssign(Type target, Type value) { if (target.isError() || value.isError()) {
return true; // already reported; stay quiet }
return target == value; }

/** int with int gives int; anything else numeric gives float. 
*/ 
private static Type arithmetic(Type left, Type right) {
if (!left.isNumeric() || !right.isNumeric()) { return null;
}
return (left == Type.FLOAT || right == Type.FLOAT) ? Type.FLOAT : Type.INT; 
}
}
