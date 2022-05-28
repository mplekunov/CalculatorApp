package com.example.calculator.model.input.defaultEditing

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.viewmodel.CalculatorViewModel

open class ClickableOperator(
    activity: FragmentActivity,
    buttons: Buttons,
    viewModel: CalculatorViewModel,
    liveInput: MutableLiveData<SpannableStringBuilder>,
    index: Int
    ) : Clickable(activity, buttons, viewModel, liveInput, index) {
    override val what get() = ClickableOperator(activity, buttons, viewModel, liveInput, index)

    override lateinit var oldString: String

    override fun bindToEditableToken() {
        buttons.functions.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }
        buttons.numbers.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }

        buttons.operators.forEach { (button, operator) ->
            button.setOnClickListener {
                oldString = viewModel.formattedInput[index]

                if (viewModel.set(operator, index)) {
                    replaceSpan(viewModel.formattedInput[index])
                    applyColorToSpan(highlightedColor, newStart, newEnd)
                }
            }
        }

        setButtonState(buttons.clear, disabledButtonColor, false)
        setButtonState(buttons.clearAll, disabledButtonColor, false)
    }
}