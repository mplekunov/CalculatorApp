package com.example.calculator.model

import com.example.calculator.miscellaneous.Associativity
import com.example.calculator.miscellaneous.Numbers
import com.example.calculator.miscellaneous.Operators
import com.example.calculator.miscellaneous.TokenTypes

class Operator (
    override var value: String,
    val subType: Operators,
    val associativity: Associativity,
    val precedence: Int,
    override val type: TokenTypes = TokenTypes.Operator,
    ) : Token {
        companion object Factory {
            fun parseOperator(operator : Operators) : Operator? {
                return when(operator) {
                    Operators.ADDITION -> Operator("+", Operators.ADDITION, Associativity.LEFT, 0)
                    Operators.SUBTRACTION -> Operator("-", Operators.SUBTRACTION, Associativity.LEFT, 0)
                    Operators.MULTIPLICATION -> Operator("*", Operators.MULTIPLICATION, Associativity.LEFT, 5)
                    Operators.DIVISION -> Operator("/", Operators.DIVISION, Associativity.LEFT, 5)
                    Operators.POWER -> Operator("^", Operators.POWER, Associativity.RIGHT, 10)
                    else -> null
                }
            }

            fun parseToken(token: Token) : Operator? {
                return when(token.value) {
                    "+" -> parseOperator(Operators.ADDITION)
                    "-" -> parseOperator(Operators.SUBTRACTION)
                    "*" -> parseOperator(Operators.MULTIPLICATION)
                    "/" -> parseOperator(Operators.DIVISION)
                    else -> null
                }
            }
        }
}
