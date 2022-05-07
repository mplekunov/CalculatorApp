package com.example.calculator.model

import com.example.calculator.miscellaneous.Associativity
import com.example.calculator.miscellaneous.Functions
import com.example.calculator.miscellaneous.TokenTypes

class Function(
    override var value: String,
    override var type: Functions,
    val associativity: Associativity = Associativity.RIGHT,
) : Token {
        companion object Factory {
            fun parseFunction(function : Functions) : Function? {
                return when (function) {
                    Functions.PERCENTAGE -> Function("%", Functions.PERCENTAGE)
                    Functions.LOG -> Function("log", Functions.LOG)
                    Functions.NATURAL_LOG -> Function("ln", Functions.NATURAL_LOG)
                    Functions.COS -> Function("cos", Functions.COS)
                    Functions.SIN -> Function("sin", Functions.SIN)
                    else -> null
                }
            }


            fun parseToken(token: Token) : Function? {
                return when(token.value) {
                    "%" -> parseFunction(Functions.PERCENTAGE)
                    "log" -> parseFunction(Functions.LOG)
                    else -> null
                }
            }
        }
}
