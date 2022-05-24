package com.example.calculator.model.input.expandedEditing

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat.setTint
import androidx.lifecycle.MutableLiveData
import com.example.calculator.R
import com.example.calculator.model.function.Function
import com.example.calculator.model.function.FunctionBody
import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.input.defaultEditing.Clickable
import com.example.calculator.model.input.defaultEditing.InputAdapter
import com.example.calculator.model.number.NumberKind
import com.example.calculator.model.token.TokenTypes
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.parser.FunctionParser
import com.example.calculator.parser.NumberParser
import com.example.calculator.viewmodel.CalculatorViewModel

class ExpandedInputAdapter(
    context: Context,
    buttons: Buttons,
    viewModel: CalculatorViewModel,
    spannableInput: MutableLiveData<SpannableStringBuilder>
) : InputAdapter(context, buttons, viewModel, spannableInput) {

    override val what: Clickable
        get() {
            return when (token.type) {
                TokenTypes.Number -> ExpandedClickableNumber(
                    context,
                    buttons,
                    viewModel,
                    spannableInput,
                    index
                )
                TokenTypes.Operator -> ExpandedClickableOperator(
                    context,
                    buttons,
                    viewModel,
                    spannableInput,
                    index
                )
                TokenTypes.Function -> ExpandedClickableFunction(
                    context,
                    buttons,
                    viewModel,
                    spannableInput,
                    index
                )
            }
        }

    override fun setBindings() {
        super.setBindings()

//        buttons.numbers.forEach { (button, number) ->
//            button.setOnClickListener {
//                if (viewModel.add(number)) {
//                    if (number == NumberKind.EPSILON || number == NumberKind.PI) {
//                        val drawable = button.drawable.constantState!!.newDrawable().mutate()
//
//                        drawable.setTint(ContextCompat.getColor(context, R.color.white))
//                        drawable.setBounds(0, 0, 80, 80)
//
//                        setSpan(drawable)
//                    }
//                    else
//                        setSpan()
//                }
//            }
//        }

        buttons.functions.forEach { (button, function) ->
            button.setOnClickListener {
                if (viewModel.add(function)) {
                    if (FunctionParser.parse<Function>(FunctionParser.parse(function)).functionBody == FunctionBody.RIGHT_SIDE) {
                        val drawable = button.drawable.constantState!!.newDrawable().mutate()

                        drawable.setTint(ContextCompat.getColor(context, R.color.white))
                        drawable.setBounds(0, 0, 80, 80)

                        setSpan(drawable)
                    }
                    else
                        setSpan()
                }
            }
        }
    }

    private fun setSpan(drawable: Drawable) {
        for (i in viewModel.inputAsTokens.indices) {
            index = i

            spannable.replace(oldStart, oldEnd, string)

            if (token.type == TokenTypes.Function) {
                spannable.setSpan(
                    ImageSpan(
                        drawable,
                        DynamicDrawableSpan.ALIGN_BASELINE
                    ), newStart, newEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            spannable.setSpan(newStart, newEnd)
        }
    }
}