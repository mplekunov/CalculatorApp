package com.example.calculator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.calculator.algorithms.ExpressionEvaluator
import com.example.calculator.formatter.TokenFormatter
import com.example.calculator.miscellaneous.Functions
import com.example.calculator.miscellaneous.Numbers
import com.example.calculator.miscellaneous.Operators

import com.example.calculator.model.Operator
import com.example.calculator.model.Number
import com.example.calculator.model.Function
import com.example.calculator.model.Token
import com.example.calculator.model.Expression

import com.example.calculator.parser.NumberParser

import kotlinx.coroutines.*

class CalculatorViewModel : ViewModel() {
    private val expression = Expression()

    private val numberParser = NumberParser()

    private var _inputAsTokens: MutableList<Token> = mutableListOf()
    private var _resultAsToken: Token = numberParser.parse(Number(Numbers.ZERO))

    val inputAsTokens: List<Token>
        get() = _inputAsTokens

    val input: List<String>
        get() = TokenFormatter.convertTokensToStrings(_inputAsTokens)
    val result: String
        get() = TokenFormatter.convertTokenToString(_resultAsToken, true)

    private val inputSize: Int
        get() = expression.expression.size

    /**
     * Sends [Numbers] object to [CalculatorViewModel]
     *
     * @param [number] [Numbers] enum field that represents number (usually as digit or [Numbers.DOT])
     * @param [index] position of an object to which [number] will be appended
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun add(number: Numbers, index: Int = inputSize) : Boolean {
        val result = expression.addNumber(Number(number), index)

        if (result)
            calculateExpression()

        return result
    }

    /**
     * Sends [Operators] object to [CalculatorViewModel]
     *
     * @param [operator] [Operators] enum field that represents operator
     * @param [index] position of an object to which [operator] will be appended
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun add(operator: Operators, index: Int = inputSize) : Boolean {
        return expression.addOperator(Operator(operator), index)
    }

    /**
     * Sends [Functions] object to [CalculatorViewModel]
     *
     * @param [function] [Functions] enum field that represents function
     * @param [index] position of an object to which [function] will be appended
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun add(function: Functions, index: Int = inputSize) : Boolean {
        val result = expression.addFunction(Function(function), index)

        if (result)
            calculateExpression()

        return result
    }

    /**
     * Replaces an old object in [CalculatorViewModel] at specified [index] to [operator]
     *
     * @param [operator] [Operators] enum field that represents operator
     * @param [index] position of an object to be replaced
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun set(operator: Operators, index: Int = inputSize) : Boolean {
        val result = expression.setOperator(Operator(operator), index)

        if (result)
            calculateExpression()

        return result
    }

    /**
     * Replaces an old object in [CalculatorViewModel] at specified [index] to [function]
     *
     * @param [function] [Functions] enum field that represents function
     * @param [index] position of an object to be replaced
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun set(function: Functions, index: Int = inputSize) : Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Deletes last [Numbers], [Functions], or [Operators] of the last object in [CalculatorViewModel]
     *
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun delete() : Boolean {
        val result = expression.delete()

        if (result)
            calculateExpression()

        return result
    }

    /**
     * Deletes last [Numbers], [Functions], or []Operators] of the object in [CalculatorViewModel] at specified [index]
     *
     * @param [index] position of editable object
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun deleteAt(index: Int) : Boolean {
        val result = expression.deleteAt(index, false)

        if (result)
            calculateExpression()

        return result
    }

    /**
     * Deletes all object currently stored in the [CalculatorViewModel]
     *
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun deleteAll() : Boolean {
        resetResult()

        return expression.deleteAll()
    }

    /**
     * Deletes all [Functions], [Numbers], or [Operators] stored in [CalculatorViewModel]'s object at specified [index]
     *
     * @param [index] position of editable object in [CalculatorViewModel]
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun deleteAllAt(index: Int) : Boolean {
        val result = expression.deleteAllAt(index)

        if (result)
            calculateExpression()

        return result
    }

    private fun resetResult() {
        _resultAsToken = numberParser.parse(Number(Numbers.ZERO))
    }


    /**
     * Saves the [result] of calculation and assigns it as the first [Token] of [Expression] in [CalculatorViewModel]
     */
    fun saveResult() {
        val result = numberParser.parse(_resultAsToken)
        deleteAll()

        if (result.type == Numbers.INFINITY)
            expression.addNumber(Number(Numbers.ZERO), inputSize)
        else
            expression.addNumber(result, expression.expression.lastIndex)

        calculateExpression()
    }

    private fun calculateExpression() {
        _inputAsTokens = expression.expression as MutableList<Token>
        viewModelScope.launch {
            _resultAsToken = try {
                ExpressionEvaluator.getResult(_inputAsTokens)
            } catch (e: ArithmeticException) {
                // Can't divide by zero
                // Error msg?!
                numberParser.parse(Number(Numbers.INFINITY))
            }
        }
    }
}