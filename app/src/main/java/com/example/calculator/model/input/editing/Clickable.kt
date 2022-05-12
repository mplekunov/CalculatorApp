package com.example.calculator.model.input.editing

import android.content.Context
import android.content.res.ColorStateList
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.MutableLiveData
import com.example.calculator.R
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.viewmodel.CalculatorViewModel

abstract class Clickable(
    protected val context: Context,
    protected val buttons: Buttons,
    protected val viewModel: CalculatorViewModel,
    protected val liveInput: MutableLiveData<SpannableStringBuilder>,
    protected val index: Int
) : ClickableSpan() {
    protected val spannable get() = liveInput.value ?: SpannableStringBuilder()

    open var highlightedColor: Int = ResourcesCompat.getColor(context.resources, R.color.yellow_dark, context.theme)
    open var defaultTextColor: Int = ResourcesCompat.getColor(context.resources, R.color.white, context.theme)

    private var enabledButtonColor: Int = ResourcesCompat.getColor(context.resources, R.color.yellow_dark, context.theme)

    open var enabledNumberButtonsColor: Int = ResourcesCompat.getColor(context.resources, R.color.white, context.theme)
    open var enabledFunctionButtonsColor: Int = enabledButtonColor
    open var enabledOperatorButtonsColor: Int = enabledButtonColor

    open var disabledButtonColor: Int = ResourcesCompat.getColor(context.resources, R.color.grey, context.theme)

    protected open val start : Int get()
    {
        var startingIndex = 0
        viewModel.input.subList(0, index).forEach { str -> startingIndex += str.length }
        return startingIndex
    }

    protected val end: Int get() { return viewModel.input[index].length + start}

    private lateinit var textView: TextView

    override fun onClick(view: View) {
        textView = view as TextView

        resetSpannableFocus()
        bindToEditableToken()

        applyColorToSpan(highlightedColor, start, end)

        buttons.equal.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_check, context.theme))

        buttons.equal.setOnClickListener {
            InputAdapter(context, buttons, viewModel, liveInput).setBindings()
            resetSpannableFocus()
            buttons.equal.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_equal, context.theme))
        }
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = false
    }

    protected abstract val what: Any

    protected abstract fun bindToEditableToken()

    protected fun applyColorToSpan(@ColorInt color: Int, start: Int = 0, end: Int = liveInput.value?.length!!) {
        spannable.highlight(color, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    fun SpannableStringBuilder.setSpan(start: Int, end: Int, flags: Int) {
        this@setSpan.setSpan(what, start, end, flags)
        liveInput.value = this@setSpan
    }

    private fun SpannableStringBuilder.highlight(color: Int, start: Int, end: Int, flags: Int) {
        this@highlight.setSpan(ForegroundColorSpan(color), start, end, flags)
        liveInput.value = this@highlight
    }


    protected fun setButtonState(btn: View?, @ColorInt color: Int, isClickable: Boolean = true) {
        if (btn is ImageButton)
            ImageViewCompat.setImageTintList(btn, ColorStateList.valueOf(color))
        else
            (btn as Button).setTextColor(color)

        btn.isClickable = isClickable
    }

    private fun resetSpannableFocus() {
        applyColorToSpan(defaultTextColor)
        setButtonsAsClickable()
    }

    private fun setButtonsAsClickable() {
        buttons.functions.functionButtons.forEach { (button, _) -> setButtonState(button, enabledFunctionButtonsColor) }
        buttons.operators.operatorButtons.forEach { (button, _) -> setButtonState(button, enabledOperatorButtonsColor) }
        buttons.numbers.numberButtons.forEach { (button, _) -> setButtonState(button, enabledNumberButtonsColor) }

        setButtonState(buttons.clear, enabledButtonColor)
        setButtonState(buttons.clearAll, enabledButtonColor)
        setButtonState(buttons.equal, enabledButtonColor)
    }
}