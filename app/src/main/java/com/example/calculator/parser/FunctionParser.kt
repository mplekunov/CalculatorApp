package com.example.calculator.parser

import com.example.calculator.datastructure.BiMap
import com.example.calculator.model.function.Function

import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.token.Token
import com.example.calculator.model.token.TokenTypes

object FunctionParser : TokenParser<FunctionKind> {
    override val TokenParser<FunctionKind>.map: BiMap<Token, FunctionKind>
        get() = BiMap<Token, FunctionKind>().apply { putAll(mutableMapOf(
            Token("%", TokenTypes.Function) to FunctionKind.PERCENTAGE,
            Token("log", TokenTypes.Function) to FunctionKind.LOG,
            Token("ln", TokenTypes.Function) to FunctionKind.NATURAL_LOG,
            Token("cos", TokenTypes.Function) to FunctionKind.COS,
            Token("sin", TokenTypes.Function) to FunctionKind.SIN
        )) }

    override fun parse(input: FunctionKind): Token {
        return map[input] ?: throw NoSuchElementException("Function doesn't exist")
    }
}