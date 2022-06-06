package com.example.calculator.model.input.defaultEditing

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
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.example.calculator.R
import com.example.calculator.algorithm.Algorithms
import com.example.calculator.formatter.TokenFormatter
import com.example.calculator.model.settings.SettingsManager
import com.example.calculator.model.token.Token
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.viewmodel.CalculatorViewModel
import kotlin.properties.Delegates

abstract class Clickable(
    protected val activity: FragmentActivity,
    protected val buttons: Buttons,
    protected val viewModel: CalculatorViewModel,
    protected val liveInput: MutableLiveData<SpannableStringBuilder>,
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

    protected var start by Delegates.notNull<Int>()
    protected var end by Delegates.notNull<Int>()

    override fun onClick(view: View) {
        start = spannable.getSpanStart(this)
        end = spannable.getSpanEnd(this)

        resetSpannableFocus()

        setButtonState(buttons.changeLayout, disabledButtonColor, false)
        bindToEditableToken()

        applyColorToSpan(highlightedColor)

        buttons.equal.setImageDrawable(
            ResourcesCompat.getDrawable(
                activity.resources,
                R.drawable.check_mark_ic,
                activity.theme
            )
        )

        buttons.equal.setOnClickListener {
            InputAdapter(activity, buttons, viewModel, liveInput).setBindings()

            resetSpannableFocus()
            buttons.equal.setImageDrawable(
                ResourcesCompat.getDrawable(
                    activity.resources,
                    R.drawable.equal_ic,
                    activity.theme
                )
            )
        }
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = false
    }

    protected abstract val what: Clickable

    protected abstract fun bindToEditableToken()

    protected open fun applyColorToSpan(@ColorInt color: Int) {
        spannable.highlight(color, start, end)
    }

    protected fun SpannableStringBuilder.replaceSpan(start: Int, end: Int, flags: Int) {
        this@replaceSpan.setSpan(what, start, end, flags)
        liveInput.value = this@replaceSpan
    }

    protected fun SpannableStringBuilder.highlight(color: Int, start: Int, end: Int) {
        this@highlight.setSpan(ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
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
        spannable.highlight(defaultTextColor, 0, spannable.length)
        setButtonsAsClickable()
    }

    private fun setButtonsAsClickable() {
        setButtonState(buttons.changeLayout, enabledButtonColor)

        buttons.functions.forEach { (button, _) ->
            setButtonState(
                button,
                enabledFunctionButtonColor
            )
        }
        buttons.operators.forEach { (button, _) ->
            setButtonState(
                button,
                enabledOperatorButtonColor
            )
        }
        buttons.numbers.forEach { (button, _) -> setButtonState(button, enabledNumberButtonColor) }

        setButtonState(buttons.clear, clearButtonColor)
        setButtonState(buttons.clearAll, clearAllButtonColor)
        setButtonState(buttons.equal, enabledButtonColor)
    }

    protected open fun replaceSpan(what: Clickable, token: Token, oldToken: Token) {
        val formattedString = TokenFormatter.convertTokenToString(oldToken, false)

//        var frontOffset = Algorithms.findStartingPosOfPattern(formattedString, oldToken.toString())
//        var backOffset = formattedString.length - (oldToken.toString().length + frontOffset)

        val newFormattedString = TokenFormatter.convertTokenToString(token, false)

        val oldStart = start /*- frontOffset*/
        val oldEnd = end /*+ backOffset*/

        spannable.replace(oldStart, oldEnd, newFormattedString)

//        frontOffset = Algorithms.findStartingPosOfPattern(newFormattedString, token.toString())
//        backOffset = newFormattedString.length - (token.toString().length + frontOffset)

        start = oldStart /*+ frontOffset*/
        end = oldStart + newFormattedString.length /*- backOffset*/

        spannable.setSpan(what, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        liveInput.value = spannable
    }
}