package com.example.calculator.model.expression

import com.example.calculator.datastructure.BigNumber
import com.example.calculator.model.postfix.PostfixEvaluator
import com.example.calculator.model.token.TokenTypes

import com.example.calculator.model.number.NumberKind
import com.example.calculator.model.operator.Operator
import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.model.token.Token
import com.example.calculator.parser.NumberParser
import com.example.calculator.parser.OperatorParser
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.math.pow

/**
 * Helper class that evaluates [Expression].
 */
class ExpressionEvaluator(private val postfixEvaluator: PostfixEvaluator) {
    var result: Token

    init {
        result = calculateResult()
    }


    /**
     * Checks if [token] is a non-applicable-number
     */
    private fun isError(token: Token): Boolean = token == NumberParser.parse(NumberKind.NAN)

    /**
     * Computes result of a mathematical expression.
     *
     * @param expression as a collection of [Token].
     * @return [Token] containing result of computation.
     */
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
                // If stack is empty, we can't evaluate expression
                if (s.isEmpty())
                    break

                // All operators require 2 operands, therefore if we don't have two operands in our stack
                // We can't calculate the result of an expression
                var left = BigNumber.ZERO
                val right = BigNumber(s.pop().toString())

                if (s.isNotEmpty())
                    left = BigNumber(s.pop().toString())


                var result: BigNumber

                try {
                     result = when (OperatorParser.parse(token) as Operator) {
                        OperatorParser.parse(OperatorKind.ADDITION) -> addition(left, right)
                        OperatorParser.parse(OperatorKind.SUBTRACTION) -> subtraction(left, right)
                        OperatorParser.parse(OperatorKind.MULTIPLICATION) -> multiplication(
                            left,
                            right
                        )
                        OperatorParser.parse(OperatorKind.DIVISION) -> {
                            if (right == BigNumber.ZERO)
                                return NumberParser.parse(NumberKind.NAN)

                            division(left, right)
                        }

                        OperatorParser.parse(OperatorKind.POWER) -> power(left, right)
                        else -> TODO("Not Implemented Yet")
                    }
                } catch (e: NumberFormatException) {
                    return NumberParser.parse(NumberKind.NAN)
                }


                s.push(numberToToken(result))
            }
        }

        return if (s.isEmpty())
            NumberParser.parse(NumberKind.ZERO)
        else
            s.pop()
    }

    private fun numberToToken(number: BigNumber) : Token = Token(number.toString(), TokenTypes.Number)

    private fun addition(left: BigNumber, right: BigNumber): BigNumber = left.plus(right)
    private fun subtraction(left: BigNumber, right: BigNumber): BigNumber = left.minus(right)
    private fun multiplication(left: BigNumber, right: BigNumber): BigNumber = left.times(right)
    private fun division(left: BigNumber, right: BigNumber): BigNumber = left.div(right)
    private fun power(left: BigNumber, right: BigNumber): BigNumber = left.pow(right)
}