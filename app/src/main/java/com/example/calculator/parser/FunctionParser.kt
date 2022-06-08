package com.example.calculator.parser

import com.example.calculator.datastructure.BiMap
import com.example.calculator.model.function.Function
import com.example.calculator.model.function.FunctionBody

import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.token.Token
import com.example.calculator.model.token.TokenTypes

object FunctionParser : TokenParser<FunctionKind> {
    override val TokenParser<FunctionKind>.map: BiMap<Token, FunctionKind>
        get() = BiMap<Token, FunctionKind>().apply { putAll(mutableMapOf(
            Token("%", TokenTypes.Function) to FunctionKind.PERCENTAGE,
            Token("log", TokenTypes.Function) to FunctionKind.LOG,
            Token("ln", TokenTypes.Function) to FunctionKind.NATURAL_LOG,
            Token("!", TokenTypes.Function) to FunctionKind.FACTORIAL,
            Token("^2", TokenTypes.Function) to FunctionKind.SQUARED,
            Token("v", TokenTypes.Function) to FunctionKind.SQUARE_ROOT,
        )) }

    @PublishedApi
    internal val functionMap = mutableMapOf(
        FunctionKind.NATURAL_LOG to Function(map[FunctionKind.NATURAL_LOG].toString(), FunctionBody.RIGHT_SIDE),
        FunctionKind.LOG to Function(map[FunctionKind.LOG].toString(), FunctionBody.RIGHT_SIDE),
        FunctionKind.SQUARE_ROOT to Function(map[FunctionKind.SQUARE_ROOT].toString(), FunctionBody.RIGHT_SIDE),
        FunctionKind.PERCENTAGE to Function(map[FunctionKind.PERCENTAGE].toString(), FunctionBody.LEFT_SIDE),
        FunctionKind.FACTORIAL to Function(map[FunctionKind.FACTORIAL].toString(), FunctionBody.LEFT_SIDE),
        FunctionKind.SQUARED to Function(map[FunctionKind.SQUARED].toString(), FunctionBody.LEFT_SIDE),

    )

    override fun parse(input: FunctionKind): Token {
        return map[input] ?: throw NoSuchElementException("Function doesn't exist")
    }

    inline fun <reified T> parse(token: Token): T {
        return when(T::class.java) {
            FunctionKind::class.java -> map[token] as T ?: throw NoSuchElementException("Function doesn't exist")
            Function::class.java -> functionMap[map[token]] as T ?: throw NoSuchElementException("Function doesn't exist")
            else -> throw Exception("Wrong return Type")
        }
    }
}