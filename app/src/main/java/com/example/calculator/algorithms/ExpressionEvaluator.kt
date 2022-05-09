package com.example.calculator.algorithms

import com.example.calculator.miscellaneous.Associativity
import com.example.calculator.miscellaneous.Functions
import com.example.calculator.miscellaneous.Operators
import com.example.calculator.miscellaneous.TokenTypes

import com.example.calculator.model.*
import com.example.calculator.parser.FunctionParser
import com.example.calculator.parser.NumberParser
import com.example.calculator.parser.OperatorParser
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.NoSuchElementException

/**
 * Helper class that evaluates [Expression].
 */
object ExpressionEvaluator {
    private val operatorParser = OperatorParser()
    private val functionParser = FunctionParser()

    /**
     * Converts infix into Postfix.
     * @param infix representation of a mathematical expression.
     * @return postfix representation of a mathematical expression.
     */
    private fun infixToPostfix(expression: MutableList<Token>) : MutableList<Token> {
        val infix = mutableListOf<Token>(). apply { addAll(expression) }

        val postfix = mutableListOf<Token>()
        val opStack = Stack<Operator>()

        while (infix.isNotEmpty()){
            val token = infix.first()
            infix.removeFirst()

            // Operator or Function
            if (token.type == TokenTypes.Function) {
                if (functionParser.parse(token).type == Functions.PERCENTAGE) {
                    var percentage = BigDecimal(postfix.removeLast().value).setScale(10, RoundingMode.HALF_UP).div(BigDecimal(100.0).setScale(10, RoundingMode.HALF_UP))

                    if (postfix.isNotEmpty()) {
                        val lastKnownOperator = opStack.peek()
                        val lastKnownNumber = BigDecimal(postfix.last().value).setScale(10, RoundingMode.HALF_UP)

                        percentage =
                            if (lastKnownOperator.type == Operators.SUBTRACTION || lastKnownOperator.type == Operators.ADDITION)
                                percentage.times(lastKnownNumber)
                            else
                                percentage
                    }

                    expression.removeLast()
                    expression[expression.lastIndex] = numberToToken(percentage.stripTrailingZeros().toString())

                    postfix.add(numberToToken(percentage.stripTrailingZeros().toString()))
                }
            }
            else if (token.type == TokenTypes.Operator) {
                val operatorToken = operatorParser.parse(token)

                when (operatorToken.type) {
                    Operators.LEFT_BRACKET -> opStack.push(operatorToken)
                    Operators.RIGHT_BRACKET -> {
                        while (opStack.peek().type != Operators.LEFT_BRACKET)
                            postfix.add(operatorParser.parse(opStack.pop()))

                        opStack.pop()
                    }
                    else -> {
                        while (opStack.isNotEmpty() && isAssociativeRule(operatorToken, opStack.peek()))
                            postfix.add(operatorParser.parse(opStack.pop()))

                        opStack.push(operatorToken)
                    }
                }
            }
            else
                postfix.add(token)
        }

        while (opStack.isNotEmpty())
            postfix.add(operatorParser.parse(opStack.pop()))

        return postfix
    }

    /**
     * Helper function.
     * Checks for an associative rule between two Operators.
     *
     * @param x of type [Operator] representing information about first operator.
     * @param y of type [Operator] representing information about second operator.
     * @return [Boolean] indicating adherence to associative rule or the lack of.
     */
    private fun isAssociativeRule(x: Operator, y: Operator) : Boolean = isLeftRule(x, y) || isRightRule(x, y)

    /**
     * Helper function.
     * Checks for the left rule of [Operator].
     *
     * @param x of type [Operator] representing information about first operator.
     * @param y of type [Operator] representing information about second operator.
     */
    private fun isLeftRule(x: Operator, y: Operator): Boolean =
        x.associativity == Associativity.LEFT && x.precedence <= y.precedence
    /**
     * Helper function.
     * Checks for the right rule of [Operator].
     *
     * @param x of type [Operator] representing information about first operator.
     * @param y of type [Operator] representing information about second operator.
     */
    private fun isRightRule(x: Operator, y: Operator): Boolean =
        x.associativity == Associativity.RIGHT && x.precedence < y.precedence

    /**
     * Computes result of a mathematical expression.
     *
     * @param expression as a collection of [Token].
     * @return [Token] containing result of computation.
     */
    fun getResult(expression: MutableList<Token>): Token {
        if (expression.isNullOrEmpty())
            return object : Token {
                override var value = ""
                override val type = TokenTypes.Number
            }

        val postfix = infixToPostfix(expression)

        val s = Stack<Token>()

        for (i in postfix.indices) {
            val token = postfix[i]

            if (token.type == TokenTypes.Number)
                s.push(token)
            else if (token.type == TokenTypes.Operator){
                // All operators require 2 operands, therefore if we don't have two operands in our stack
                // We can't calculate the result of an expression
                if (s.size < 2)
                    break

                val operator = operatorParser.parse(token)

                val right = BigDecimal(s.pop().value).setScale(10, RoundingMode.HALF_UP)
                val left = BigDecimal(s.pop().value).setScale(10, RoundingMode.HALF_UP)

                val result = when(operator.type) {
                    Operators.ADDITION -> addition(left, right)
                    Operators.SUBTRACTION -> subtraction(left, right)
                    Operators.MULTIPLICATION -> multiplication(left, right)
                    Operators.DIVISION -> division(left, right)
                    Operators.POWER -> power(left, right)
                    else -> TODO("Not Implemented Yet")
                }

                s.push(numberToToken(result))
            }
        }

        return s.pop()
    }

    private fun numberToToken(number: String) : Token {
        return object : Token {
            override var value = number
            override val type = TokenTypes.Number
        }
    }

    private fun addition(left: BigDecimal, right: BigDecimal): String = left.plus(right).stripTrailingZeros().toString()
    private fun subtraction(left: BigDecimal, right: BigDecimal): String = left.minus(right).stripTrailingZeros().toString()
    private fun multiplication(left: BigDecimal, right: BigDecimal): String = left.times(right).stripTrailingZeros().toString()
    private fun division(left: BigDecimal, right: BigDecimal): String =
        if (right != BigDecimal.ZERO) left.div(right).stripTrailingZeros().toString() else ""
    private fun power(left: BigDecimal, right: BigDecimal): String = left.pow(right.toInt()).stripTrailingZeros().toString()
}