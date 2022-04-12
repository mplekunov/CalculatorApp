package com.example.calculator.viewmodel
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.calculator.algorithms.ExpressionEvaluator
import com.example.calculator.algorithms.InputParser
import com.example.calculator.model.*

class CalculatorViewModel : ViewModel() {
    private val expression = Expression()

    var inputAsTokens: List<Token> = emptyList()
    var resultOfExpression: Token = Token(Kind.Number, "0")

    fun appendToken(input: String) {
        val token = InputParser.parseToken(input)

        calculateExpression(expression.appendToken(token))
    }

    fun appendTokenAt(input: String, index: Int) {
        val token = InputParser.parseToken(input)

        calculateExpression(expression.appendTokenAt(token, index))
    }

    fun setTokenAt(input: String, index: Int) {
        val token = InputParser.parseToken(input)

        calculateExpression(expression.setTokenAt(token, index))
    }

    fun deleteToken() {
        calculateExpression(expression.deleteToken())
    }

    fun deleteTokenAt(index: Int) {
        calculateExpression(expression.deleteTokenAt(index, false))
    }

    fun deleteAllTokens() {
        resultOfExpression.value = "0"
        calculateExpression(expression.deleteAllTokens())
    }

    fun deleteAllTokensAt(index: Int) {
        calculateExpression(expression.deleteAllTokensAt(index))
    }

    fun saveResult() {
        expression.deleteAllTokens()
        val token = InputParser.parseToken(resultOfExpression.value)
        calculateExpression(expression.appendToken(token))
    }

    private fun calculateExpression(isUpdated: Boolean) {
        if (isUpdated) {
            inputAsTokens = expression.expression
            resultOfExpression = ExpressionEvaluator.getResult(inputAsTokens)
        }
    }
}