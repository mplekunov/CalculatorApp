package com.example.calculator.model.input.expandedEditing

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.lifecycle.MutableLiveData
import com.example.calculator.model.input.defaultEditing.ClickableNumber
import com.example.calculator.model.number.NumberKind
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.viewmodel.CalculatorViewModel

class ExpandedClickableNumber(
    context: Context,
    buttons: Buttons,
    viewModel: CalculatorViewModel,
    liveInput: MutableLiveData<SpannableStringBuilder>,
    index: Int
) : ClickableNumber(context, buttons, viewModel, liveInput, index) {
    override val what
        get() = ExpandedClickableNumber(context, buttons, viewModel, liveInput, index)

    override fun bindToEditableToken() {
        super.bindToEditableToken()

        buttons.numbers.forEach { (button, number) ->
            button.setOnClickListener {
                oldString = viewModel.formattedInput[index]

                if (viewModel.add(number, index)) {
                    replaceSpan(viewModel.formattedInput[index])
                    applyColorToSpan(highlightedColor, newStart, newEnd)
                }
            }
        }

        buttons.numbers.forEach { (button, number) ->
            if (number == NumberKind.EPSILON || number == NumberKind.PI)
                setButtonState(button, disabledButtonColor, false)
        }
    }
}