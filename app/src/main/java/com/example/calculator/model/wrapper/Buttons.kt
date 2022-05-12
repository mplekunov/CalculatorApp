package com.example.calculator.model.wrapper

import com.example.calculator.databinding.FragmentCalculatorBinding
import com.example.calculator.miscellaneous.Functions
import com.example.calculator.miscellaneous.Numbers
import com.example.calculator.miscellaneous.Operators

class Buttons(
    binding: FragmentCalculatorBinding
    ) {
    val functions = Functions(binding)
    val operators = Operators(binding)
    val numbers = Numbers(binding)
    val clear = binding.delete
    val clearAll = binding.deleteAll
    val equal = binding.equalSign
}