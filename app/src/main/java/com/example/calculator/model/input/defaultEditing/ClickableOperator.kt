package com.example.calculator.model.input.defaultEditing

import android.text.SpannableStringBuilder
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.parser.OperatorParser
import com.example.calculator.viewmodel.CalculatorViewModel

open class ClickableOperator(
    activity: FragmentActivity,
    buttons: Buttons,
    viewModel: CalculatorViewModel,
    liveInput: MutableLiveData<SpannableStringBuilder>,
    val index: Int
    ) : Clickable(activity, buttons, viewModel, liveInput) {
    override val what get() = ClickableOperator(activity, buttons, viewModel, liveInput, index)

    override fun onClick(view: View) {
        val leftParenthesis = OperatorParser.parse(OperatorKind.LEFT_BRACKET)
        val rightParenthesis = OperatorParser.parse(OperatorKind.RIGHT_BRACKET)

        if (viewModel.inputAsTokens[index] != leftParenthesis && viewModel.inputAsTokens[index] != rightParenthesis)
            super.onClick(view)
    }

    override fun bindToEditableToken() {
        buttons.functions.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }
        buttons.numbers.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }

        buttons.operators.forEach { (button, operator) ->
            button.setOnClickListener {
                val oldToken = viewModel.inputAsTokens[index]

                if (viewModel.set(operator, index)) {

                    replaceSpan(what, viewModel.inputAsTokens[index], oldToken)
                    applyColorToSpan(highlightedColor)
                }
            }
        }

        setButtonState(buttons.clear, disabledButtonColor, false)
        setButtonState(buttons.clearAll, disabledButtonColor, false)
    }
}