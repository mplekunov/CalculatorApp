package com.example.calculator.model.text.editing

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import androidx.lifecycle.MutableLiveData
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.viewmodel.CalculatorViewModel

class ClickableOperator(
    context: Context,
    buttons: Buttons,
    viewModel: CalculatorViewModel,
    liveInput: MutableLiveData<SpannableStringBuilder>,
    index: Int
    ) : Clickable(context, buttons, viewModel, liveInput, index) {
    override val what get() = ClickableOperator(context, buttons, viewModel, liveInput, index)

    override fun bindToEditableToken() {
        buttons.functions.functionButtons.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }
        buttons.numbers.numberButtons.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }

        buttons.operators.operatorButtons.forEach { (button, operator) ->
            button?.setOnClickListener {
                val oldString = viewModel.input[index]

                if (viewModel.set(operator, index)) {
                    replaceSpan(viewModel.input[index], oldString)
                    applyColorToSpan(highlightedColor, start, end)
                }
            }
        }

        setButtonState(buttons.clear, disabledButtonColor, false)
        setButtonState(buttons.clearAll, disabledButtonColor, false)
    }

    private fun replaceSpan(newString: String, oldString: String) {
        val oldEnd = oldString.length + start

        spannable.replace(start, oldEnd, newString)
        spannable.setSpan(start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}