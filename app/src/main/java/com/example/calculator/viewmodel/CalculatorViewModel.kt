package com.example.calculator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.calculator.algorithms.ExpressionEvaluator
import com.example.calculator.algorithms.TokenFormatter

import com.example.calculator.miscellaneous.TokenTypes

import com.example.calculator.model.Operator
import com.example.calculator.model.Number
import com.example.calculator.model.Function
import com.example.calculator.model.Token
import com.example.calculator.model.Expression

import kotlinx.coroutines.*
import kotlin.NullPointerException

class CalculatorViewModel : ViewModel() {
    private val expression = Expression()

    private var _inputAsTokens: MutableList<Token> = mutableListOf()
    val inputAsTokens: List<Token>
        get() = _inputAsTokens

    lateinit var resultOfExpression: Token

    init {
        resetResult()
    }

    fun addToken(token: Token) {
        addTokenAt(token)
    }

    @Throws(NullPointerException::class)
    private fun addNumber(token: Token, index: Int = expression.expression.size) {
        val number = Number.parseToken(token) ?: throw NullPointerException("Empty Number Token")

        // We can't receive more then one number token at a time because user inputs one token at a time
        // Therefore, after conversion, valueAsTokens will have only 1 token and the that token we will need to add
        expression.addNumber(number.valueAsTokens.first(), index)
        calculateExpression()
    }

    @Throws(NullPointerException::class)
    private fun addOperator(token: Token, index: Int = expression.expression.size) {
        val operator = Operator.parseToken(token)?.subType ?: throw NullPointerException("Empty Operator Token")

        expression.addOperator(operator, index)
    }

    @Throws(NullPointerException::class)
    private fun addFunction(token : Token, index : Int = expression.expression.size) {
        val function = Function.parseToken(token)?.subType ?: throw NullPointerException("Empty Function Token")

        expression.addFunction(function, index)
        calculateExpression()
    }

    fun addTokenAt(token: Token, index: Int = expression.expression.size) {
        when(token.type) {
            TokenTypes.Operator -> addOperator(token, index)
            TokenTypes.Number -> addNumber(token, index)
            TokenTypes.Function -> addFunction(token, index)
        }
    }

    fun setTokenAt(token: Token, index: Int = expression.expression.lastIndex) {
        expression.setTokenAt(token, index)

        if (index != expression.expression.lastIndex || (index == expression.expression.lastIndex && token.type == TokenTypes.Number))
            calculateExpression()
    }

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
            override var value = "0"
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
        addToken(result)
        calculateExpression()
    }

    private fun calculateExpression() {
        _inputAsTokens = expression.expression as MutableList<Token>
        try {
            viewModelScope.launch {
                resultOfExpression = ExpressionEvaluator.getResult(_inputAsTokens)
            }
        // Division by zero
        } catch (e: ArithmeticException) {
            resetResult()
        }
    }
}