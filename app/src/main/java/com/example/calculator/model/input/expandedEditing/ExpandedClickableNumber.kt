package com.example.calculator.model.input.expandedEditing

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.example.calculator.model.input.defaultEditing.ClickableNumber
import com.example.calculator.model.number.NumberKind
import com.example.calculator.model.settings.SettingsManager
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.viewmodel.CalculatorViewModel

class ExpandedClickableNumber(
    activity: FragmentActivity,
    buttons: Buttons,
    viewModel: CalculatorViewModel,
    liveInput: MutableLiveData<SpannableStringBuilder>,
    index: Int
) : ExpandedClickable(activity, buttons, viewModel, liveInput, index) {
    override lateinit var oldString: String

    override val what
        get() = ExpandedClickableNumber(activity, buttons, viewModel, liveInput, index)

    override fun bindToEditableToken() {
        buttons.operators.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false)}
        buttons.functions.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }

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

        buttons.numbers.forEach { (button, number) ->
            button.setOnClickListener {
                oldString = viewModel.formattedInput[index]

                if (viewModel.add(number, index)) {
                    replaceSpan(viewModel.formattedInput[index])
                    applyColorToSpan(highlightedColor, newStart, newEnd)
                }
            }
        }
    }
}