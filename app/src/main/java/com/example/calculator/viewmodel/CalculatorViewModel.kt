package com.example.calculator.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.calculator.algorithms.ExpressionEvaluator
import com.example.calculator.algorithms.TokenFormatter
import com.example.calculator.miscellaneous.Functions
import com.example.calculator.miscellaneous.Numbers
import com.example.calculator.miscellaneous.Operators

import com.example.calculator.miscellaneous.TokenTypes

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
    val inputAsTokens: List<Token>
        get() = _inputAsTokens

    lateinit var resultOfExpression: Token

    private val expressionSize: Int
        get() = expression.expression.size

    init {
        resetResult()
    }

    fun add(number: Numbers, index: Int = expressionSize) {
        expression.addNumber(Number(number), index)
        Log.d("viewModel", "addNumber: ${TokenFormatter.convertTokensToStrings(expression.expression)}")
        calculateExpression()
    }

    fun add(operator: Operators, index: Int = expressionSize) {
        expression.addOperator(Operator(operator), index)

        if (index != expression.expression.lastIndex)
            calculateExpression()
    }

    fun add(function: Functions, index: Int = expressionSize) {
        expression.addFunction(Function(function), index)
        calculateExpression()
    }

    fun set(operator: Operators, index: Int = expressionSize) {
        expression.setOperator(Operator(operator), index)
        calculateExpression()
    }

    fun set(function: Functions, index: Int = expressionSize) {
        TODO("Not yet implemented")
    }

    fun delete() {
        expression.delete()
        calculateExpression()
    }

    fun deleteAt(index: Int) {
        expression.deleteAt(index, false)
        calculateExpression()
    }

    fun deleteAll() {
        expression.deleteAll()
        resetResult()
        calculateExpression()
    }

    private fun resetResult() {
        resultOfExpression = object : Token {
            override var value = "0"
            override val type = TokenTypes.Number
        }
    }

    fun deleteAllAt(index: Int) {
        expression.deleteAllAt(index)
        calculateExpression()
    }

    fun saveResult() {
        val result = numberParser.parse(resultOfExpression)
        deleteAll()

        if (result.type == Numbers.INFINITY)
            expression.addNumber(Number(Numbers.ZERO), expressionSize)
        else
            expression.addNumber(result, expression.expression.lastIndex)

        calculateExpression()
    }

    private fun calculateExpression() {
        _inputAsTokens = expression.expression as MutableList<Token>
        viewModelScope.launch {
            resultOfExpression = try {
                ExpressionEvaluator.getResult(_inputAsTokens)
            } catch (e: ArithmeticException) {
                // Can't divide by zero
                // Error msg?!
                numberParser.parse(Number(Numbers.INFINITY))
            }
        }
    }
}