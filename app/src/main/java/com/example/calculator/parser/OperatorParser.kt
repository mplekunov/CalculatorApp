package com.example.calculator.parser

import com.example.calculator.datastructure.BiMap

import com.example.calculator.miscellaneous.Associativity
import com.example.calculator.miscellaneous.Operators
import com.example.calculator.miscellaneous.TokenTypes

import com.example.calculator.model.Operator
import com.example.calculator.model.Token

class OperatorParser: TokenParser<Operator, String, Operators> {
    override val TokenParser<Operator, String, Operators>.map: BiMap<String, Operators>
        get() = BiMap<String, Operators>().apply { putAll(mutableMapOf(
            "+" to Operators.ADDITION,
            "-" to Operators.SUBTRACTION,
            "/" to Operators.DIVISION,
            "*" to Operators.MULTIPLICATION,
            "^" to Operators.POWER
        )) }

    private val operatorsMap = mutableMapOf(
        Operators.ADDITION to Operator(Operators.ADDITION, Associativity.LEFT, 0),
        Operators.SUBTRACTION to Operator(Operators.SUBTRACTION, Associativity.LEFT, 0),
        Operators.MULTIPLICATION to Operator(Operators.MULTIPLICATION, Associativity.LEFT, 5),
        Operators.DIVISION to Operator(Operators.DIVISION, Associativity.LEFT, 5),
        Operators.POWER to Operator(Operators.POWER, Associativity.RIGHT, 10)
    )

    override fun parse(input: Operator): Token {
        val token = object : Token {
            override var value: String = ""
            override val type: TokenTypes = TokenTypes.Operator
        }

        token.value = map[input.type]!!

        return token
    }

    fun parse(input: Token): Operator {
        return operatorsMap[map[input.value]]!!
    }
}