package com.example.calculator.model.expression

import com.example.calculator.model.operator.Associativity
import com.example.calculator.model.token.TokenTypes

import com.example.calculator.model.*
import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.number.Number
import com.example.calculator.model.number.NumberKind
import com.example.calculator.model.operator.Operator
import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.model.token.Token
import com.example.calculator.parser.FunctionParser
import com.example.calculator.parser.NumberParser
import com.example.calculator.parser.OperatorParser
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

/**
 * Helper class that evaluates [Expression].
 */
object ExpressionEvaluator {
    /**
     * Converts infix into Postfix.
     * @param [infix] representation of a mathematical expression.
     * @return [postfix] representation of a mathematical expression.
     */
    private fun infixToPostfix(expression: MutableList<Token>) : MutableList<Token> {
        val infix = mutableListOf<Token>(). apply { addAll(expression) }

        val postfix = mutableListOf<Token>()
        val opStack = Stack<Operator>()

        var i = 0
        while (i < infix.size) {
            if (i == 0 && infix[i].type == TokenTypes.Operator && i + 1 < infix.size) {
                if (OperatorParser.parse<OperatorKind>(infix[i]) == OperatorKind.SUBTRACTION) {
                    val right = BigDecimal(NumberParser.parse<Number>(infix[i + 1]).toString()).setScale(10, RoundingMode.HALF_UP)
                    postfix.add(Token( "${BigDecimal(0).minus(right)}", TokenTypes.Number))
                }
                else
                    return mutableListOf(NumberParser.parse(NumberKind.INFINITY))

                i += 2
                continue
            }

            val token = infix[i]
            i++

            // Operator or Function
            if (token.type == TokenTypes.Function) {
                if (token == FunctionParser.parse(FunctionKind.PERCENTAGE)) {
                    val first = BigDecimal(postfix.removeLast().toString()).setScale(10, RoundingMode.HALF_UP)
                    val second = BigDecimal(100.0).setScale(10, RoundingMode.HALF_UP)
                    var percentage = division(first, second)

                    if (i < infix.size) {
                        val lastKnownOperator = opStack.peek()

                        var last = postfix.last()

                        if (postfix.lastIndex - 1 >= 0 && last.type == TokenTypes.Operator)
                            last = postfix[postfix.lastIndex - 1]

                        val lastKnownNumber = BigDecimal(last.toString()).setScale(10, RoundingMode.HALF_UP)

                        percentage =
                            if (lastKnownOperator == OperatorParser.parse(OperatorKind.SUBTRACTION) || lastKnownOperator == OperatorParser.parse(OperatorKind.ADDITION))
                                multiplication(lastKnownNumber, percentage)
                            else
                                percentage
                    }

                    expression.removeLast()
                    if (percentage < BigDecimal.ZERO)
                        expression[expression.lastIndex] = numberToToken(percentage.times(BigDecimal("-1")))
                    else
                        expression[expression.lastIndex] = numberToToken(percentage)

                    postfix.add(numberToToken(percentage))
                }
            }
            else if (token.type == TokenTypes.Operator) {
                when(OperatorParser.parse(token) as OperatorKind) {
                    OperatorKind.LEFT_BRACKET -> opStack.push(OperatorParser.parse(token))
                    OperatorKind.RIGHT_BRACKET -> {

                        while (opStack.peek() != OperatorParser.parse(OperatorKind.LEFT_BRACKET))
                            postfix.add(opStack.pop() as Operator)

                        opStack.pop()
                    }
                    else -> {
                        while (opStack.isNotEmpty() && isAssociativeRule(OperatorParser.parse(token), opStack.peek()))
                            postfix.add(OperatorParser.parse(opStack.pop()) as Operator)

                        opStack.push(OperatorParser.parse(token))
                    }
                }
            }
            else
                postfix.add(token)
        }

        while (opStack.isNotEmpty())
            postfix.add(OperatorParser.parse(opStack.pop()) as Operator)

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
        if (expression.isEmpty())
            return NumberParser.parse(NumberKind.ZERO)

        val postfix = infixToPostfix(expression)

        val s = Stack<Token>()

        for (i in postfix.indices) {
            val token = postfix[i]

            if (token.type == TokenTypes.Number)
                s.push(token)
            else if (token.type == TokenTypes.Operator){
                // All operators require 2 operands, therefore if we don't have two operands in our stack
                // We can't calculate the result of an expression
                var right = BigDecimal.ZERO
                var left = BigDecimal.ZERO

                if (s.size >= 2) {
                    right = BigDecimal(NumberParser.parse<Number>(s.pop()).toString()).setScale(10, RoundingMode.HALF_UP)
                    left = BigDecimal(NumberParser.parse<Number>(s.pop()).toString()).setScale(10, RoundingMode.HALF_UP)
                }
                else
                    right = BigDecimal(NumberParser.parse<Number>(s.pop()).toString()).setScale(10, RoundingMode.HALF_UP)


                val result = when(OperatorParser.parse(token) as Operator) {
                    OperatorParser.parse(OperatorKind.ADDITION) -> addition(left, right)
                    OperatorParser.parse(OperatorKind.SUBTRACTION) -> subtraction(left, right)
                    OperatorParser.parse(OperatorKind.MULTIPLICATION) -> multiplication(left, right)
                    OperatorParser.parse(OperatorKind.DIVISION) -> {
                        if (right.toDouble() == 0.0)
                            return NumberParser.parse(NumberKind.INFINITY)

                        division(left, right)
                    }
                    OperatorParser.parse(OperatorKind.POWER) -> power(left, right)
                    else -> TODO("Not Implemented Yet")
                }

                s.push(numberToToken(result))
            }
        }

        return s.pop()
    }

    private fun numberToToken(number: BigDecimal) : Token = Token(number.toString(), TokenTypes.Number)

    private fun addition(left: BigDecimal, right: BigDecimal): BigDecimal = left.plus(right).stripTrailingZeros()
    private fun subtraction(left: BigDecimal, right: BigDecimal): BigDecimal = left.minus(right).stripTrailingZeros()
    private fun multiplication(left: BigDecimal, right: BigDecimal): BigDecimal = left.times(right).stripTrailingZeros()
    private fun division(left: BigDecimal, right: BigDecimal): BigDecimal = left.div(right).stripTrailingZeros()
    private fun power(left: BigDecimal, right: BigDecimal): BigDecimal = left.pow(right.toInt()).stripTrailingZeros()
}