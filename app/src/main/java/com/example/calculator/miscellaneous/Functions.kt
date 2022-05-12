package com.example.calculator.miscellaneous

import com.example.calculator.databinding.FragmentCalculatorBinding

class Functions(binding : FragmentCalculatorBinding?) {
    enum class Kind {
        PERCENTAGE,
        LOG,
        NATURAL_LOG,
        FACTORIAL,
        SIN,
        COS,
        TAN,
        SQUARE_ROOT
    }

    val functionButtons = mapOf (
        binding?.percentSign to Kind.PERCENTAGE
    )
}