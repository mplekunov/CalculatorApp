package com.example.calculator.model.input.expandedEditing

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.example.calculator.R
import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.input.defaultEditing.Clickable
import com.example.calculator.model.number.NumberKind
import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.model.token.Token
import com.example.calculator.model.token.TokenTypes
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.parser.FunctionParser
import com.example.calculator.parser.NumberParser
import com.example.calculator.parser.OperatorParser
import com.example.calculator.viewmodel.CalculatorViewModel
import kotlin.math.roundToInt

abstract class ExpandedClickable(
    activity: FragmentActivity,
    buttons: Buttons,
    viewModel: CalculatorViewModel,
    liveInput: MutableLiveData<SpannableStringBuilder>,
) : Clickable(activity, buttons, viewModel, liveInput) {
    override fun onClick(view: View) {
        start = spannable.getSpanStart(this)
        end = spannable.getSpanEnd(this)

        resetSpannableFocus()

        setButtonState(buttons.changeLayout, disabledButtonColor, false)
        bindToEditableToken()

        applyColorToSpan(highlightedColor)

        buttons.equal.setImageDrawable(ResourcesCompat.getDrawable(activity.resources, R.drawable.check_mark_ic, activity.theme))

        buttons.equal.setOnClickListener {
            ExpandedInputAdapter(activity, buttons, viewModel, liveInput).setBindings()

            resetSpannableFocus()
            buttons.equal.setImageDrawable(ResourcesCompat.getDrawable(activity.resources, R.drawable.equal_ic, activity.theme))
        }
    }

    override fun resetSpannableFocus() {
        super.resetSpannableFocus()

        for (i in viewModel.inputAsTokens.indices) {
            if (viewModel.inputAsTokens[i].type == TokenTypes.Function)
                applyColorToImageSpan(defaultTextColor, i)
        }
    }

    override fun bindToEditableToken() {}

    fun applyColorToImageSpan(color: Int, index: Int) {
        setDrawableSpan(index, color)
    }

    protected fun setDrawableSpan(index: Int, color: Int = defaultTextColor) {
        val token = viewModel.inputAsTokens[index]

        val drawable = when(token.type) {
            TokenTypes.Function -> buttons.functions[FunctionParser.parse<FunctionKind>(token)]!!.drawable.constantState!!.newDrawable().mutate()
            TokenTypes.Number -> buttons.numbers[NumberParser.parse<NumberKind>(token)]!!.drawable.constantState?.newDrawable()!!.mutate()
            TokenTypes.Operator -> buttons.operators[OperatorParser.parse<OperatorKind>(token)]!!.drawable.constantState!!.newDrawable().mutate()
        }

        drawable.setTint(color)
        val size: Int = activity.findViewById<TextView>(R.id.input).textSize.toInt()
        drawable.setBounds(0, 0,  (size / 1.2).roundToInt(), (size / 1.2).roundToInt())

        if (FunctionParser.parse<FunctionKind>(token) == FunctionKind.LOG)
            drawable.setBounds(0, 0, (size * 1.5).roundToInt(), size)

        spannable.setSpan(
            ImageSpan(
                drawable,
                DynamicDrawableSpan.ALIGN_CENTER
            ), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        liveInput.value = spannable
    }
}