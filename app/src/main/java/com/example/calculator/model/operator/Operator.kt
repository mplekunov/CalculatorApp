package com.example.calculator.model.operator

import com.example.calculator.model.token.Token
import com.example.calculator.model.token.TokenTypes

class Operator: Token {
    var associativity: Associativity = Associativity.NONE
    var precedence: Int = 0

    constructor(value: String) : super(value, TokenTypes.Operator)

    constructor(value: String, associativity: Associativity, precedence: Int) : this(value) {
        this.associativity = associativity
        this.precedence = precedence
    }
}