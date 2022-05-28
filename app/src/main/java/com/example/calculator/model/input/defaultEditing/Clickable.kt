package com.example.calculator.model.input.defaultEditing

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
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toColorInt
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.example.calculator.R
import com.example.calculator.algorithm.Algorithms
import com.example.calculator.model.settings.SettingsManager
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.viewmodel.CalculatorViewModel

abstract class Clickable(
    protected val activity: FragmentActivity,
    protected val buttons: Buttons,
    protected val viewModel: CalculatorViewModel,
    protected val liveInput: MutableLiveData<SpannableStringBuilder>,
    protected var index: Int
) : ClickableSpan() {
    protected val spannable get() = liveInput.value ?: SpannableStringBuilder()

    private val settingsManager get() = SettingsManager(activity.applicationContext)

    open val highlightedColor get() = settingsManager.getColor(R.string.saved_highlighting_color_key)
    open val defaultTextColor get() = settingsManager.getColor(R.string.saved_input_font_color_key)

    protected val enabledButtonColor get() = settingsManager.getColor(R.string.saved_highlighting_color_key)

    protected val enabledNumberButtonColor get() = settingsManager.getColor(R.string.saved_number_button_color_key)
    protected val enabledFunctionButtonColor get() = settingsManager.getColor(R.string.saved_function_button_color_key)
    protected val enabledOperatorButtonColor get() = settingsManager.getColor(R.string.saved_operator_button_color_key)

    protected val disabledButtonColor get() = settingsManager.getColor(R.string.saved_disabled_button_color_key)

    protected val clearButtonColor get() = settingsManager.getColor(R.string.saved_clear_button_color_key)
    protected val clearAllButtonColor get() = settingsManager.getColor(R.string.saved_clear_all_button_color_key)

    abstract var oldString: String

    protected val oldStart: Int get() = getStartingPos()
    protected val oldEnd: Int get() = oldString.length + oldStart

    protected val newStart: Int get() = Algorithms.findStartingPosOfPattern(viewModel.formattedInput[index], viewModel.inputAsTokens[index].toString()) + oldStart
    protected val newEnd: Int get() = viewModel.inputAsTokens[index].length + newStart

    override fun onClick(view: View) {
        resetSpannableFocus()

        setButtonState(buttons.changeLayout, disabledButtonColor, false)
        bindToEditableToken()

        applyColorToSpan(highlightedColor, newStart, newEnd)

        buttons.equal.setImageDrawable(ResourcesCompat.getDrawable(activity.resources, R.drawable.check_mark_ic, activity.theme))

        buttons.equal.setOnClickListener {
            InputAdapter(activity, buttons, viewModel, liveInput).setBindings()

            resetSpannableFocus()
            buttons.equal.setImageDrawable(ResourcesCompat.getDrawable(activity.resources, R.drawable.equal_ic, activity.theme))
        }
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = false
    }

    protected abstract val what: Clickable

    protected abstract fun bindToEditableToken()

    protected open fun applyColorToSpan(@ColorInt color: Int, start: Int = 0, end: Int = liveInput.value?.length!!) {
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

    protected open fun resetSpannableFocus() {
        applyColorToSpan(defaultTextColor)
        setButtonsAsClickable()
    }

    private fun setButtonsAsClickable() {
        setButtonState(buttons.changeLayout, enabledButtonColor)

        buttons.functions.forEach { (button, _) -> setButtonState(button, enabledFunctionButtonColor) }
        buttons.operators.forEach { (button, _) -> setButtonState(button, enabledOperatorButtonColor) }
        buttons.numbers.forEach { (button, _) -> setButtonState(button, enabledNumberButtonColor) }

        setButtonState(buttons.clear, clearButtonColor)
        setButtonState(buttons.clearAll, clearAllButtonColor)
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