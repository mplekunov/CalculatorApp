package com.example.calculator.model

enum class Operator {
    ADDITION("+", ASSOCIATIVITY.LEFT, 0),
    MULTIPLICATION("*", ASSOCIATIVITY.LEFT, 5),
    SUBTRACTION("-", ASSOCIATIVITY.LEFT, 0),
    DIVISION("/", ASSOCIATIVITY.LEFT, 5),
    PERCENTAGE("%", ASSOCIATIVITY.NONE, -1),
    POWER("^", ASSOCIATIVITY.RIGHT, 10),
    LEFT_BRACKET("(", ASSOCIATIVITY.NONE, -1),
    RIGHT_BRACKET(")", ASSOCIATIVITY.NONE, -1),
    DOT(".", ASSOCIATIVITY.NONE, -1);

    enum class ASSOCIATIVITY {
        LEFT,
        RIGHT,
        NONE
    }

    var operator: String = ""
    var associativity: ASSOCIATIVITY = ASSOCIATIVITY.NONE
    var precedence: Int = -1

    constructor()

    constructor(operator: String, associativity: ASSOCIATIVITY, precedence: Int) {
        this.operator = operator
        this.associativity = associativity
        this.precedence = precedence
    }

    override fun toString(): String = operator
}

inline fun <reified T: Enum<T>> contains(operator: String): Boolean {
    return enumValues<T>().any { it.toString() == operator }
}

inline fun <reified T: Enum<T>> getValue(operator: String): T? {
    return enumValues<T>().firstOrNull { it.toString() == operator }
}