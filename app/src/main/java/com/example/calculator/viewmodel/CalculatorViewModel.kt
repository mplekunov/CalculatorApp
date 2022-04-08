package com.example.calculator.viewmodel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.calculator.algorithms.InputEvaluator
import com.example.calculator.model.Operator
import java.text.NumberFormat
import java.util.*
import kotlin.math.ceil
import kotlin.math.pow

class CalculatorViewModel : ViewModel() {
    private var _result = MutableLiveData<Double>()
    private var _infix = MutableLiveData<MutableList<String>>()
    private val inputEvaluator = InputEvaluator()


    val expression: LiveData<String> = Transformations.map(_infix) {
        it.toString().replace(Regex("[\\[,\\]]"), "")
    }

    val result: LiveData<String> = Transformations.map(_result) {
        NumberFormat.getNumberInstance().format(it)
    }

    init {
        clearAll()
    }

    fun parseToken(token: String) {
        if (_infix.value?.isNotEmpty() == true) {
            if (inputEvaluator.isNumber(_infix.value?.last()!!) && inputEvaluator.isNumber(token))
                concatToLastToken(token)
            else if (inputEvaluator.isNumber(_infix.value?.last()!!) && !inputEvaluator.isNumber(token)) {
                if (token.equals("%", true))
                    setToken(_infix.value?.lastIndex!!, evalPercentExpr())
                else if (token.equals(".", true)) {
                    if (!_infix.value?.last()!!.last().toString().equals(".", true) && !inputEvaluator.isFloat(_infix.value?.last()!!.toDouble()))
                        concatToLastToken(token)
                }
                else if (!token.equals(".", true)) {
                    if (_infix.value?.last()!!.last().toString().equals(".", true))
                        concatToLastToken("0")

                    addToken(token)
                }
            } else if (!inputEvaluator.isNumber(_infix.value?.last()!!) && inputEvaluator.isNumber(token))
                addToken(token)
        }
        else if (inputEvaluator.isNumber(token))
            addToken(token)

        if (!_infix.value.isNullOrEmpty())
            _result.value = inputEvaluator.getResult(_infix.value!!)
    }

    private fun evalPercentExpr(): String {
        var percentExpr : Double = _infix.value?.last()!!.toDouble() / 100

        if (_infix.value?.size!! > 2 && _infix.value?.get(_infix.value?.lastIndex!! - 1)!!.equals("+", true))
            percentExpr *= _infix.value?.get(_infix.value?.lastIndex!! - 2)!!.toDouble()

        return percentExpr.toString()
    }

    private fun addToken(element: String) {
        _infix.value?.add(element)
        _infix.value = _infix.value
    }

    private fun concatToLastToken(element: String) {
        _infix.value?.lastIndex?.let { _infix.value?.set(it, _infix.value?.last() + element) }
        _infix.value = _infix.value
    }

    private fun setToken(index: Int, element: String) {
        _infix.value?.set(index, element)
        _infix.value = _infix.value
    }

    fun deleteLastToken() {
        if (_infix.value?.isEmpty() == true)
            return

        var lastToken = _infix.value?.last()

        if (!lastToken.isNullOrEmpty()) {
            lastToken = lastToken.subSequence(0, lastToken.lastIndex) as String

            if (lastToken.isNotEmpty())
                _infix.value?.set(_infix.value?.lastIndex!!, lastToken)
            else
                _infix.value?.removeLast()

            _infix.value = _infix.value
        }

        if (!_infix.value.isNullOrEmpty())
            _result.value = inputEvaluator.getResult(_infix.value!!)
    }

    fun clearAll() {
        Log.d("Calculator", "Values Initialized")
        _result.value = 0.0
        _infix.value = mutableListOf()
    }
}