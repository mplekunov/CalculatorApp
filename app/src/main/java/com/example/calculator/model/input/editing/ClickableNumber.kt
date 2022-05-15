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
    index: Int,
) : Clickable(context, buttons, viewModel, liveInput, index) {
    override val what get() = ClickableNumber(context, buttons, viewModel, liveInput, index)

    override lateinit var oldString: String

    override fun bindToEditableToken() {
        buttons.operators.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false)}
        buttons.functions.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }

        buttons.numbers.forEach { (button, number) ->

            button.setOnClickListener {
                oldString = viewModel.formattedInput[index]

                if (viewModel.add(number, index)) {
                    replaceSpan(viewModel.formattedInput[index])
                    applyColorToSpan(highlightedColor, newStart, newEnd)
                }
            }
        }

        buttons.clear.setOnClickListener {
            oldString = viewModel.formattedInput[index]

            if (viewModel.delete(index)) {
                replaceSpan(viewModel.formattedInput[index])
                applyColorToSpan(highlightedColor, newStart, newEnd)
            }
        }

        buttons.clearAll.setOnClickListener {
            oldString = viewModel.formattedInput[index]

            if (viewModel.deleteAll(index)) {
                replaceSpan(viewModel.formattedInput[index])
                applyColorToSpan(highlightedColor, newStart, newEnd)
            }
        }
    }
}