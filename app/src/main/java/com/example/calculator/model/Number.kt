package com.example.calculator.model

import com.example.calculator.miscellaneous.Numbers
import com.example.calculator.miscellaneous.TokenTypes

class Number (
    override var value: String,
    val valueAsTokens: MutableList<Numbers>
    ) : Token {
        companion object Factory {
            fun parseNumber(number: Numbers) : Number? {
                return when(number) {
                    Numbers.ZERO -> Number("0", mutableListOf(Numbers.ZERO))
                    Numbers.ONE -> Number("1", mutableListOf(Numbers.ONE))
                    Numbers.TWO -> Number("2", mutableListOf(Numbers.TWO))
                    Numbers.THREE -> Number("3", mutableListOf(Numbers.THREE))
                    Numbers.FOUR-> Number("4", mutableListOf(Numbers.FOUR))
                    Numbers.FIVE -> Number("5", mutableListOf(Numbers.FIVE))
                    Numbers.SIX -> Number("6", mutableListOf(Numbers.SIX))
                    Numbers.SEVEN -> Number("7", mutableListOf(Numbers.SEVEN))
                    Numbers.EIGHT -> Number("8", mutableListOf(Numbers.EIGHT))
                    Numbers.NINE -> Number("9", mutableListOf(Numbers.NINE))
                    else -> null
                }
            }

            fun parseToken(token: Token) : Number {
                val number = Number(token.value, mutableListOf())

                for (num in token.value)
                    number.valueAsTokens.add(when (num) {
                        '0' -> Numbers.ZERO
                        '1' -> Numbers.ONE
                        '2' -> Numbers.TWO
                        '3' -> Numbers.THREE
                        '4' -> Numbers.FOUR
                        '5' -> Numbers.FIVE
                        '6' -> Numbers.SIX
                        '7' -> Numbers.SEVEN
                        '8' -> Numbers.EIGHT
                        '9' -> Numbers.NINE
                        else -> Numbers.DOT
                    })

                return number
            }
        }

        override val type = TokenTypes.Number

    }