package com.example.calculator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.calculator.model.expression.ExpressionEvaluator
import com.example.calculator.formatter.TokenFormatter

import com.example.calculator.model.token.Token
import com.example.calculator.model.expression.Expression
import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.number.NumberKind
import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.parser.FunctionParser

import com.example.calculator.parser.NumberParser
import com.example.calculator.parser.OperatorParser

import kotlinx.coroutines.*

class CalculatorViewModel : ViewModel() {
    private val expression = Expression()

    private var _inputAsTokens: MutableList<Token> = mutableListOf()
    private var _outputAsToken: Token = NumberParser.parse(NumberKind.ZERO)

    val inputAsTokens: List<Token>
        get() = _inputAsTokens

    val formattedInput: List<String>
        get() = TokenFormatter.convertTokensToStrings(_inputAsTokens)
    val formattedOutput: String
        get() = TokenFormatter.convertTokenToString(_outputAsToken, true)

    private val inputSize: Int
        get() = expression.expression.size

    /**
     * Sends [Numbers.Kind] object to [CalculatorViewModel]
     *
     * @param [number] [Numbers.Kind] enum field that represents number (usually as digit or [Numbers.Kind.DOT])
     * @param [index] position of an object to which [number] will be appended
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun add(number: NumberKind, index: Int = inputSize) : Boolean {
        val result = expression.add(NumberParser.parse(number), index)

        if (result)
            calculateExpression()

        return result
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
        val result = expression.add(FunctionParser.parse(function), index)

        if (result)
            calculateExpression()

        return result
    }

    /**
     * Replaces an old object in [CalculatorViewModel] at specified [index] to [operator]
     *
     * @param [operator] [Operators.Kind] enum field that represents operator
     * @param [index] position of an object to be replaced
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun set(operator: OperatorKind, index: Int = inputSize) : Boolean {
        val result = expression.setOperator(OperatorParser.parse(operator), index)

        if (result)
            calculateExpression()

        return result
    }

    /**
     * Replaces an old object in [CalculatorViewModel] at specified [index] to [function]
     *
     * @param [function] [Functions.Kind] enum field that represents function
     * @param [index] position of an object to be replaced
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun set(function: FunctionKind, index: Int = inputSize) : Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Deletes last [Numbers.Kind], [Functions.Kind], or [Operators.Kind] of the last object in [CalculatorViewModel]
     *
     * @param [index] position of editable object
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun delete(index: Int = inputSize) : Boolean {
        val result = if (index == inputSize)
            expression.delete(index)
        else
            expression.delete(index, false)

        if (result)
            calculateExpression()

        return result
    }

    /**
     * Deletes all object currently stored in the [CalculatorViewModel]
     *
     * @param [index] position of editable object
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun deleteAll(index: Int = inputSize) : Boolean {
        resetResult()

        return expression.deleteAll(index)
    }


    private fun resetResult() {
        _outputAsToken = NumberParser.parse(NumberKind.ZERO)
    }

    /**
     * Saves the [formattedOutput] of calculation and assigns it as the first [Token] of [Expression] in [CalculatorViewModel]
     */
    fun saveResult() {
        val result = _outputAsToken
        deleteAll()

        if (result.value == NumberParser.parse(NumberKind.INFINITY).value)
            expression.add(NumberParser.parse(NumberKind.ZERO), inputSize)
        else
            expression.add(result, expression.expression.lastIndex)

        calculateExpression()
    }

    private fun calculateExpression() {
        _inputAsTokens = expression.expression as MutableList<Token>
        viewModelScope.launch {
            _outputAsToken = try {
                ExpressionEvaluator.getResult(_inputAsTokens)
            } catch (e: ArithmeticException) {
                // Can't divide by zero
                // Error msg?!
                NumberParser.parse(NumberKind.INFINITY)
            }
        }
    }
}