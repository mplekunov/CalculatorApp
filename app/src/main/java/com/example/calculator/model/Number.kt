package com.example.calculator.model

import com.example.calculator.miscellaneous.Numbers

class Number() {
    val valueAsTokens: MutableList<Numbers> = mutableListOf()
    var type: Numbers = Numbers.INTEGER

    constructor(number: Numbers) : this() {
        valueAsTokens.add(number)

        if (number == Numbers.DOT)
            type = Numbers.DOT
    }
}
