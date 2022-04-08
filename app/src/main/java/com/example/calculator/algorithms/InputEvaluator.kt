package com.example.calculator.algorithms

import com.example.calculator.model.*
import java.util.*
import kotlin.math.ceil
import kotlin.math.pow

class InputEvaluator() {
    private var _result = 0.0

    private fun infixToPostfix(infix: MutableList<String>) : MutableList<String> {
        val postfix = mutableListOf<String>()
        val opStack = Stack<Operator>()

        while (infix.isNotEmpty()){
            val token = infix.first()
            infix.removeFirst()

            if (operatorMap.containsKey(token)) {
                when (val op = operatorMap[token]) {
                    getOperator(OPERATORS.LEFT_BRACKET) -> opStack.push(op)
                    getOperator(OPERATORS.RIGHT_BRACKET) -> {
                        while (opStack.peek() != getOperator(OPERATORS.LEFT_BRACKET))
                            postfix.add(opStack.pop().value)

                        opStack.pop()
                    }
                    else -> {
                        while (opStack.isNotEmpty() && isAssociativeRule(op!!, opStack.peek()))
                            postfix.add(opStack.pop().value)

                        opStack.push(op)
                    }
                }
            }
            else
                postfix.add(token)
        }

        while (opStack.isNotEmpty())
            postfix.add(opStack.pop().value)

        return postfix
    }

    private fun isAssociativeRule(x: Operator, y: Operator) : Boolean = (isLeftRule(x, y) || isRightRule(x, y))

    private fun isLeftRule(x: Operator, y: Operator): Boolean =
        x.associativity == ASSOCIATIVITY.LEFT && x.precedence <= y.precedence

    private fun isRightRule(x: Operator, y: Operator): Boolean =
        x.associativity == ASSOCIATIVITY.RIGHT && x.precedence < y.precedence

    fun isNumber(str: String): Boolean {
        return str.toDoubleOrNull() != null
    }

    fun isFloat(num: Double): Boolean {
        return ceil(num) != num
    }

    fun getResult(input: List<String>): Double {
        val infix = mutableListOf<String>().apply { addAll(input) }

        if (input.isNullOrEmpty())
            return 0.0

        val postfix = infixToPostfix(infix)

        val s = Stack<Double>()

        for (i in postfix.indices) {
            val token = postfix[i]

            if (isNumber(token))
                s.push(token.toDouble())
            else {
                if (s.size < 2)
                    break

                val right = s.pop()
                val left = s.pop()

                when {
                    operatorMap[token] == getOperator(OPERATORS.ADDITION) -> s.push(left + right)
                    operatorMap[token] == getOperator(OPERATORS.SUBTRACTION) -> s.push(left - right)
                    operatorMap[token] == getOperator(OPERATORS.MULTIPLICATION) -> s.push(left * right)
                    operatorMap[token] == getOperator(OPERATORS.DIVISION) -> s.push(left / right)
                    operatorMap[token] == getOperator(OPERATORS.POWER) -> s.push(left.pow(right))
                }
            }
        }

        return s.pop()
    }
}