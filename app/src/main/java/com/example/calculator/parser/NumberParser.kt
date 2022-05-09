package com.example.calculator.parser

import com.example.calculator.datastructure.BiMap
import com.example.calculator.miscellaneous.Numbers
import com.example.calculator.miscellaneous.TokenTypes

import com.example.calculator.model.Token
import com.example.calculator.model.Number

class NumberParser : TokenParser<Number, String, Numbers> {
    override val TokenParser<Number, String, Numbers>.map: BiMap<String, Numbers>
        get() = BiMap<String, Numbers>().apply { putAll(mutableListOf(
            "0" to Numbers.ZERO,
            "1" to Numbers.ONE,
            "2" to Numbers.TWO,
            "3" to Numbers.THREE,
            "4" to Numbers.FOUR,
            "5" to Numbers.FIVE,
            "6" to Numbers.SIX,
            "7" to Numbers.SEVEN,
            "8" to Numbers.EIGHT,
            "9" to Numbers.NINE,
            "." to Numbers.DOT
        )) }

    override fun parse(input: Number): Token {
        val token = object : Token {
            override var value: String = ""
            override val type: TokenTypes = TokenTypes.Number
        }

        input.valueAsTokens.forEach { number -> token.value += map[number] }

        return token
    }

    fun parse(token: Token): Number {
        val number = Number()

        token.value.forEach { ch ->
            run {
                number.valueAsTokens.add(map[ch.toString()]!!)
                if (map[ch.toString()]!! == Numbers.DOT)
                    number.type = Numbers.FLOAT
            }
        }

        return number
    }
}