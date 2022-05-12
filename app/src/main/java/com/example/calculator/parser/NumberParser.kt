package com.example.calculator.parser

import com.example.calculator.datastructure.BiMap
import com.example.calculator.miscellaneous.Numbers
import com.example.calculator.miscellaneous.TokenTypes

import com.example.calculator.model.Token
import com.example.calculator.model.Number

class NumberParser : TokenParser<Number, String, Numbers.Kind> {
    override val TokenParser<Number, String, Numbers.Kind>.map: BiMap<String, Numbers.Kind>
        get() = BiMap<String, Numbers.Kind>().apply { putAll(mutableListOf(
            "0" to Numbers.Kind.ZERO,
            "1" to Numbers.Kind.ONE,
            "2" to Numbers.Kind.TWO,
            "3" to Numbers.Kind.THREE,
            "4" to Numbers.Kind.FOUR,
            "5" to Numbers.Kind.FIVE,
            "6" to Numbers.Kind.SIX,
            "7" to Numbers.Kind.SEVEN,
            "8" to Numbers.Kind.EIGHT,
            "9" to Numbers.Kind.NINE,
            "." to Numbers.Kind.DOT,
            "E" to Numbers.Kind.EXPONENT,
            "-" to Numbers.Kind.NEGATIVE,
            "+" to Numbers.Kind.POSITIVE,
            Double.POSITIVE_INFINITY.toString() to Numbers.Kind.INFINITY
        )) }

    override fun parse(input: Number): Token {
        val token = object : Token {
            override var value: String = ""
            override val type: TokenTypes = TokenTypes.Number
        }

        if (input.type != Numbers.Kind.INFINITY)
            input.valueAsTokens.forEach { number -> token.value += map[number] }
        else
            token.value = map[input.type]!!

        return token
    }

    fun parse(token: Token): Number {
        val number = Number()

        if (map.containsKey(token.value) && map[token.value] == Numbers.Kind.INFINITY)
            return Number(Numbers.Kind.INFINITY)

        token.value.forEach { ch ->
            run {
                number.valueAsTokens.add(map[ch.toString()]!!)
                if (map[ch.toString()]!! == Numbers.Kind.DOT)
                    number.type = Numbers.Kind.FLOAT
            }
        }

        return number
    }
}