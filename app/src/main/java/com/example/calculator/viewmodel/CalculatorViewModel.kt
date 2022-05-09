package com.example.calculator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.calculator.algorithms.ExpressionEvaluator
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

    private var _inputAsTokens: MutableList<Token> = mutableListOf()
    val inputAsTokens: List<Token>
        get() = _inputAsTokens

    lateinit var resultOfExpression: Token

    init {
        resetResult()
    }

    fun addNumber(number: Numbers, index: Int = expression.expression.size) {
        expression.addNumber(Number(number), index)
        calculateExpression()
    }

    fun addOperator(operator: Operators, index: Int = expression.expression.size) {
        expression.addOperator(Operator(operator), index)

        if (index != expression.expression.lastIndex)
            calculateExpression()
    }

    fun addFunction(function: Functions, index : Int = expression.expression.size) {
        expression.addFunction(Function(function), index)
        calculateExpression()
    }

//    fun setTokenAt(token: Token, index: Int = expression.expression.lastIndex) {
//        expression.setTokenAt(token, index)
//
//        if (index != expression.expression.lastIndex || (index == expression.expression.lastIndex && token.type == TokenTypes.Number))
//            calculateExpression()
//    }

    fun deleteToken() {
        expression.deleteToken()

        if (expression.expression.isNotEmpty() && expression.expression.last().type == TokenTypes.Number)
            calculateExpression()
    }

    fun deleteTokenAt(index: Int) {
        expression.deleteTokenAt(index, false)

        calculateExpression()
    }

    fun deleteAllTokens() {
        expression.deleteAllTokens()
        resetResult()
        calculateExpression()
    }

    private fun resetResult() {
        resultOfExpression = object : Token {
            override var value = ""
            override val type = TokenTypes.Number
        }
    }

    fun deleteAllTokensAt(index: Int) {
        expression.deleteAllTokensAt(index)
        calculateExpression()
    }

    fun saveResult() {
        val result = resultOfExpression
        deleteAllTokens()
        expression.addNumber(NumberParser().parse(result), expression.expression.lastIndex)
        calculateExpression()
    }

    private fun calculateExpression() {
        _inputAsTokens = expression.expression as MutableList<Token>
        viewModelScope.launch {
            resultOfExpression = ExpressionEvaluator.getResult(_inputAsTokens)
        }
    }
}