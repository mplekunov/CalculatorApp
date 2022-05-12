package com.example.calculator.miscellaneous

import com.example.calculator.databinding.FragmentCalculatorBinding


class Operators(binding: FragmentCalculatorBinding?) {
    enum class Kind {
        ADDITION,
        SUBTRACTION,
        DIVISION,
        MULTIPLICATION,
        POWER,
        LEFT_BRACKET,
        RIGHT_BRACKET
    }

    val operatorButtons = mapOf(
    binding?.additionSign to Kind.ADDITION,
    binding?.subtractionSign to Kind.SUBTRACTION,
    binding?.divisionSign to Kind.DIVISION,
    binding?.multiplicationSign to Kind.MULTIPLICATION
    )
}