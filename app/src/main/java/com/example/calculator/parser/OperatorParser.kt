package com.example.calculator.parser

import com.example.calculator.datastructure.BiMap
import com.example.calculator.model.operator.Associativity
import com.example.calculator.model.operator.Operator

import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.model.token.Token
import com.example.calculator.model.token.TokenTypes
import kotlin.Exception

object OperatorParser: TokenParser<OperatorKind> {
    override val TokenParser<OperatorKind>.map: BiMap<Token, OperatorKind>
        get() = BiMap<Token, OperatorKind>().apply { putAll(mutableMapOf(
            Token("+", TokenTypes.Operator) to OperatorKind.ADDITION,
            Token("-", TokenTypes.Operator) to OperatorKind.SUBTRACTION,
            Token("/", TokenTypes.Operator) to OperatorKind.DIVISION,
            Token("*", TokenTypes.Operator) to OperatorKind.MULTIPLICATION,
            Token("^", TokenTypes.Operator) to OperatorKind.POWER
        )) }

    @PublishedApi
    internal val operatorsMap = mutableMapOf(
        OperatorKind.ADDITION to Operator(map[OperatorKind.ADDITION].toString(), Associativity.LEFT, 0),
        OperatorKind.SUBTRACTION to Operator(map[OperatorKind.SUBTRACTION].toString(), Associativity.LEFT, 0),
        OperatorKind.MULTIPLICATION to Operator(map[OperatorKind.MULTIPLICATION].toString(), Associativity.LEFT, 5),
        OperatorKind.DIVISION to Operator(map[OperatorKind.DIVISION].toString(), Associativity.LEFT, 5),
        OperatorKind.POWER to Operator(map[OperatorKind.POWER].toString(), Associativity.RIGHT, 10)
    )

    override fun parse(input: OperatorKind): Token {
        return map[input] ?: throw NoSuchElementException("Operator doesn't exist")
    }

    inline fun <reified T> parse(token: Token): T {
        return when(T::class.java) {
            OperatorKind::class.java -> map[token] as T ?: throw NoSuchElementException("Operator doesn't exist")
            Operator::class.java -> operatorsMap[map[token]] as T ?: throw NoSuchElementException("Operator doesn't exist")
            else -> throw Exception("Wrong return Type")
        }
    }
}