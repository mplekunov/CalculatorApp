package com.example.calculator.model.expression

import com.example.calculator.miscellaneous.Associativity
import com.example.calculator.miscellaneous.Operators

class Operator(
    val type: Operators.Kind
    ) {
    var associativity: Associativity = Associativity.NONE
    var precedence: Int = 0

    constructor(type: Operators.Kind, associativity: Associativity, precedence: Int) : this(type) {
        this.associativity = associativity
        this.precedence = precedence
    }
}