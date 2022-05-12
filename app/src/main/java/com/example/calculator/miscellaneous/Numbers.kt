package com.example.calculator.miscellaneous

import com.example.calculator.databinding.FragmentCalculatorBinding

class Numbers(binding: FragmentCalculatorBinding?) {
    enum class Kind {
        ZERO,
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE,
        DOT,
        INTEGER,
        FLOAT,
        INFINITY,
        NEGATIVE,
        EXPONENT,
        POSITIVE
    }

    val numberButtons = mapOf(
        binding?.number0 to Kind.ZERO,
        binding?.number1 to Kind.ONE,
        binding?.number2 to Kind.TWO,
        binding?.number3 to Kind.THREE,
        binding?.number4 to Kind.FOUR,
        binding?.number5 to Kind.FIVE,
        binding?.number6 to Kind.SIX,
        binding?.number7 to Kind.SEVEN,
        binding?.number8 to Kind.EIGHT,
        binding?.number9 to Kind.NINE,
        binding?.dotSign to Kind.DOT
    )
}