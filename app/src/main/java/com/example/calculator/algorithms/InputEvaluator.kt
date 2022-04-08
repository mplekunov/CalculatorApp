package com.example.calculator.algorithms

import com.example.calculator.model.*
import java.util.*
import kotlin.math.pow

class InputEvaluator {
    private fun infixToPostfix(infix: MutableList<String>) : MutableList<String> {
        val postfix = mutableListOf<String>()
        val opStack = Stack<Operator>()

        while (infix.isNotEmpty()){
            val token = infix.first()
            infix.removeFirst()

            if (contains<Operator>(token)) {
                when (val op = getOperator(token)!!) {
                    Operator.LEFT_BRACKET -> opStack.push(op)
                    Operator.RIGHT_BRACKET -> {
                        while (opStack.peek() != Operator.LEFT_BRACKET)
                            postfix.add(opStack.pop().operator)

                        opStack.pop()
                    }
                    else -> {
                        while (opStack.isNotEmpty() && isAssociativeRule(op, opStack.peek()))
                            postfix.add(opStack.pop().operator)

                        opStack.push(op)
                    }
                }
            }
            else
                postfix.add(token)
        }

        while (opStack.isNotEmpty())
            postfix.add(opStack.pop().operator)

        return postfix
    }

    private fun isAssociativeRule(x: Operator, y: Operator) : Boolean = (isLeftRule(x, y) || isRightRule(x, y))

    private fun isLeftRule(x: Operator, y: Operator): Boolean =
        x.associativity == Operator.ASSOCIATIVITY.LEFT && x.precedence <= y.precedence

    private fun isRightRule(x: Operator, y: Operator): Boolean =
        x.associativity == Operator.ASSOCIATIVITY.RIGHT && x.precedence < y.precedence

    fun getOperator(token: String): Operator? = getValue<Operator>(token)

    fun isNumber(token: String): Boolean = !isOperator(token)

    fun isFloat(token: String): Boolean = token.any { it.toString() == Operator.DOT.operator }

//        ceil(token.toDouble()) != token.toDouble()

    fun isOperator(token: String): Boolean = contains<Operator>(token)

    fun getResult(input: List<String>): Double {
        val infix = mutableListOf<String>().apply { addAll(input) }

        if (input.isNullOrEmpty())
            return 0.0

        val postfix = infixToPostfix(infix)

        val s = Stack<Double>()

        for (i in postfix.indices) {
            val token = postfix[i]

            if (!contains<Operator>(token))
                s.push(token.toDouble())
            else {
                if (s.size < 2)
                    break

                val right = s.pop()
                val left = s.pop()

                when(getOperator(token)) {
                    Operator.ADDITION -> s.push(left + right)
                    Operator.SUBTRACTION -> s.push(left - right)
                    Operator.MULTIPLICATION -> s.push(left * right)
                    Operator.DIVISION -> s.push(left / right)
                    Operator.POWER -> s.push(left.pow(right))
                    else -> {}
                }
            }
        }

        return s.pop()
    }
}