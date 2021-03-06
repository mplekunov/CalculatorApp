package com.example.calculator.viewmodel

import androidx.lifecycle.ViewModel

import com.example.calculator.model.expression.ExpressionEvaluator
import com.example.calculator.formatter.TokenFormatter

import com.example.calculator.model.token.Token
import com.example.calculator.model.expression.Expression
import com.example.calculator.model.postfix.PostfixEvaluator
import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.number.NumberKind
import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.parser.FunctionParser

import com.example.calculator.parser.NumberParser
import com.example.calculator.parser.OperatorParser

import kotlinx.coroutines.*

class CalculatorViewModel : ViewModel() {
    private val expression = Expression()

    private val postfixEvaluator get() = PostfixEvaluator(expression.expression as MutableList<Token>)

    val inputAsTokens: List<Token> get() = postfixEvaluator.infix

    val outputAsToken: Token get() = ExpressionEvaluator(postfixEvaluator.postfix).result

    private val inputSize: Int
        get() = inputAsTokens.size

    /**
     * Sends [Numbers.Kind] object to [CalculatorViewModel]
     *
     * @param [number] [Numbers.Kind] enum field that represents number (usually as digit or [Numbers.Kind.DOT])
     * @param [index] position of an object to which [number] will be appended
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun add(number: NumberKind, index: Int = inputSize) : Boolean {
        return expression.add(NumberParser.parse(number), index)
    }

    /**
     * Sends [Operators.Kind] object to [CalculatorViewModel]
     *
     * @param [operator] [Operators.Kind] enum field that represents operator
     * @param [index] position of an object to which [operator] will be appended
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun add(operator: OperatorKind, index: Int = inputSize) : Boolean {
        return expression.add(OperatorParser.parse(operator), index)
    }

    /**
     * Sends [Functions.Kind] object to [CalculatorViewModel]
     *
     * @param [function] [Functions.Kind] enum field that represents function
     * @param [index] position of an object to which [function] will be appended
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun add(function: FunctionKind, index: Int = inputSize) : Boolean {
        return expression.add(FunctionParser.parse(function), index)
    }

    /**
     * Replaces an old object in [CalculatorViewModel] at specified [index] to [operator]
     *
     * @param [operator] [Operators.Kind] enum field that represents operator
     * @param [index] position of an object to be replaced
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun set(operator: OperatorKind, index: Int = inputSize) : Boolean {
        return expression.setOperator(OperatorParser.parse(operator), index)
    }

    /**
     * Replaces an old object in [CalculatorViewModel] at specified [index] to [function]
     *
     * @param [function] [Functions.Kind] enum field that represents function
     * @param [index] position of an object to be replaced
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun set(function: FunctionKind, index: Int = inputSize) : Boolean {
        return expression.setFunction(FunctionParser.parse(function), index)
    }

    /**
     * Deletes last [Numbers.Kind], [Functions.Kind], or [Operators.Kind] of the last object in [CalculatorViewModel]
     *
     * @param [index] position of editable object
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun delete(index: Int = inputSize) : Boolean {
        return if (index == inputSize)
            expression.delete(index)
        else
            expression.delete(index, false)
    }

    /**
     * Deletes all object currently stored in the [CalculatorViewModel]
     *
     * @param [index] position of editable object
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun deleteAll(index: Int = inputSize) : Boolean {
        return expression.deleteAll(index)
    }

    /**
     * Saves the [formattedOutput] of calculation and assigns it as the first [Token] of [Expression] in [CalculatorViewModel]
     */
    fun saveResult() {
        val result = outputAsToken
        deleteAll()

        if (result == NumberParser.parse(NumberKind.INFINITY) || result == NumberParser.parse(NumberKind.NAN))
            expression.add(NumberParser.parse(NumberKind.ZERO), inputSize)
        else {
            if (result.first() == NumberParser.parse(NumberKind.NEGATIVE)) {
                expression.add(OperatorParser.parse(OperatorKind.SUBTRACTION), expression.expression.lastIndex)
                expression.add(result.slice(1..result.lastIndex), expression.expression.lastIndex)
            }
            else
                expression.add(result, expression.expression.lastIndex)
        }
    }
}