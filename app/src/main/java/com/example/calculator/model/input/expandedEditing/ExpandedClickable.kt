package com.example.calculator.model.input.expandedEditing

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat.setTint
import androidx.core.text.getSpans
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

        val imageSpans = spannable.getSpans<ImageSpan>(0, spannable.length)
        imageSpans.forEach { imageSpan ->
            val drawable = imageSpan.drawable

            drawable.setTint(defaultTextColor)

            val start = spannable.getSpanStart(imageSpan)
            val end = spannable.getSpanEnd(imageSpan)

            spannable.removeSpan(imageSpan)

            spannable.setSpan(
                ImageSpan(
                    drawable,
                    DynamicDrawableSpan.ALIGN_CENTER
                ), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        liveInput.value = spannable
    }

    override fun replaceSpan(what: Clickable, token: Token, oldToken: Token) {
        super.replaceSpan(what, token, oldToken)

        if (token.type == TokenTypes.Function && token != oldToken) {
            val drawable = buttons.functions[FunctionParser.parse<FunctionKind>(token)]!!.drawable.constantState!!.newDrawable().mutate()

            val size: Int = activity.findViewById<TextView>(R.id.input).textSize.toInt()
            drawable.setBounds(0, 0,  (size / 1.3).roundToInt(), (size / 1.3).roundToInt())

            if (FunctionParser.parse<FunctionKind>(token) == FunctionKind.LOG)
                drawable.setBounds(0, 0, (size * 1.4).roundToInt(), size)

            setDrawableSpan(drawable, highlightedColor)
        }
    }

    protected fun setDrawableSpan(drawable: Drawable, color: Int) {
        drawable.setTint(color)

        val spans = spannable.getSpans<ImageSpan>(start, end)

        if (spans.isNotEmpty())
            spannable.removeSpan(spans.first())

        spannable.setSpan(
            ImageSpan(
                drawable,
                DynamicDrawableSpan.ALIGN_CENTER
            ), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        liveInput.value = spannable
    }

    override fun bindToEditableToken() {}

    override fun applyColorToSpan(color: Int) {
        super.applyColorToSpan(color)

        val spans = spannable.getSpans<ImageSpan>(start, end)

        if (spans.isNotEmpty()) {
            val drawable = spans.first().drawable
            setDrawableSpan(drawable, color)
        }
    }
}