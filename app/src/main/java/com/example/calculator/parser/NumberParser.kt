package com.example.calculator.parser

import com.example.calculator.datastructure.BiMap
import com.example.calculator.model.number.Number

import com.example.calculator.model.number.NumberKind
import com.example.calculator.model.token.Token

object NumberParser : TokenParser<NumberKind> {
    override val TokenParser<NumberKind>.map: BiMap<String, NumberKind>
        get() = BiMap<String, NumberKind>().apply { putAll(mutableListOf(
            "0" to NumberKind.ZERO,
            "1" to NumberKind.ONE,
            "2" to NumberKind.TWO,
            "3" to NumberKind.THREE,
            "4" to NumberKind.FOUR,
            "5" to NumberKind.FIVE,
            "6" to NumberKind.SIX,
            "7" to NumberKind.SEVEN,
            "8" to NumberKind.EIGHT,
            "9" to NumberKind.NINE,
            "." to NumberKind.DOT,
            "E" to NumberKind.EXPONENT,
            "-" to NumberKind.NEGATIVE,
            "+" to NumberKind.POSITIVE,
            Double.POSITIVE_INFINITY.toString() to NumberKind.INFINITY
        )) }

    override fun parse(input: NumberKind): Number {
        return Number(map[input] ?: throw NoSuchElementException("Number doesn't exist"))
    }

    fun parse(token: Token) : Number = Number(token.value)
}