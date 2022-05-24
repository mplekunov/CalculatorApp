package com.example.calculator.model.input.expandedEditing

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.calculator.model.input.defaultEditing.ClickableOperator
import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.parser.OperatorParser
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

    override fun onClick(view: View) {
        val leftParenthesis = OperatorParser.parse(OperatorKind.LEFT_BRACKET)
        val rightParenthesis = OperatorParser.parse(OperatorKind.RIGHT_BRACKET)

        if (viewModel.inputAsTokens[index] != leftParenthesis && viewModel.inputAsTokens[index] != rightParenthesis)
            super.onClick(view)
    }

    override fun bindToEditableToken() {
        super.bindToEditableToken()

        buttons.operators.forEach { (button, operator) ->
            if (operator == OperatorKind.LEFT_BRACKET || operator == OperatorKind.RIGHT_BRACKET)
                setButtonState(button, disabledButtonColor, false)

            button.setOnClickListener {
                oldString = viewModel.formattedInput[index]

                if (viewModel.set(operator, index)) {
                    replaceSpan(viewModel.formattedInput[index])
                    applyColorToSpan(highlightedColor, newStart, newEnd)
                }
            }
        }
    }
}