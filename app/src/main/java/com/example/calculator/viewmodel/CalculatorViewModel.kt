package com.example.calculator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculator.algorithms.ExpressionEvaluator
import com.example.calculator.Parser.InputParser
import com.example.calculator.model.*
import kotlinx.coroutines.*

class CalculatorViewModel : ViewModel() {
    private val expression = Expression()

    var inputAsTokens: List<Token> = emptyList()
    var resultOfExpression: Token = Token(Kind.Number, "0")

    fun appendToken(input: String) {
        val token = InputParser.parseToken(input)
        expression.appendToken(token)

        if (token.kind == Kind.Number)
            calculateExpression()
    }

    fun appendTokenAt(input: String, index: Int) {
        val token = InputParser.parseToken(input)
        expression.appendTokenAt(token, index)

        calculateExpression()
    }

    fun setTokenAt(input: String, index: Int) {
        val token = InputParser.parseToken(input)
        expression.setTokenAt(token, index)

        calculateExpression()
    }

    fun deleteToken() {
        expression.deleteToken()

        if (expression.expression.isNotEmpty() && expression.expression.last().kind == Kind.Number)
            calculateExpression()
    }

    fun deleteTokenAt(index: Int) {
        expression.deleteTokenAt(index, false)

        calculateExpression()
    }

    fun deleteAllTokens() {
        resultOfExpression = Token(Kind.Number, "0")
        expression.deleteAllTokens()

        calculateExpression()
    }

    fun deleteAllTokensAt(index: Int) {
        expression.deleteAllTokensAt(index)

        calculateExpression()
    }

    fun saveResult() {
        expression.deleteAllTokens()
        val token = InputParser.parseToken(resultOfExpression.value)
        expression.appendToken(token)

        calculateExpression()
    }

    private fun calculateExpression() {
        inputAsTokens = expression.expression
        try {
            viewModelScope.launch {
                resultOfExpression = ExpressionEvaluator.getResult(inputAsTokens)
            }
        // Division by zero
        } catch (e: ArithmeticException) {
            resultOfExpression = Token(Kind.Number, "0")
        }
    }
}