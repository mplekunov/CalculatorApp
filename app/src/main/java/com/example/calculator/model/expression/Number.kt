package com.example.calculator.model.expression

import com.example.calculator.miscellaneous.Numbers

class Number() {
    var valueAsTokens: MutableList<Numbers.Kind> = mutableListOf()
    var type: Numbers.Kind = Numbers.Kind.INTEGER

    constructor(number: Numbers.Kind) : this() {
        valueAsTokens.add(number)

        if (number == Numbers.Kind.DOT)
            type = Numbers.Kind.DOT
        else if (number == Numbers.Kind.INFINITY)
            type = Numbers.Kind.INFINITY
    }
}
