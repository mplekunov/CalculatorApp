package com.example.calculator.model.input.editing

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import androidx.lifecycle.MutableLiveData
import com.example.calculator.algorithm.Algorithms
import com.example.calculator.model.token.TokenTypes
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.viewmodel.CalculatorViewModel

class InputAdapter(
    private val context: Context,
    private val buttons: Buttons,
    private val viewModel: CalculatorViewModel,
    private val spannableInput: MutableLiveData<SpannableStringBuilder>,
) {
    private val index get() = viewModel.formattedInput.lastIndex

    private val newString get() = viewModel.formattedInput[index]

    private val oldStart get() = getStartingPos()
    private val oldEnd get() = spannableInput.value?.length ?: 0

    private val newStart get() = Algorithms.findStartingPosOfPattern(viewModel.formattedInput[index], viewModel.inputAsTokens[index].value) + oldStart
    private val newEnd get() = viewModel.inputAsTokens[index].value.length + newStart

    private val spannable = spannableInput.value ?: SpannableStringBuilder()

    private val what: Clickable
        get() {
            return when(viewModel.inputAsTokens[index].type) {
                TokenTypes.Number -> ClickableNumber(context, buttons, viewModel, spannableInput, index)
                TokenTypes.Function -> ClickableFunction(context, buttons, viewModel, spannableInput, index)
                TokenTypes.Operator -> ClickableOperator(context, buttons, viewModel, spannableInput, index)
            }
        }

    fun setBindings() {
        buttons.equal.setOnClickListener {
            viewModel.saveResult()
            resetSpannableInput()
            replaceSpan()
        }

        buttons.clear.setOnClickListener {
            if (viewModel.delete()) {
                if (viewModel.formattedInput.lastIndex < 0)
                    resetSpannableInput()
                else
                    replaceSpan()
            }
        }

        buttons.clearAll.setOnClickListener {
            if (viewModel.deleteAll())
                resetSpannableInput()
        }

        buttons.numbers.forEach { (button, number) ->
            button.setOnClickListener {
                if (viewModel.add(number))
                    replaceSpan()
            }
        }

        buttons.operators.forEach { (button, operator) ->
            button.setOnClickListener {
                if (viewModel.add(operator))
                    replaceSpan()
            }
        }

        buttons.functions.forEach { (button, function) ->
            button.setOnClickListener {
                if (viewModel.add(function))
                    replaceSpan()
            }
        }
    }

    private fun replaceSpan() {
        spannable.replace(oldStart, oldEnd, newString)
        spannable.setSpan(newStart, newEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun getStartingPos() : Int {
        var startingIndex = 0
        viewModel.formattedInput.subList(0, index).forEach { str -> startingIndex += str.length }
        return startingIndex
    }


    private fun resetSpannableInput() {
        spannable.clearAll()
    }

    private fun SpannableStringBuilder.setSpan(start: Int, end: Int, flags: Int) {
        this@setSpan.setSpan(what, start, end, flags)

        spannableInput.value = this@setSpan
    }

    private fun SpannableStringBuilder.clearAll() {
        this@clearAll.clearSpans()
        this@clearAll.clear()

        spannableInput.value = this@clearAll
    }
}

