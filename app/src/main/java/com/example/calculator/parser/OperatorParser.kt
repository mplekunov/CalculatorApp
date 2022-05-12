package com.example.calculator.parser

import com.example.calculator.datastructure.BiMap

import com.example.calculator.miscellaneous.Associativity
import com.example.calculator.miscellaneous.Operators
import com.example.calculator.miscellaneous.TokenTypes

import com.example.calculator.model.Operator
import com.example.calculator.model.Token

class OperatorParser: TokenParser<Operator, String, Operators.Kind> {
    override val TokenParser<Operator, String, Operators.Kind>.map: BiMap<String, Operators.Kind>
        get() = BiMap<String, Operators.Kind>().apply { putAll(mutableMapOf(
            "+" to Operators.Kind.ADDITION,
            "-" to Operators.Kind.SUBTRACTION,
            "/" to Operators.Kind.DIVISION,
            "*" to Operators.Kind.MULTIPLICATION,
            "^" to Operators.Kind.POWER
        )) }

    private val operatorsMap = mutableMapOf(
        Operators.Kind.ADDITION to Operator(Operators.Kind.ADDITION, Associativity.LEFT, 0),
        Operators.Kind.SUBTRACTION to Operator(Operators.Kind.SUBTRACTION, Associativity.LEFT, 0),
        Operators.Kind.MULTIPLICATION to Operator(Operators.Kind.MULTIPLICATION, Associativity.LEFT, 5),
        Operators.Kind.DIVISION to Operator(Operators.Kind.DIVISION, Associativity.LEFT, 5),
        Operators.Kind.POWER to Operator(Operators.Kind.POWER, Associativity.RIGHT, 10)
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