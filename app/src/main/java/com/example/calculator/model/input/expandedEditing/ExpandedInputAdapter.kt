package com.example.calculator.model.input.expandedEditing

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.util.TypedValue
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.example.calculator.R
import com.example.calculator.model.function.Function
import com.example.calculator.model.function.FunctionBody
import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.input.defaultEditing.Clickable
import com.example.calculator.model.input.defaultEditing.InputAdapter
import com.example.calculator.model.number.NumberKind
import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.model.settings.SettingsManager
import com.example.calculator.model.token.TokenTypes
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.parser.FunctionParser
import com.example.calculator.parser.NumberParser
import com.example.calculator.parser.OperatorParser
import com.example.calculator.viewmodel.CalculatorViewModel
import kotlin.math.roundToInt

class ExpandedInputAdapter(
    activity: FragmentActivity,
    buttons: Buttons,
    viewModel: CalculatorViewModel,
    spannableInput: MutableLiveData<SpannableStringBuilder>
) : InputAdapter(activity, buttons, viewModel, spannableInput) {

    override val what: Clickable
        get() {
            return when (token.type) {
                TokenTypes.Number -> ExpandedClickableNumber(
                    activity,
                    buttons,
                    viewModel,
                    spannableInput,
                    index
                )
                TokenTypes.Operator -> ExpandedClickableOperator(
                    activity,
                    buttons,
                    viewModel,
                    spannableInput,
                    index
                )
                TokenTypes.Function -> ExpandedClickableFunction(
                    activity,
                    buttons,
                    viewModel,
                    spannableInput,
                    index
                )
            }
        }

    override fun setBindings() {
        super.setBindings()

        resetSpannableInput()
        setSpan()

        buttons.functions.forEach { (button, function) ->
            button.setOnClickListener {
                if (viewModel.add(function))
                    setSpan()
            }
        }
    }

    override fun setSpan() {
        if (spannable.isEmpty()) {
            for (i in viewModel.inputAsTokens.indices) {
                index = i

                if (token.type == TokenTypes.Function && FunctionParser.parse<Function>(token).functionBody == FunctionBody.RIGHT_SIDE)
                    setDrawableSpan()
                else {
                    spannable.replace(oldStart, oldEnd, string)
                    spannable.setSpan(newStart, newEnd)
                }
            }
        }
        else {
            index = viewModel.inputAsTokens.lastIndex - 1

            index = if (index >= 0 && token.type == TokenTypes.Function && FunctionParser.parse<Function>(token).functionBody == FunctionBody.RIGHT_SIDE) {
                setDrawableSpan()
                viewModel.inputAsTokens.lastIndex
            } else
                viewModel.inputAsTokens.lastIndex

            if (token == OperatorParser.parse(OperatorKind.SUBTRACTION)) {
                if (index - 2 >= 0 && viewModel.inputAsTokens[index - 2].type == TokenTypes.Operator) {
                    for(i in (index - 1)..viewModel.inputAsTokens.lastIndex) {
                        index = i

                        spannable.replace(oldStart, oldEnd, string)
                        spannable.setSpan(newStart, newEnd)
                    }
                }
            }

            spannable.replace(oldStart, oldEnd, string)
            spannable.setSpan(newStart, newEnd)
        }
    }

    private fun setDrawableSpan() {
        spannable.replace(oldStart, oldEnd, string)

        val drawable = when(token.type) {
            TokenTypes.Function -> buttons.functions[FunctionParser.parse<FunctionKind>(token)]!!.drawable.constantState!!.newDrawable().mutate()
            TokenTypes.Number -> buttons.numbers[NumberParser.parse<NumberKind>(token)]!!.drawable.constantState?.newDrawable()!!.mutate()
            TokenTypes.Operator -> buttons.operators[OperatorParser.parse<OperatorKind>(token)]!!.drawable.constantState!!.newDrawable().mutate()
        }

        val typedValue = TypedValue()
        activity.theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true)

        drawable.setTint(activity.getColor(typedValue.resourceId))
        val size: Int = activity.findViewById<TextView>(R.id.input).textSize.toInt()
        drawable.setBounds(0, 0,  (size / 1.2).roundToInt(), (size / 1.2).roundToInt())

        if (FunctionParser.parse<FunctionKind>(token) == FunctionKind.LOG)
            drawable.setBounds(0, 0, (size * 1.5).roundToInt(), size)


        spannable.setSpan(
            ImageSpan(
                drawable,
                DynamicDrawableSpan.ALIGN_CENTER
            ), newStart, newEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(newStart, newEnd)
    }
}