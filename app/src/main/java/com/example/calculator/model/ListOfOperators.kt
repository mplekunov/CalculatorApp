package com.example.calculator.model

enum class OPERATORS {
    ADDITION,
    MULTIPLICATION,
    SUBTRACTION,
    DIVISION,
    PERCENTAGE,
    POWER,
    LEFT_BRACKET,
    RIGHT_BRACKET
}

enum class ASSOCIATIVITY {
    LEFT,
    RIGHT,
    NONE
}

val operatorMap = hashMapOf(
    "+" to Operator("+", ASSOCIATIVITY.LEFT, 0),
    "(" to Operator("(", ASSOCIATIVITY.NONE, -1),
    ")" to Operator(")", ASSOCIATIVITY.NONE, -1),
    "^" to Operator("^", ASSOCIATIVITY.RIGHT, 10),
    "*" to Operator("*", ASSOCIATIVITY.LEFT, 5),
    "-" to Operator("-", ASSOCIATIVITY.LEFT, 0),
    "/" to Operator("/", ASSOCIATIVITY.LEFT, 5),
    "%" to Operator("%", ASSOCIATIVITY.LEFT, -1)
)

fun getOperator(op: OPERATORS): Operator? {
    return when {
        op == OPERATORS.ADDITION -> operatorMap["+"]
        op == OPERATORS.SUBTRACTION -> operatorMap["-"]
        op == OPERATORS.DIVISION -> operatorMap["/"]
        op == OPERATORS.PERCENTAGE -> operatorMap["%"]
        op == OPERATORS.POWER -> operatorMap["^"]
        op == OPERATORS.LEFT_BRACKET -> operatorMap["("]
        op == OPERATORS.MULTIPLICATION -> operatorMap["*"]
        op == OPERATORS.RIGHT_BRACKET -> operatorMap[")"]
        else -> null
    }
}
