package com.example.calculator.model.input.defaultEditing

import android.text.Spannable
import android.text.SpannableStringBuilder
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.example.calculator.algorithm.Algorithms
import com.example.calculator.formatter.TokenFormatter
import com.example.calculator.model.token.Token
import com.example.calculator.model.token.TokenTypes
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.viewmodel.CalculatorViewModel
import java.lang.reflect.TypeVariable

open class InputAdapter(
    val activity: FragmentActivity,
    val buttons: Buttons,
    val viewModel: CalculatorViewModel,
    val spannableInput: MutableLiveData<SpannableStringBuilder>,
) {
    protected val lastIndex get() = viewModel.inputAsTokens.lastIndex

    companion object {
        protected val spanMap = HashMap<Int, Clickable>()
    }
    val spannable = spannableInput.value ?: SpannableStringBuilder()

    protected fun getWhat(type: TokenTypes, index: Int): Clickable? {
        return when (type) {
            TokenTypes.Number -> ClickableNumber(
                activity,
                buttons,
                viewModel,
                spannableInput,
                index
            )
            TokenTypes.Operator -> ClickableOperator(
                activity,
                buttons,
                viewModel,
                spannableInput,
                index
            )
            else -> null
        }
    }

    protected fun getLastToken(): Token? {
        return if (viewModel.inputAsTokens.isNotEmpty()) viewModel.inputAsTokens.last()
        else null
    }

    open fun setBindings() {
        buttons.equal.setOnClickListener {
            viewModel.saveResult()
            resetSpannableInput()

            for (i in viewModel.inputAsTokens.indices)
                appendSpan(i)
        }

        buttons.clear.setOnClickListener {
            val prevIndex = lastIndex
            val oldToken = getLastToken()

            if (viewModel.delete()) {
                if (viewModel.formattedInput.lastIndex < 0)
                    resetSpannableInput()
                else {
                    if (prevIndex == lastIndex)
                        replaceSpan(viewModel.inputAsTokens.last(), oldToken!!)
                    else
                        removeSpan(oldToken!!, prevIndex)
                }
            }
        }

        buttons.clearAll.setOnClickListener {
            if (viewModel.deleteAll())
                resetSpannableInput()
        }

        buttons.numbers.forEach { (button, number) ->
            button.setOnClickListener {
                val prevIndex = lastIndex
                val oldToken = getLastToken()

                if (viewModel.add(number))
                    if (prevIndex + 1 > lastIndex)
                        replaceSpan(viewModel.inputAsTokens[lastIndex], oldToken!!)
                    else
                        appendSpan(prevIndex + 1)
            }
        }

        buttons.operators.forEach { (button, operator) ->
            button.setOnClickListener {
                val prevIndex = lastIndex
                val oldToken = getLastToken()

                if (viewModel.add(operator))
                    if (prevIndex + 1 > lastIndex)
                        replaceSpan(viewModel.inputAsTokens[lastIndex], oldToken!!)
                    else
                        appendSpan(prevIndex + 1)
            }
        }

        buttons.functions.forEach { (button, function) ->
            button.setOnClickListener {
                val prevIndex = lastIndex
                val oldToken = getLastToken()

                if (viewModel.add(function))
                    if (prevIndex + 1 > lastIndex)
                        replaceSpan(viewModel.inputAsTokens[lastIndex], oldToken!!)
                    else
                        appendSpan(prevIndex + 1)
            }
        }
    }

    protected open fun removeSpan(token: Token, index: Int = lastIndex) {
        val last = spanMap[index]

        val formattedString = TokenFormatter.convertTokenToString(token, false)

        val frontOffset = Algorithms.findStartingPosOfPattern(formattedString, token.toString())
        val start = spannable.getSpanStart(last) - frontOffset

        val backOffset = formattedString.length - (spannable.getSpanEnd(last) - start)
        val end = spannable.getSpanEnd(last) + backOffset

        spannable.delete(start, end)

        spanMap.remove(index)

        spannableInput.value = spannable
    }


    fun appendSpan(index: Int) {
        for (i in index..lastIndex) {
            val what = getWhat(viewModel.inputAsTokens[i].type, i)!!
            spanMap[i] = what

            spannable.append(what, viewModel.inputAsTokens[i])
        }
    }

    fun replaceAllSpans() {
        for (i in viewModel.inputAsTokens.indices) {
            val what = spanMap[i]

            if (viewModel.inputAsTokens[i].type == TokenTypes.Function) {
                spannable.removeSpan(what)
            } else
                replaceSpan(viewModel.inputAsTokens[i], viewModel.inputAsTokens[i], i)
        }
    }

    protected open fun replaceSpan(token: Token, oldToken: Token, index: Int = lastIndex) {
        var what = spanMap[index]!!

        val formattedString = TokenFormatter.convertTokenToString(oldToken, false)

        var frontOffset = Algorithms.findStartingPosOfPattern(formattedString, oldToken.toString())
        var backOffset = formattedString.length - (oldToken.toString().length + frontOffset)

        val oldStart = spannable.getSpanStart(what) - frontOffset
        val oldEnd = spannable.getSpanEnd(what) + backOffset

        val newFormattedString = TokenFormatter.convertTokenToString(token, false)

        spannable.replace(oldStart, oldEnd, newFormattedString)

        frontOffset = Algorithms.findStartingPosOfPattern(newFormattedString, token.toString())
        backOffset = newFormattedString.length - (token.toString().length + frontOffset)

        val newStart = oldStart + frontOffset
        val newEnd = oldStart + newFormattedString.length - backOffset

        what = getWhat(token.type, lastIndex)!!
        spanMap.replace(index, what)

        spannable.setSpan(what, newStart, newEnd)

        spannableInput.value = spannable
    }

    protected fun resetSpannableInput() {
        spannable.clearAll()
    }

    protected open fun SpannableStringBuilder.append(what: Clickable, token: Token) {
        val formattedString = TokenFormatter.convertTokenToString(token, false)

        val frontOffset = Algorithms.findStartingPosOfPattern(formattedString, token.toString())
        val start = spannable.length

        val backOffset = formattedString.length - (token.length + frontOffset)
        val end = formattedString.length + start

        this@append.replace(spannable.length, spannable.length, formattedString)
        this@append.setSpan(what, start + frontOffset, end - backOffset)

        spannableInput.value = this@append
    }

    fun SpannableStringBuilder.setSpan(what: Clickable, start: Int, end: Int) {
        this@setSpan.setSpan(what, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannableInput.value = this@setSpan
    }

    private fun SpannableStringBuilder.clearAll() {
        this@clearAll.clearSpans()
        this@clearAll.clear()

        spannableInput.value = this@clearAll
    }
}

