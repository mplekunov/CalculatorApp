package com.example.calculator.model

import com.example.calculator.miscellaneous.Numbers

class Number() {
    var valueAsTokens: MutableList<Numbers> = mutableListOf()
    var type: Numbers = Numbers.INTEGER

    constructor(number: Numbers) : this() {
        valueAsTokens.add(number)

        if (number == Numbers.DOT)
            type = Numbers.DOT
        else if (number == Numbers.INFINITY)
            type = Numbers.INFINITY
    }
}
