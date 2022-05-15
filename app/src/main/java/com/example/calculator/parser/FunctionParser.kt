package com.example.calculator.parser

import com.example.calculator.datastructure.BiMap
import com.example.calculator.model.function.Function
import com.example.calculator.model.token.TokenTypes

import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.token.Token

object FunctionParser : TokenParser<FunctionKind> {
    override val TokenParser<FunctionKind>.map: BiMap<String, FunctionKind>
        get() = BiMap<String, FunctionKind>().apply { putAll(mutableMapOf(
            "%" to FunctionKind.PERCENTAGE,
            "log" to FunctionKind.LOG,
            "ln" to FunctionKind.NATURAL_LOG,
            "cos" to FunctionKind.COS,
            "sin" to FunctionKind.SIN
        )) }

    private val functionsMap = mutableMapOf(
        FunctionKind.PERCENTAGE to Function(map[FunctionKind.PERCENTAGE]!!),
        FunctionKind.LOG to Function(map[FunctionKind.LOG]!!),
        FunctionKind.NATURAL_LOG to Function(map[FunctionKind.NATURAL_LOG]!!),
        FunctionKind.COS to Function(map[FunctionKind.COS]!!),
        FunctionKind.SIN to Function(map[FunctionKind.SIN]!!)
    )

    override fun parse(input: FunctionKind): Function {
        return Function(map[input] ?: throw NoSuchElementException("Function doesn't exist"))
    }

    fun parse(token: Token) : Function = functionsMap[map[token.value]] ?: throw NoSuchElementException("Function doesn't exist")
}