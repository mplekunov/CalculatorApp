package com.example.calculator.model.input.defaultEditing

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.example.calculator.model.settings.SettingsManager
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.viewmodel.CalculatorViewModel

open class ClickableNumber(
    activity: FragmentActivity,
    buttons: Buttons,
    viewModel: CalculatorViewModel,
    liveInput: MutableLiveData<SpannableStringBuilder>,
    index: Int,
) : Clickable(activity, buttons, viewModel, liveInput, index) {
    override val what get() = ClickableNumber(activity, buttons, viewModel, liveInput, index)

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