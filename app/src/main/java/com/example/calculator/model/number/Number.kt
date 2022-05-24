package com.example.calculator.model.number

import com.example.calculator.model.token.Token
import com.example.calculator.model.token.TokenTypes

class Number(value: String) : Token(value, TokenTypes.Number) {
    var isConstant: Boolean = false

    constructor(value: String, isConstant: Boolean) : this(value) {
        this.isConstant = isConstant
    }
}
