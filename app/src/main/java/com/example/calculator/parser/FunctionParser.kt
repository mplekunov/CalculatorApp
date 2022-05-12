package com.example.calculator.parser

import com.example.calculator.datastructure.BiMap
import com.example.calculator.miscellaneous.Functions
import com.example.calculator.miscellaneous.TokenTypes

import com.example.calculator.model.expression.Function
import com.example.calculator.model.expression.Token

class FunctionParser : TokenParser<Function, String, Functions.Kind> {
    override val TokenParser<Function, String, Functions.Kind>.map: BiMap<String, Functions.Kind>
        get() = BiMap<String, Functions.Kind>().apply { putAll(mutableMapOf(
            "%" to Functions.Kind.PERCENTAGE,
            "log" to Functions.Kind.LOG,
            "ln" to Functions.Kind.NATURAL_LOG,
            "cos" to Functions.Kind.COS,
            "sin" to Functions.Kind.SIN
        )) }

    private val functionsMap = mutableMapOf(
        Functions.Kind.PERCENTAGE to Function(Functions.Kind.PERCENTAGE),
        Functions.Kind.LOG to Function(Functions.Kind.LOG),
        Functions.Kind.NATURAL_LOG to Function(Functions.Kind.NATURAL_LOG),
        Functions.Kind.COS to Function(Functions.Kind.COS),
        Functions.Kind.SIN to Function(Functions.Kind.SIN)
    )

    override fun parse(input: Function): Token {
        val token = object : Token {
            override var value: String = ""
            override val type: TokenTypes = TokenTypes.Function
        }

        token.value += map[input.type]

        return token
    }

    fun parse(input: Token): Function {
        return functionsMap[map[input.value]]!!
    }
}