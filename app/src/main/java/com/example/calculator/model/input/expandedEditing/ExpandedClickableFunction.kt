package com.example.calculator.model.input.expandedEditing

import android.app.Activity
import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.example.calculator.R
import com.example.calculator.model.function.Function
import com.example.calculator.model.function.FunctionBody
import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.input.defaultEditing.Clickable
import com.example.calculator.model.number.NumberKind
import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.model.settings.SettingsManager
import com.example.calculator.model.token.TokenTypes
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.parser.FunctionParser
import com.example.calculator.parser.NumberParser
import com.example.calculator.parser.OperatorParser
import com.example.calculator.viewmodel.CalculatorViewModel

class ExpandedClickableFunction(
    activity: FragmentActivity,
    buttons: Buttons,
    viewModel: CalculatorViewModel,
    liveInput: MutableLiveData<SpannableStringBuilder>,
    index: Int
) : ExpandedClickable(activity,  buttons, viewModel, liveInput, index) {

    override lateinit var oldString: String

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

                oldString = viewModel.formattedInput[index]

                if (viewModel.set(function, index)) {
                    replaceSpan(viewModel.formattedInput[index])
                    setDrawableSpan()
                    applyColorToSpan(highlightedColor, newStart, newEnd)
                }
            }
        }
    }
}