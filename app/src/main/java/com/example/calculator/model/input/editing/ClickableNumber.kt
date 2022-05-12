package com.example.calculator.model.input.editing

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import androidx.lifecycle.MutableLiveData
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.viewmodel.CalculatorViewModel

class ClickableNumber(
    context: Context,
    buttons: Buttons,
    viewModel: CalculatorViewModel,
    liveInput: MutableLiveData<SpannableStringBuilder>,
    index: Int
    ) : Clickable(context, buttons, viewModel, liveInput, index) {
    override val what get() = ClickableNumber(context, buttons, viewModel, liveInput, index)

    override fun bindToEditableToken() {
        buttons.operators.operatorButtons.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false)}
        buttons.functions.functionButtons.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }

        buttons.numbers.numberButtons.forEach { (button, number) ->
            button?.setOnClickListener {
                val oldString = viewModel.input[index]

                if (viewModel.add(number, index)) {
                    replaceSpan(viewModel.input[index], oldString)
                    applyColorToSpan(highlightedColor, start, end)
                }
            }
        }

        buttons.clear.setOnClickListener {
            val oldString = viewModel.input[index]

            if (viewModel.delete(index)) {
                replaceSpan(viewModel.input[index], oldString)
                applyColorToSpan(highlightedColor, start, end)
            }
        }

        buttons.clearAll.setOnClickListener {
            val oldString = viewModel.input[index]

            if (viewModel.deleteAll(index)) {
                replaceSpan(viewModel.input[index], oldString)
                applyColorToSpan(highlightedColor, start, end)
            }
        }
    }

    private fun replaceSpan(newString: String, oldString: String) {
        val oldEnd = oldString.length + start

        spannable.replace(start, oldEnd, newString)
        spannable.setSpan(start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}