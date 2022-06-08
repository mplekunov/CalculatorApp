package com.example.calculator.model.input.expandedEditing

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.util.TypedValue
import android.widget.TextView
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
    override fun getWhat(type: TokenTypes, index: Int): Clickable {
        return when(type) {
            TokenTypes.Function -> ExpandedClickableFunction(
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
            TokenTypes.Number -> ExpandedClickableNumber(
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

        buttons.functions.forEach { (button, function) ->
            button.setOnClickListener {
                val prevIndex = lastIndex

                @Suppress("NAME_SHADOWING")
                if (viewModel.add(function)) {
                    when {
                        prevIndex == lastIndex -> updateSpan(prevIndex)
                        prevIndex < lastIndex -> addSpan(prevIndex + 1)
                    }

                    val token = FunctionParser.parse(function)
                    val function = FunctionParser.parse<Function>(token)

                    if (function.functionBody == FunctionBody.RIGHT_SIDE)
                        setDrawableSpan(prevIndex + 1)
                }
            }
        }
    }

    private fun setDrawableSpan(index: Int) {
        val what = spanMap[index]
        val token = viewModel.inputAsTokens[index]

//        val formattedString = TokenFormatter.convertTokenToString(token, false)
//
////        val frontOffset = Algorithms.findStartingPosOfPattern(formattedString, token.toString())
////        val backOffset = formattedString.length - (token.length + frontOffset)

        val start = spannable.getSpanStart(what)
        val end = spannable.getSpanEnd(what)

        val drawable = when(token.type) {
            TokenTypes.Function -> buttons.functions[FunctionParser.parse<FunctionKind>(token)]!!.drawable.constantState!!.newDrawable().mutate()
            TokenTypes.Number -> buttons.numbers[NumberParser.parse<NumberKind>(token)]!!.drawable.constantState?.newDrawable()!!.mutate()
            TokenTypes.Operator -> buttons.operators[OperatorParser.parse<OperatorKind>(token)]!!.drawable.constantState!!.newDrawable().mutate()
        }

        val typedValue = TypedValue()
        activity.theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true)

        drawable.setTint(SettingsManager(activity.applicationContext).getColor(R.string.saved_input_font_color_key))

        val size: Int = activity.findViewById<TextView>(R.id.input).textSize.toInt()
        drawable.setBounds(0, 0,  (size / 1.3).roundToInt(), (size / 1.3).roundToInt())

        if (FunctionParser.parse<FunctionKind>(token) == FunctionKind.LOG)
            drawable.setBounds(0, 0, (size * 1.4).roundToInt(), size)

        spannable.setSpan(
            ImageSpan(
                drawable,
                DynamicDrawableSpan.ALIGN_CENTER
            ), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableInput.value = spannable
    }
}