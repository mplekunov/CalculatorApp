package com.example.calculator.model.input.expandedEditing

import android.app.Activity
import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.calculator.R
import com.example.calculator.model.function.Function
import com.example.calculator.model.function.FunctionBody
import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.input.defaultEditing.Clickable
import com.example.calculator.model.number.NumberKind
import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.model.token.TokenTypes
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.parser.FunctionParser
import com.example.calculator.parser.NumberParser
import com.example.calculator.parser.OperatorParser
import com.example.calculator.viewmodel.CalculatorViewModel

class ExpandedClickableFunction(
    context: Context,
    buttons: Buttons,
    viewModel: CalculatorViewModel,
    liveInput: MutableLiveData<SpannableStringBuilder>,
    index: Int
) : Clickable(context, buttons, viewModel, liveInput, index) {

    override lateinit var oldString: String

    override val what
        get() = ExpandedClickableFunction(context, buttons, viewModel, liveInput, index)

    override fun onClick(view: View) {
        super.onClick(view)
    }

    override fun bindToEditableToken() {
        buttons.operators.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }
        buttons.numbers.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }

        setButtonState(buttons.functions[FunctionKind.PERCENTAGE], disabledButtonColor, false)
        setButtonState(buttons.functions[FunctionKind.SQUARED], disabledButtonColor, false)
        setButtonState(buttons.functions[FunctionKind.FACTORIAL], disabledButtonColor, false)

        setButtonState(buttons.clear, disabledButtonColor, false)
        setButtonState(buttons.clearAll, disabledButtonColor, false)

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
                    applyColorToSpan(highlightedColor, newStart, newEnd)
                }
            }
        }
    }


    override fun replaceSpan(newString: String) {
        setDrawableSpan(newString)
    }

    private fun setDrawableSpan(newString: String) {
        spannable.replace(oldStart, oldEnd, newString)

        val token = viewModel.inputAsTokens[index]

        val drawable = when(token.type) {
            TokenTypes.Function -> buttons.functions[FunctionParser.parse<FunctionKind>(token)]!!.drawable.constantState!!.newDrawable().mutate()
            TokenTypes.Number -> buttons.numbers[NumberParser.parse<NumberKind>(token)]!!.drawable.constantState?.newDrawable()!!.mutate()
            TokenTypes.Operator -> buttons.operators[OperatorParser.parse<OperatorKind>(token)]!!.drawable.constantState!!.newDrawable().mutate()
        }

        drawable.setTint(ContextCompat.getColor(context, R.color.white))
        val size: Int = (context as Activity).findViewById<TextView>(R.id.input).textSize.toInt()
        drawable.setBounds(0, 0,  size - 15, size - 20)

        if (FunctionParser.parse<FunctionKind>(token) == FunctionKind.LOG)
            drawable.setBounds(0, 0, size + 45, size)

        spannable.setSpan(
            ImageSpan(
                drawable,
                DynamicDrawableSpan.ALIGN_CENTER
            ), newStart, newEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(newStart, newEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}