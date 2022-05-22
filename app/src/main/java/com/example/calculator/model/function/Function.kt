package com.example.calculator.model.function

import com.example.calculator.model.token.Token
import com.example.calculator.model.token.TokenTypes

class Function: Token {
    var functionBody: FunctionBody = FunctionBody.NONE

    constructor(value: String) : super(value, TokenTypes.Function)

    constructor(value: String, functionBody: FunctionBody) : this(value) {
        this.functionBody = functionBody
    }
}