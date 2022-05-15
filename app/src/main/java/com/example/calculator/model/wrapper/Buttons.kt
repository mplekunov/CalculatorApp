package com.example.calculator.model.wrapper

import com.example.calculator.databinding.FragmentCalculatorBinding
import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.number.NumberKind
import com.example.calculator.model.operator.OperatorKind

class Buttons(
    binding: FragmentCalculatorBinding
    ) {
    val functions = mapOf(
        binding.percentSign to FunctionKind.PERCENTAGE
    )

    val operators = mapOf(
        binding.additionSign to OperatorKind.ADDITION,
        binding.subtractionSign to OperatorKind.SUBTRACTION,
        binding.multiplicationSign to OperatorKind.MULTIPLICATION,
        binding.divisionSign to OperatorKind.DIVISION
    )

    val numbers = mapOf(
        binding.number0 to NumberKind.ZERO,
        binding.number1 to NumberKind.ONE,
        binding.number2 to NumberKind.TWO,
        binding.number3 to NumberKind.THREE,
        binding.number4 to NumberKind.FOUR,
        binding.number5 to NumberKind.FIVE,
        binding.number6 to NumberKind.SIX,
        binding.number7 to NumberKind.SEVEN,
        binding.number8 to NumberKind.EIGHT,
        binding.number9 to NumberKind.NINE,
        binding.dotSign to NumberKind.DOT
    )
    val clear = binding.delete
    val clearAll = binding.deleteAll
    val equal = binding.equalSign
}