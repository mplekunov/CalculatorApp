package com.example.calculator.algorithms

import com.example.calculator.model.*
import java.math.RoundingMode
import java.util.*
import kotlin.math.pow

/**
 * Helper class that evaluates [Expression].
 */
object ExpressionEvaluator {
    /**
     * Converts infix into Postfix.
     * @param infix representation of a mathematical expression.
     * @return postfix representation of a mathematical expression.
     */
    private fun infixToPostfix(infix: MutableList<Token>) : MutableList<Token> {
        val postfix = mutableListOf<Token>()
        val opStack = Stack<Token>()

        while (infix.isNotEmpty()){
            val token = infix.first()
            infix.removeFirst()

            if (token.kind == Kind.Operator) {
                when (token.value) {
                    Operator.LEFT_BRACKET.operator -> opStack.push(token)
                    Operator.RIGHT_BRACKET.operator -> {
                        while (opStack.peek().value != Operator.LEFT_BRACKET.operator)
                            postfix.add(opStack.pop())

                        opStack.pop()
                    }
                    else -> {
                        while (opStack.isNotEmpty() && isAssociativeRule(token, opStack.peek()))
                            postfix.add(opStack.pop())

                        opStack.push(token)
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
    private fun isAssociativeRule(x: Token, y: Token) : Boolean =
        (isLeftRule(getValue<Operator>(x.value), getValue<Operator>(y.value)) || isRightRule(getValue<Operator>(x.value), getValue<Operator>(y.value)))

    /**
     * Helper function.
     * Checks for the left rule of [Operator].
     *
     * @param x of type [Operator] representing information about first operator.
     * @param y of type [Operator] representing information about second operator.
     */
    private fun isLeftRule(x: Operator?, y: Operator?): Boolean =
        x!!.associativity == Operator.ASSOCIATIVITY.LEFT && x.precedence <= y!!.precedence
    /**
     * Helper function.
     * Checks for the right rule of [Operator].
     *
     * @param x of type [Operator] representing information about first operator.
     * @param y of type [Operator] representing information about second operator.
     */
    private fun isRightRule(x: Operator?, y: Operator?): Boolean =
        x!!.associativity == Operator.ASSOCIATIVITY.RIGHT && x.precedence < y!!.precedence

    /**
     * Computes result of a mathematical expression.
     *
     * @param expression as a collection of [Token].
     * @return [Token] containing result of computation.
     */
    fun getResult(expression: List<Token>): Token {
        val infix = mutableListOf<Token>().apply { addAll(expression) }

        if (infix.isNullOrEmpty())
            return Token(Kind.Number, "0")

        val postfix = infixToPostfix(infix)

        val s = Stack<Token>()

        for (i in postfix.indices) {
            val token = postfix[i]

            if (token.kind == Kind.Number)
                s.push(token)
            else {
                if (s.size < 2)
                    break

                val right = s.pop().value.toDouble()
                val left = s.pop().value.toDouble()

                when(token.value) {
                    Operator.ADDITION.operator -> s.push(Token(Kind.Number, (left + right).toString()))
                    Operator.SUBTRACTION.operator -> s.push(Token(Kind.Number, (left - right).toString()))
                    Operator.MULTIPLICATION.operator -> s.push(Token(Kind.Number, (left * right).toString()))
                    Operator.DIVISION.operator -> s.push(Token(Kind.Number, (left / right).toString()))
                    Operator.POWER.operator -> s.push(Token(Kind.Number, (left.pow(right).toString())))
                }
            }
        }

        return s.pop()
    }
}