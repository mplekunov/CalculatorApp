package com.example.calculator.model.expression

import com.example.calculator.model.token.TokenTypes

import com.example.calculator.model.number.Number
import com.example.calculator.model.number.NumberKind
import com.example.calculator.model.operator.Operator
import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.model.token.Token
import com.example.calculator.parser.NumberParser
import com.example.calculator.parser.OperatorParser
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

/**
 * Helper class that evaluates [Expression].
 */
class ExpressionEvaluator(private val postfixEvaluator: PostfixEvaluator) {
    var result: Token

    init {
        result = calculateResult()
    }
    /**
     * Computes result of a mathematical expression.
     *
     * @param expression as a collection of [Token].
     * @return [Token] containing result of computation.
     */

    private fun isError(token: Token): Boolean {
        return token == NumberParser.parse(NumberKind.NAN)

    }
    private fun calculateResult() : Token {
        val postfix = postfixEvaluator.postfix

        val s = Stack<Token>()

        for (i in postfix.indices) {
            val token = postfix[i]

            if (isError(token))
                return NumberParser.parse(NumberKind.NAN)

            if (token.type == TokenTypes.Number)
                s.push(token)
            else if (token.type == TokenTypes.Operator) {
                // All operators require 2 operands, therefore if we don't have two operands in our stack
                // We can't calculate the result of an expression
                if (s.isEmpty())
                    break

                val right = BigDecimal(NumberParser.parse<Number>(s.pop()).toString()).setScale(10, RoundingMode.HALF_UP)
                val left = BigDecimal(NumberParser.parse<Number>(s.pop()).toString()).setScale(10, RoundingMode.HALF_UP)

                val result = when(OperatorParser.parse(token) as Operator) {
                    OperatorParser.parse(OperatorKind.ADDITION) -> addition(left, right)
                    OperatorParser.parse(OperatorKind.SUBTRACTION) -> subtraction(left, right)
                    OperatorParser.parse(OperatorKind.MULTIPLICATION) -> multiplication(left, right)
                    OperatorParser.parse(OperatorKind.DIVISION) -> {
                        if (right.toDouble() == 0.0)
                            return NumberParser.parse(NumberKind.NAN)

                        division(left, right)
                    }
                    OperatorParser.parse(OperatorKind.POWER) -> power(left, right)
                    else -> TODO("Not Implemented Yet")
                }

                s.push(numberToToken(result))
            }
        }

        return if (s.isEmpty())
            NumberParser.parse(NumberKind.ZERO)
        else
            s.pop()
    }

    private fun numberToToken(number: BigDecimal) : Token = Token(number.toPlainString(), TokenTypes.Number)

    private fun addition(left: BigDecimal, right: BigDecimal): BigDecimal = left.plus(right).stripTrailingZeros()
    private fun subtraction(left: BigDecimal, right: BigDecimal): BigDecimal = left.minus(right).stripTrailingZeros()
    private fun multiplication(left: BigDecimal, right: BigDecimal): BigDecimal = left.times(right).stripTrailingZeros()
    private fun division(left: BigDecimal, right: BigDecimal): BigDecimal = left.div(right).stripTrailingZeros()
    private fun power(left: BigDecimal, right: BigDecimal): BigDecimal = left.pow(right.toInt()).stripTrailingZeros()
}