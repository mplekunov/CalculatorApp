package com.example.calculator.viewmodel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.calculator.algorithms.InputEvaluator
import com.example.calculator.model.StringFormatter
import com.example.calculator.model.Operator
import kotlin.math.max

class CalculatorViewModel : ViewModel() {
    private var _result = MutableLiveData<Double>()
    private var _input = MutableLiveData<MutableList<String>>()

    val expression: LiveData<String> = Transformations.map(_input) {
        StringFormatter.formatInput(it)
    }

    val result: LiveData<String> = Transformations.map(_result) {
        StringFormatter.formatOutput(it)
    }

    init {
        clearAll()
    }

    fun parseToken(token: String) {
        if (InputEvaluator.isNumber(token))
            parseNumber(token)
        else if (InputEvaluator.isOperator(token))
            parseOperator(token)

        updateResult()
    }

    private fun parseOperator(token: String) {
        val expr = _input.value ?: emptyList()

        if (expr.isEmpty())
            return

        val lastChar = expr.last().last().toString()
        if (InputEvaluator.isOperator(lastChar) && InputEvaluator.getOperator(lastChar) == Operator.DOT && InputEvaluator.getOperator(token) != Operator.DOT)
            concatToLastToken("0")

        if (InputEvaluator.isOperator(expr.last()))
            return

        when {
            InputEvaluator.getOperator(token) == Operator.PERCENTAGE -> parsePercentage()
            InputEvaluator.getOperator(token) == Operator.DOT -> parseDot(token)
            else -> addToken(token)
        }
        Log.d("Calculator", "${_input.value}")
    }

    private fun parseDot(token: String) {
        val expr = _input.value ?: emptyList()

        if (expr.isEmpty() || InputEvaluator.isFloat(expr.last()))
            return

        concatToLastToken(token)
        Log.d("Calculator", "${_input.value}")
    }

    private fun parsePercentage() {
        val expr = _input.value ?: emptyList()

        if (expr.isEmpty())
            return

        var percentage: Double = expr.last().toDouble() / 100

        if (expr.size > 2) {
            val lastKnownOperator = InputEvaluator.getOperator(expr[expr.lastIndex - 1])
            val lastKnownNumber = expr[expr.lastIndex - 2].toDouble()

            percentage = when (lastKnownOperator) {
                Operator.ADDITION -> percentage * lastKnownNumber
                Operator.SUBTRACTION -> (1 - percentage) * lastKnownNumber
                else -> percentage
            }
        }

        setToken(expr.lastIndex, percentage.toString())
        Log.d("Calculator", "${_input.value}")
    }

    private fun parseNumber(token: String) {
        val expr = _input.value ?: emptyList()

        when {
            expr.isEmpty() || InputEvaluator.isOperator(expr.last()) -> addToken(token)
            expr.last().first() == '0' && !InputEvaluator.isFloat(expr.last()) -> setToken(expr.lastIndex, token)
            else -> if (expr.last().length < max(StringFormatter.MAX_INTEGER_DIGITS, StringFormatter.MAX_FRACTION_DIGITS)) concatToLastToken(token)
        }
        Log.d("Calculator", "${_input.value}")
    }

    private fun addToken(element: String) {
        _input.value?.add(element)
        _input.value = _input.value
    }

    private fun concatToLastToken(element: String) {
        _input.value?.lastIndex?.let { _input.value?.set(it, _input.value?.last() + element) }
        _input.value = _input.value
    }

    private fun setToken(index: Int, element: String) {
        _input.value?.set(index, element)
        _input.value = _input.value
    }

    private fun updateResult() {
        if (_input.value.isNullOrEmpty())
            _result.value = 0.0
        else
            _result.value = InputEvaluator.getResult(_input.value!!)
    }

    fun deleteLastToken() {
        val expr = (_input.value ?: emptyList()).toMutableList()

        if (expr.isEmpty())
            return

        var lastToken = expr.last()

        if (lastToken.isNotEmpty()) {
            lastToken = lastToken.subSequence(0, lastToken.lastIndex) as String

            if (lastToken.isNotEmpty())
                expr[expr.lastIndex] = lastToken
            else
                expr.removeLast()

            _input.value = expr
        }

        updateResult()
    }

    fun clearAll() {
        _result.value = 0.0
        _input.value = mutableListOf()
    }

    fun useResult() {
        _input.value = mutableListOf<String>().apply { add(_result.value.toString()) }
    }
}