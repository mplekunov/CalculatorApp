package com.example.calculator.model.text.editing

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import androidx.lifecycle.MutableLiveData
import com.example.calculator.databinding.FragmentCalculatorBinding
import com.example.calculator.miscellaneous.TokenTypes
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.viewmodel.CalculatorViewModel

class InputAdapter(
    private val context: Context,
    private val buttons: Buttons,
    private val viewModel: CalculatorViewModel,
    private val spannableInput: MutableLiveData<SpannableStringBuilder>,
) {
    private val index get() = viewModel.input.lastIndex

    private val newString get() = viewModel.input[index]

    private val start get() = getStartingPos()

    private val newEnd get() = newString.length + start
    private val oldEnd get() = spannableInput.value?.length ?: 0

    private val spannable = spannableInput.value ?: SpannableStringBuilder()


    private val what: Any
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
                if (index > 0)
                    replaceSpan()
                else
                    resetSpannableInput()
            }
        }

        buttons.clearAll.setOnClickListener {
            if (viewModel.deleteAll())
                resetSpannableInput()
        }

        buttons.numbers.numberButtons.forEach { (button, number) ->
            button?.setOnClickListener {
                if (viewModel.add(number))
                    replaceSpan()
            }
        }

        buttons.operators.operatorButtons.forEach { (button, operator) ->
            button?.setOnClickListener {
                if (viewModel.add(operator))
                    replaceSpan()
            }
        }

        buttons.functions.functionButtons.forEach { (button, function) ->
            button?.setOnClickListener {
                if (viewModel.add(function))
                    replaceSpan()
            }
        }
    }

    private fun replaceSpan() {
        spannable.replace(start, oldEnd, newString)
        spannable.setSpan(start, newEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun getStartingPos() : Int {
        var startingIndex = 0
        viewModel.input.subList(0, index).forEach { str -> startingIndex += str.length }
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

