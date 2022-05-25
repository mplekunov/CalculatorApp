package com.example.calculator.model.input.defaultEditing

import android.content.Context
import android.content.res.ColorStateList
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
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


    open var highlightedColor: Int = ResourcesCompat.getColor(context.resources, R.color.highlighted_text, context.theme)
    open var defaultTextColor: Int = ResourcesCompat.getColor(context.resources, R.color.default_text, context.theme)

    private var enabledButtonColor: Int = ResourcesCompat.getColor(context.resources, R.color.calc_default_function_button, context.theme)

    open var enabledNumberButtonColor: Int = context.resolveColorAttr(android.R.attr.textColorPrimary)
    open var enabledFunctionButtonColor: Int = ResourcesCompat.getColor(context.resources, R.color.calc_default_function_button, context.theme)
    open var enabledOperatorButtonColor: Int = ResourcesCompat.getColor(context.resources, R.color.calc_default_operation_button, context.theme)

    open var disabledButtonColor: Int = ResourcesCompat.getColor(context.resources, R.color.calc_button_pressed, context.theme)

    abstract var oldString: String

    protected val oldStart: Int get() = getStartingPos()
    protected val oldEnd: Int get() = oldString.length + oldStart

    protected val newStart: Int get() = Algorithms.findStartingPosOfPattern(viewModel.formattedInput[index], viewModel.inputAsTokens[index].toString()) + oldStart
    protected val newEnd: Int get() = viewModel.inputAsTokens[index].length + newStart

    @ColorInt
    private fun Context.resolveColorAttr(@AttrRes colorAttr: Int): Int {
        val resolvedAttr = resolveThemeAttr(colorAttr)
        // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
        val colorRes = if (resolvedAttr.resourceId != 0) resolvedAttr.resourceId else resolvedAttr.data
        return ContextCompat.getColor(this, colorRes)
    }

    private fun Context.resolveThemeAttr(@AttrRes attrRes: Int): TypedValue {
        val typedValue = TypedValue()
        theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue
    }

    override fun onClick(view: View) {
        resetSpannableFocus()

        setButtonState(buttons.changeLayout, disabledButtonColor, false)
        bindToEditableToken()

        applyColorToSpan(highlightedColor, newStart, newEnd)

        buttons.equal.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.check_mark_ic, context.theme))

        buttons.equal.setOnClickListener {
            InputAdapter(context, buttons, viewModel, liveInput).setBindings()

            resetSpannableFocus()
            buttons.equal.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.equal_ic, context.theme))
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

    protected fun SpannableStringBuilder.setSpan(start: Int, end: Int, flags: Int) {
        this@setSpan.setSpan(what, start, end, flags)
        liveInput.value = this@setSpan
    }

    protected fun SpannableStringBuilder.highlight(color: Int, start: Int, end: Int, flags: Int) {
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
        setButtonState(buttons.changeLayout, enabledButtonColor)

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

    protected open fun replaceSpan(newString: String) {
        spannable.replace(oldStart, oldEnd, newString)
        spannable.setSpan(newStart, newEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}