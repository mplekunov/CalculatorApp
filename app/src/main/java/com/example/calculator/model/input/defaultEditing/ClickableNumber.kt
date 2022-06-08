package com.example.calculator.model.input.defaultEditing

import android.text.SpannableStringBuilder
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.example.calculator.model.token.Token
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.viewmodel.CalculatorViewModel

open class ClickableNumber(
    activity: FragmentActivity,
    buttons: Buttons,
    viewModel: CalculatorViewModel,
    liveInput: MutableLiveData<SpannableStringBuilder>,
    val index: Int,
) : Clickable(activity, buttons, viewModel, liveInput) {
    override val what get() = ClickableNumber(activity, buttons, viewModel, liveInput, index)

    override fun bindToEditableToken() {
        buttons.operators.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false)}
        buttons.functions.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }

        buttons.numbers.forEach { (button, number) ->

            button.setOnClickListener {
                val oldToken = viewModel.inputAsTokens[index]

                if (viewModel.add(number, index)) {
                    replaceSpan(what, viewModel.inputAsTokens[index], oldToken)
                    applyColorToSpan(highlightedColor)
                }
            }
        }

        buttons.clear.setOnClickListener {
            val oldToken = viewModel.inputAsTokens[index]

            if (viewModel.delete(index)) {
                replaceSpan(what, viewModel.inputAsTokens[index], oldToken)
                applyColorToSpan(highlightedColor)
            }
        }

        buttons.clearAll.setOnClickListener {
            val oldToken = viewModel.inputAsTokens[index]

            if (viewModel.deleteAll(index)) {
                replaceSpan(what, viewModel.inputAsTokens[index], oldToken)
                applyColorToSpan(highlightedColor)
            }
        }
    }
}