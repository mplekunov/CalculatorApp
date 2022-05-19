package com.example.calculator.model.input.defaultEditing

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.lifecycle.MutableLiveData
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.viewmodel.CalculatorViewModel

open class ClickableFunction(
    context: Context,
    buttons: Buttons,
    viewModel: CalculatorViewModel,
    liveInput: MutableLiveData<SpannableStringBuilder>,
    index: Int
    ) : Clickable(context, buttons, viewModel, liveInput, index) {
    override val what get() = ClickableFunction(context, buttons, viewModel, liveInput, index)

    override lateinit var oldString: String

    override fun bindToEditableToken() {
        buttons.operators.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }
        buttons.numbers.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }

        buttons.functions.forEach { (button, function) ->
            button.setOnClickListener {
                oldString = viewModel.formattedInput[index]

                if (viewModel.set(function, index)) {
                    replaceSpan(viewModel.formattedInput[index])
                    applyColorToSpan(highlightedColor, newStart, newEnd)
                }
            }
        }

        setButtonState(buttons.clear, disabledButtonColor, false)
        setButtonState(buttons.clearAll, disabledButtonColor, false)
    }
}