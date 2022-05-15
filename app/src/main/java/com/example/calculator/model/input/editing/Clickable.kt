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
import com.example.calculator.algorithm.Algorithms
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

    open var enabledNumberButtonColor: Int = ResourcesCompat.getColor(context.resources, R.color.white, context.theme)
    open var enabledFunctionButtonColor: Int = enabledButtonColor
    open var enabledOperatorButtonColor: Int = enabledButtonColor

    open var disabledButtonColor: Int = ResourcesCompat.getColor(context.resources, R.color.grey, context.theme)

    abstract var oldString: String

    protected val oldStart: Int get() = getStartingPos()
    protected val oldEnd: Int get() = oldString.length + oldStart

    protected val newStart: Int get() = Algorithms.findStartingPosOfPattern(viewModel.formattedInput[index], viewModel.inputAsTokens[index].value) + oldStart
    protected val newEnd: Int get() = viewModel.inputAsTokens[index].value.length + newStart

    override fun onClick(view: View) {
        resetSpannableFocus()
        bindToEditableToken()

        applyColorToSpan(highlightedColor, newStart, newEnd)

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

    protected abstract val what: Clickable

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
        buttons.functions.forEach { (button, _) -> setButtonState(button, enabledFunctionButtonColor) }
        buttons.operators.forEach { (button, _) -> setButtonState(button, enabledOperatorButtonColor) }
        buttons.numbers.forEach { (button, _) -> setButtonState(button, enabledNumberButtonColor) }

        setButtonState(buttons.clear, enabledButtonColor)
        setButtonState(buttons.clearAll, enabledButtonColor)
        setButtonState(buttons.equal, enabledButtonColor)
    }

    private fun getStartingPos() : Int {
        var startingIndex = 0
        viewModel.formattedInput.subList(0, index).forEach { str -> startingIndex += str.length }
        return startingIndex
    }

    protected fun replaceSpan(newString: String) {
        spannable.replace(oldStart, oldEnd, newString)
        spannable.setSpan(newStart, newEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}