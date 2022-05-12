package com.example.calculator.algorithms

import com.example.calculator.miscellaneous.Associativity
import com.example.calculator.miscellaneous.Functions
import com.example.calculator.miscellaneous.Operators
import com.example.calculator.miscellaneous.TokenTypes

import com.example.calculator.model.*
import com.example.calculator.model.expression.Operator
import com.example.calculator.model.expression.Token
import com.example.calculator.parser.FunctionParser
import com.example.calculator.parser.OperatorParser
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

/**
 * Helper class that evaluates [Expression].
 */
object ExpressionEvaluator {
    private val operatorParser = OperatorParser()
    private val functionParser = FunctionParser()

    /**
     * Converts infix into Postfix.
     * @param [infix] representation of a mathematical expression.
     * @return [postfix] representation of a mathematical expression.
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
                if (functionParser.parse(token).type == Functions.Kind.PERCENTAGE) {
                    val first = BigDecimal(postfix.removeLast().value).setScale(10, RoundingMode.HALF_UP)
                    val second = BigDecimal(100.0).setScale(10, RoundingMode.HALF_UP)
                    var percentage = division(first, second)

                    if (postfix.isNotEmpty()) {
                        // Percentage bug
                        val lastKnownOperator = opStack.peek()

                        var last = postfix.last()
                        if (postfix.lastIndex - 1 >= 0 && last.type == TokenTypes.Operator)
                            last = postfix[postfix.lastIndex - 1]

                        val lastKnownNumber = BigDecimal(last.value).setScale(10, RoundingMode.HALF_UP)

                        percentage =
                            if (lastKnownOperator.type == Operators.Kind.SUBTRACTION || lastKnownOperator.type == Operators.Kind.ADDITION)
                                multiplication(lastKnownNumber, percentage)
                            else
                                percentage
                    }

                    expression.removeLast()
                    expression[expression.lastIndex] = numberToToken(percentage.toString())

                    postfix.add(numberToToken(percentage.toString()))
                }
            }
            else if (token.type == TokenTypes.Operator) {
                val operatorToken = operatorParser.parse(token)

                when (operatorToken.type) {
                    Operators.Kind.LEFT_BRACKET -> opStack.push(operatorToken)
                    Operators.Kind.RIGHT_BRACKET -> {
                        while (opStack.peek().type != Operators.Kind.LEFT_BRACKET)
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
    @Throws(ArithmeticException::class)
    fun getResult(expression: MutableList<Token>): Token {
        if (expression.isNullOrEmpty())
            return object : Token {
                override var value = "0"
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
                    Operators.Kind.ADDITION -> addition(left, right)
                    Operators.Kind.SUBTRACTION -> subtraction(left, right)
                    Operators.Kind.MULTIPLICATION -> multiplication(left, right)
                    Operators.Kind.DIVISION -> division(left, right)
                    Operators.Kind.POWER -> power(left, right)
                    else -> TODO("Not Implemented Yet")
                }

                s.push(numberToToken(result.toString()))
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

    private fun addition(left: BigDecimal, right: BigDecimal): BigDecimal = left.plus(right).stripTrailingZeros()
    private fun subtraction(left: BigDecimal, right: BigDecimal): BigDecimal = left.minus(right).stripTrailingZeros()
    private fun multiplication(left: BigDecimal, right: BigDecimal): BigDecimal = left.times(right).stripTrailingZeros()
    @Throws(ArithmeticException::class)
    private fun division(left: BigDecimal, right: BigDecimal): BigDecimal =
        if (right.toDouble() != 0.0)
            left.div(right).stripTrailingZeros()
        else throw ArithmeticException("Division by zero")
    private fun power(left: BigDecimal, right: BigDecimal): BigDecimal = left.pow(right.toInt()).stripTrailingZeros()
}