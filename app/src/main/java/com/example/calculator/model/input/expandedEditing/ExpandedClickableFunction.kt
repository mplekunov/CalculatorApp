package com.example.calculator.model.input.expandedEditing

import android.text.SpannableStringBuilder
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.example.calculator.R
import com.example.calculator.model.function.Function
import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.parser.FunctionParser
import com.example.calculator.viewmodel.CalculatorViewModel
import kotlin.math.roundToInt

class ExpandedClickableFunction(
    activity: FragmentActivity,
    buttons: Buttons,
    viewModel: CalculatorViewModel,
    liveInput: MutableLiveData<SpannableStringBuilder>,
    val index: Int
) : ExpandedClickable(activity,  buttons, viewModel, liveInput) {
    override val what
        get() = ExpandedClickableFunction(activity, buttons, viewModel, liveInput, index)

    override fun bindToEditableToken() {
        buttons.operators.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }
        buttons.numbers.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }

        setButtonState(buttons.clear, disabledButtonColor, false)
        setButtonState(buttons.clearAll, disabledButtonColor, false)

        setButtonState(buttons.functions[FunctionKind.PERCENTAGE], disabledButtonColor, false)
        setButtonState(buttons.functions[FunctionKind.SQUARED], disabledButtonColor, false)
        setButtonState(buttons.functions[FunctionKind.FACTORIAL], disabledButtonColor, false)

        buttons.functions.forEach { (button, function) ->
            button.setOnClickListener {
                buttons.functions
                    .filter { (_, curKind) ->
                                curKind != FunctionKind.PERCENTAGE &&
                                curKind != FunctionKind.SQUARED &&
                                curKind != FunctionKind.FACTORIAL }
                    .filter { (_, curKind) ->
                    val curBody = FunctionParser.parse<Function>(FunctionParser.parse(curKind)).functionBody
                    val functionBody = FunctionParser.parse<Function>(FunctionParser.parse(function)).functionBody

                    curBody != functionBody }
                    .forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }

                val oldToken = viewModel.inputAsTokens[index]

                if (viewModel.set(function, index)) {
                    replaceSpan(what, viewModel.inputAsTokens[index], oldToken)
                    applyColorToSpan(highlightedColor)
                }
            }
        }
    }
}