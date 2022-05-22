package com.example.calculator.model.input.expandedEditing

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.lifecycle.MutableLiveData
import com.example.calculator.model.input.defaultEditing.ClickableOperator
import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.viewmodel.CalculatorViewModel

class ExpandedClickableOperator(
    context: Context,
    buttons: Buttons,
    viewModel: CalculatorViewModel,
    liveInput: MutableLiveData<SpannableStringBuilder>,
    index: Int
) : ClickableOperator(context, buttons, viewModel, liveInput, index) {
    override val what
        get() = ExpandedClickableOperator(context, buttons, viewModel, liveInput, index)

    override fun bindToEditableToken() {
//        super.bindToEditableToken()

        buttons.functions.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }
        buttons.numbers.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }

        buttons.operators.forEach { (button, operator) ->
            if (operator != OperatorKind.LEFT_BRACKET && operator != OperatorKind.RIGHT_BRACKET) {

                button.setOnClickListener {
                    oldString = viewModel.formattedInput[index]

                    if (viewModel.set(operator, index)) {
                        replaceSpan(viewModel.formattedInput[index])
                        applyColorToSpan(highlightedColor, newStart, newEnd)
                    }
                }
            }
        }

        setButtonState(buttons.clear, disabledButtonColor, false)
        setButtonState(buttons.clearAll, disabledButtonColor, false)

    }
}