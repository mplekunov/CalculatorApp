package com.example.calculator.model.input.expandedEditing

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.calculator.R
import com.example.calculator.model.function.Function
import com.example.calculator.model.function.FunctionBody
import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.input.defaultEditing.Clickable
import com.example.calculator.model.input.defaultEditing.InputAdapter
import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.model.token.TokenTypes
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.parser.FunctionParser
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

        buttons.functions.forEach { (button, function) ->
            button.setOnClickListener {
                if (viewModel.add(function))
                    replaceSpan()
            }
        }
    }

    override fun replaceSpan() {
        for (i in viewModel.inputAsTokens.indices) {
            index = i

            spannable.replace(oldStart, oldEnd, string)

            when (token) {
                FunctionParser.parse(FunctionKind.NATURAL_LOG) -> {
                    val drawable = AppCompatResources.getDrawable(context, R.drawable.ln_ic)
                    drawable?.setTint(ContextCompat.getColor(context, R.color.white))
                    drawable?.setBounds(0, 0, 100, 100)

                    spannable.setSpan(
                        ImageSpan(
                            drawable!!,
                            DynamicDrawableSpan.ALIGN_BASELINE
                        ), newStart, newEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                FunctionParser.parse(FunctionKind.LOG) -> {
                    val drawable = AppCompatResources.getDrawable(context, R.drawable.log_ic)
                    drawable?.setTint(ContextCompat.getColor(context, R.color.white))
                    drawable?.setBounds(0, 0, 100, 100)

                    spannable.setSpan(
                        ImageSpan(
                            drawable!!,
                            DynamicDrawableSpan.ALIGN_BASELINE
                        ), newStart, newEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }

            spannable.setSpan(newStart, newEnd)

        }
    }
}