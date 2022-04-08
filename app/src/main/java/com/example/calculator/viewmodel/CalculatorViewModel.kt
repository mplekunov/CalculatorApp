package com.example.calculator.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.calculator.algorithms.InputEvaluator
import com.example.calculator.model.Operator
import java.text.NumberFormat

class CalculatorViewModel : ViewModel() {
    private var _result = MutableLiveData<Double>()
    private var _input = MutableLiveData<MutableList<String>>()

    private val evaluator = InputEvaluator()

    val expression: LiveData<String> = Transformations.map(_input) {
        it.toString().replace(Regex("[\\[,\\]]"), "")
    }

    val result: LiveData<String> = Transformations.map(_result) {
        NumberFormat.getNumberInstance().format(it)
    }

    init {
        clearAll()
    }

    fun parseToken(token: String) {
        if (evaluator.isNumber(token))
            parseNumber(token)
        else if (evaluator.isOperator(token))
            parseOperator(token)

        updateResult()
    }

    private fun parseOperator(token: String) {
        val expr = _input.value ?: emptyList()

        if (expr.isEmpty())
            return

        val lastChar = expr.last().last().toString()
        if (evaluator.isOperator(lastChar) && evaluator.getOperator(lastChar) == Operator.DOT && evaluator.getOperator(token) != Operator.DOT)
            concatToLastToken("0")

        if (evaluator.isOperator(expr.last()))
            return

        when {
            evaluator.getOperator(token) == Operator.PERCENTAGE -> parsePercentage()
            evaluator.getOperator(token) == Operator.DOT -> parseDot(token)
            else -> addToken(token)
        }
    }

    private fun parseDot(token: String) {
        val expr = _input.value ?: emptyList()

        if (expr.isEmpty() || evaluator.isFloat(expr.last()))
            return

        concatToLastToken(token)
    }

    private fun parsePercentage() {
        val expr = _input.value ?: emptyList()

        if (expr.isEmpty())
            return

        var percentage: Double = expr.last().toDouble() / 100

        if (expr.size > 2) {
            val lastKnownOperator = evaluator.getOperator(expr[expr.lastIndex - 1])
            val lastKnownNumber = expr[expr.lastIndex - 2].toDouble()

            percentage = when (lastKnownOperator) {
                Operator.ADDITION -> percentage * lastKnownNumber
                Operator.SUBTRACTION -> (1 - percentage) * lastKnownNumber
                else -> percentage
            }
        }

        setToken(expr.lastIndex, percentage.toString())
    }

    private fun parseNumber(token: String) {
        val expr = _input.value ?: emptyList()

        if (expr.isNotEmpty() && evaluator.isNumber(expr.last()))
            concatToLastToken(token)
        else
            addToken(token)
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
            _result.value = evaluator.getResult(_input.value!!)
    }

    fun deleteLastToken() {
        if (_input.value?.isEmpty() == true)
            return

        var lastToken = _input.value?.last()

        if (!lastToken.isNullOrEmpty()) {
            lastToken = lastToken.subSequence(0, lastToken.lastIndex) as String

            if (lastToken.isNotEmpty())
                _input.value?.set(_input.value?.lastIndex!!, lastToken)
            else
                _input.value?.removeLast()

            _input.value = _input.value
        }

        updateResult()
    }

    fun clearAll() {
        _result.value = 0.0
        _input.value = mutableListOf()
    }
}