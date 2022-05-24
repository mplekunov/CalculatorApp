package com.example.calculator.parser

import com.example.calculator.datastructure.BiMap
import com.example.calculator.model.number.Number

import com.example.calculator.model.number.NumberKind
import com.example.calculator.model.token.Token
import com.example.calculator.model.token.TokenTypes

object NumberParser : TokenParser<NumberKind> {
    override val TokenParser<NumberKind>.map: BiMap<Token, NumberKind>
        get() = BiMap<Token, NumberKind>().apply { putAll(mutableListOf(
            Token("0", TokenTypes.Number) to NumberKind.ZERO,
            Token("1", TokenTypes.Number) to NumberKind.ONE,
            Token("2", TokenTypes.Number) to NumberKind.TWO,
            Token("3", TokenTypes.Number) to NumberKind.THREE,
            Token("4", TokenTypes.Number) to NumberKind.FOUR,
            Token("5", TokenTypes.Number) to NumberKind.FIVE,
            Token("6", TokenTypes.Number) to NumberKind.SIX,
            Token("7", TokenTypes.Number) to NumberKind.SEVEN,
            Token("8", TokenTypes.Number) to NumberKind.EIGHT,
            Token("9", TokenTypes.Number) to NumberKind.NINE,
            Token(".", TokenTypes.Number) to NumberKind.DOT,
            Token("E", TokenTypes.Number) to NumberKind.EXPONENT,
            Token("-", TokenTypes.Number) to NumberKind.NEGATIVE,
            Token(Double.POSITIVE_INFINITY.toString(), TokenTypes.Number) to NumberKind.INFINITY,
            Token("NaN", TokenTypes.Number) to NumberKind.NAN,
            Token(Math.PI.toString(), TokenTypes.Number) to NumberKind.PI,
            Token(Math.E.toString(), TokenTypes.Number) to NumberKind.EPSILON
        )) }

    override fun parse(input: NumberKind): Token {
        return map[input] ?: throw NoSuchElementException("Number doesn't exist")
    }

    inline fun <reified T> parse(token: Token): T {
        return when (T::class.java) {
            NumberKind::class.java -> map[token] as T
                ?: throw NoSuchElementException("Operator doesn't exist")
            Number::class.java -> {
                if (map[token] == NumberKind.INFINITY )
                    return Number(Double.POSITIVE_INFINITY.toString()) as T
                else if (map[token] == NumberKind.NAN)
                    return Number(map[token].toString()) as T

                return Number(token.toString()) as T
            }
            else -> throw Exception("Wrong return Type")
        }
    }
}