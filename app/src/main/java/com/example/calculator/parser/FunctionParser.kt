package com.example.calculator.parser

import com.example.calculator.datastructure.BiMap
import com.example.calculator.miscellaneous.Functions
import com.example.calculator.miscellaneous.TokenTypes

import com.example.calculator.model.Function
import com.example.calculator.model.Token

class FunctionParser : TokenParser<Function, String, Functions> {
    override val TokenParser<Function, String, Functions>.map: BiMap<String, Functions>
        get() = BiMap<String, Functions>().apply { putAll(mutableMapOf(
            "%" to Functions.PERCENTAGE,
            "log" to Functions.LOG,
            "ln" to Functions.NATURAL_LOG,
            "cos" to Functions.COS,
            "sin" to Functions.SIN
        )) }

    private val functionsMap = mutableMapOf(
        Functions.PERCENTAGE to Function(Functions.PERCENTAGE),
        Functions.LOG to Function(Functions.LOG),
        Functions.NATURAL_LOG to Function(Functions.NATURAL_LOG),
        Functions.COS to Function(Functions.COS),
        Functions.SIN to Function(Functions.SIN)
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