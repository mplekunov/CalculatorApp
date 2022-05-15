package com.example.calculator.parser

import android.graphics.Region
import com.example.calculator.datastructure.BiMap
import com.example.calculator.model.operator.Associativity
import com.example.calculator.model.operator.Operator

import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.model.token.Token
import kotlin.Exception

object OperatorParser: TokenParser<OperatorKind> {
    override val TokenParser<OperatorKind>.map: BiMap<String, OperatorKind>
        get() = BiMap<String, OperatorKind>().apply { putAll(mutableMapOf(
            "+" to OperatorKind.ADDITION,
            "-" to OperatorKind.SUBTRACTION,
            "/" to OperatorKind.DIVISION,
            "*" to OperatorKind.MULTIPLICATION,
            "^" to OperatorKind.POWER
        )) }

    @PublishedApi
    internal val operatorsMap = mutableMapOf(
        OperatorKind.ADDITION to Operator(map[OperatorKind.ADDITION]!!, Associativity.LEFT, 0),
        OperatorKind.SUBTRACTION to Operator(map[OperatorKind.SUBTRACTION]!!, Associativity.LEFT, 0),
        OperatorKind.MULTIPLICATION to Operator(map[OperatorKind.MULTIPLICATION]!!, Associativity.LEFT, 5),
        OperatorKind.DIVISION to Operator(map[OperatorKind.DIVISION]!!, Associativity.LEFT, 5),
        OperatorKind.POWER to Operator(map[OperatorKind.POWER]!!, Associativity.RIGHT, 10)
    )

    override fun parse(input: OperatorKind): Operator {
        return operatorsMap[input] ?: throw NoSuchElementException("Operator doesn't exist")
    }

    inline fun <reified T> parse(token: Token): T {
        return when(T::class.java) {
            OperatorKind::class.java -> map[token.value] as T ?: throw NoSuchElementException("Operator doesn't exist")
            Operator::class.java -> operatorsMap[map[token.value]] as T ?: throw NoSuchElementException("Operator doesn't exist")
            else -> throw Exception("Wrong return Type")
        }
    }
}