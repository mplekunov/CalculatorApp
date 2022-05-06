package com.example.calculator.algorithms

import com.example.calculator.miscellaneous.Associativity
import com.example.calculator.miscellaneous.Operators
import com.example.calculator.miscellaneous.TokenTypes
import com.example.calculator.model.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.ArithmeticException
import kotlin.NoSuchElementException
import kotlin.Number as Number1

/**
 * Helper class that evaluates [Expression].
 */
object ExpressionEvaluator {
    val bigDecimal = BigDecimal.ZERO
    /**
     * Converts infix into Postfix.
     * @param infix representation of a mathematical expression.
     * @return postfix representation of a mathematical expression.
     */
    @Throws(NullPointerException::class)
    private fun infixToPostfix(infix: MutableList<Token>) : MutableList<Token> {
        val postfix = mutableListOf<Token>()
        val opStack = Stack<Operator>()

        while (infix.isNotEmpty()){
            val token = infix.first()
            infix.removeFirst()

            // Operator or Function or Percentage
            if (token.type == TokenTypes.Operator) {
                val operatorToken = Operator.parseToken(token) ?: throw NullPointerException("Empty Operator Token")

                when (operatorToken.type) {
                    Operators.LEFT_BRACKET -> opStack.push(operatorToken)
                    Operators.RIGHT_BRACKET -> {
                        while (opStack.peek().type != Operators.LEFT_BRACKET)
                            postfix.add(opStack.pop())

                        opStack.pop()
                    }
                    else -> {
                        while (opStack.isNotEmpty() && isAssociativeRule(operatorToken, opStack.peek()))
                            postfix.add(opStack.pop())

                        opStack.push(operatorToken)
                    }
                }
            }
            else
                postfix.add(token)
        }

        while (opStack.isNotEmpty())
            postfix.add(opStack.pop())

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
    @Throws(ArithmeticException::class, NoSuchElementException::class)
    fun getResult(expression: List<Token>): Token {
        val infix = mutableListOf<Token>().apply { addAll(expression) }

        if (infix.isNullOrEmpty())
            return object : Token {
                override var value = "0"
                override val type = TokenTypes.Number
            }

        val postfix = infixToPostfix(infix)

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

                val operator = Operator.parseToken(token) ?: throw NullPointerException("Empty Operator Token")

                val right = BigDecimal(s.pop().value).setScale(10, RoundingMode.HALF_UP)
                val left = BigDecimal(s.pop().value).setScale(10, RoundingMode.HALF_UP)

                val result = when(operator.type) {
                    Operators.ADDITION -> addition(left, right)
                    Operators.SUBTRACTION -> subtraction(left, right)
                    Operators.MULTIPLICATION -> multiplication(left, right)
                    Operators.DIVISION -> division(left, right)
                    Operators.POWER -> power(left, right)
                    else -> throw NoSuchElementException("No Suitable Operator Was Found")
                }

                s.push(numberToToken(result))
            }
        }

        val token = s.pop()
        token.value = BigDecimal(token.value).toString()

        return token
    }

    private fun numberToToken(number: String) : Token {
        return object : Token {
            override var value = number
            override val type = TokenTypes.Number
        }
    }

    private fun addition(left: BigDecimal, right: BigDecimal): String = left.plus(right).toString()
    private fun subtraction(left: BigDecimal, right: BigDecimal): String = left.minus(right).toString()
    private fun multiplication(left: BigDecimal, right: BigDecimal): String = left.times(right).toString()
    @Throws(ArithmeticException::class)
    private fun division(left: BigDecimal, right: BigDecimal): String {
        return if (right != BigDecimal.ZERO) left.div(right).toString() else throw ArithmeticException("Division by 0")
    }
    private fun power(left: BigDecimal, right: BigDecimal): String = left.pow(right.toInt()).toString()
}