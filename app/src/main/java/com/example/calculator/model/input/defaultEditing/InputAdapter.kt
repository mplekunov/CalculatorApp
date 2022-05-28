package com.example.calculator.model.input.defaultEditing

import android.text.Spannable
import android.text.SpannableStringBuilder
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.example.calculator.algorithm.Algorithms
import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.model.token.TokenTypes
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.parser.OperatorParser
import com.example.calculator.viewmodel.CalculatorViewModel

open class InputAdapter(
    val activity: FragmentActivity,
    val buttons: Buttons,
    val viewModel: CalculatorViewModel,
    val spannableInput: MutableLiveData<SpannableStringBuilder>,
) {
    var index = 0

    protected val string get() = viewModel.formattedInput[index]
    protected val token get() = viewModel.inputAsTokens[index]

    protected val oldStart get() = getStartingPos()
    protected val oldEnd get() = spannableInput.value?.length ?: 0

    protected val newStart
        get() = Algorithms.findStartingPosOfPattern(
            string,
            token.toString()
        ) + oldStart

    protected val newEnd get() = token.length + newStart

    val spannable = spannableInput.value ?: SpannableStringBuilder()

    protected open val what: Clickable?
        get() {
            return when (token.type) {
                TokenTypes.Number -> ClickableNumber(
                    activity,
                    buttons,
                    viewModel,
                    spannableInput,
                    index
                )
                TokenTypes.Operator -> ClickableOperator(
                    activity,
                    buttons,
                    viewModel,
                    spannableInput,
                    index
                )
                else -> null
            }
        }

    open fun setBindings() {
        buttons.equal.setOnClickListener {
            viewModel.saveResult()
            resetSpannableInput()
            setSpan()
        }

        buttons.clear.setOnClickListener {
            if (viewModel.delete()) {
                if (viewModel.formattedInput.lastIndex < 0)
                    resetSpannableInput()
                else
                    setSpan()
            }
        }

        buttons.clearAll.setOnClickListener {
            if (viewModel.deleteAll())
                resetSpannableInput()
        }

        buttons.numbers.forEach { (button, number) ->
            button.setOnClickListener {
                if (viewModel.add(number))
                    setSpan()
            }
        }

        buttons.operators.forEach { (button, operator) ->
            button.setOnClickListener {
                if (viewModel.add(operator))
                    setSpan()
            }
        }

        buttons.functions.forEach { (button, function) ->
            button.setOnClickListener {
                if (viewModel.add(function))
                    setSpan()
            }
        }
    }

    protected open fun setSpan() {
        if (spannable.isEmpty()) {
            for (i in viewModel.inputAsTokens.indices) {
                index = i

                spannable.replace(oldStart, oldEnd, string)

                if (what != null)
                    spannable.setSpan(newStart, newEnd)
            }
        }
        else {
            index = viewModel.inputAsTokens.lastIndex

            if (token == OperatorParser.parse(OperatorKind.SUBTRACTION)) {
                if (index - 2 >= 0 && viewModel.inputAsTokens[index - 2].type == TokenTypes.Operator) {
                    for(i in (index - 1)..viewModel.inputAsTokens.lastIndex) {
                        index = i

                        spannable.replace(oldStart, oldEnd, string)
                        spannable.setSpan(newStart, newEnd)
                    }
                }
            }

            spannable.replace(oldStart, oldEnd, string)

            if (what != null)
                spannable.setSpan(newStart, newEnd)
        }
    }

    private fun getStartingPos(): Int {
        var startingIndex = 0
        val list = viewModel.formattedInput.subList(0, index)
        list.forEach { str -> startingIndex += str.length }
        return startingIndex
    }

    protected fun resetSpannableInput() {
        spannable.clearAll()
    }

    fun SpannableStringBuilder.setSpan(start: Int, end: Int) {
        this@setSpan.setSpan(what, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannableInput.value = this@setSpan
    }

    private fun SpannableStringBuilder.clearAll() {
        this@clearAll.clearSpans()
        this@clearAll.clear()

        spannableInput.value = this@clearAll
    }
}

