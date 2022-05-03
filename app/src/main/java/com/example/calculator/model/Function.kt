package com.example.calculator.model

enum class Function {
    PERCENTAGE("%", Operator.ASSOCIATIVITY.NONE, -1);

    enum class ASSOCIATIVITY {
        LEFT,
        RIGHT,
        NONE
    }

    var operator: String = ""
    var associativity: Operator.ASSOCIATIVITY = Operator.ASSOCIATIVITY.NONE
    var precedence: Int = -1

    constructor()

    constructor(operator: String, associativity: Operator.ASSOCIATIVITY, precedence: Int) {
        this.operator = operator
        this.associativity = associativity
        this.precedence = precedence
    }

    override fun toString(): String = operator
}