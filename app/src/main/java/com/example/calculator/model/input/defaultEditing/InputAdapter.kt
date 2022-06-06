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
        @JvmStatic
        protected val spanMap = HashMap<Int, Clickable>()
    }

    val spannable = spannableInput.value ?: SpannableStringBuilder()

    protected open fun getWhat(type: TokenTypes, index: Int): Clickable? {
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


    // If prevIndex == lastIndex => Either Added or Removed From/To Current Token
    // If prevIndex < lastIndex => Added one or more new Tokens
    // If prevIndex > lastIndex => Removed one or more old Tokens

    open fun setBindings() {
        buttons.equal.setOnClickListener {
            viewModel.saveResult()
            resetSpannableInput()

            addSpan(0)
        }

        buttons.clear.setOnClickListener {
            val prevIndex = lastIndex

            if (viewModel.delete()) {
                if (viewModel.inputAsTokens.lastIndex < 0)
                    resetSpannableInput()
                else {
                    when {
                        prevIndex == lastIndex -> updateSpan(prevIndex)
                        prevIndex > lastIndex -> removeSpan(prevIndex)
                    }
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

                if (viewModel.add(number)) {
                    when {
                        prevIndex == lastIndex -> updateSpan(prevIndex)
                        prevIndex < lastIndex -> addSpan(prevIndex + 1)
                    }
                }
            }
        }

        buttons.operators.forEach { (button, operator) ->
            button.setOnClickListener {
                val prevIndex = lastIndex

                if (viewModel.add(operator)) {
                    when {
                        prevIndex == lastIndex -> updateSpan(prevIndex)
                        prevIndex < lastIndex -> addSpan(prevIndex + 1)
                    }
                }
            }
        }

        buttons.functions.forEach { (button, function) ->
            button.setOnClickListener {
                val prevIndex = lastIndex

                if (viewModel.add(function)) {
                    when {
                        prevIndex == lastIndex -> updateSpan(prevIndex)
                        prevIndex < lastIndex -> addSpan(prevIndex + 1)
                    }
                }
            }
        }
    }

    protected open fun updateSpan(index: Int) {
        val token = viewModel.inputAsTokens[index]
        val what = spanMap[index]

        val formattedString = TokenFormatter.convertTokenToString(token, false)

        val start = spannable.getSpanStart(what)
        val end = spannable.getSpanEnd(what)

        spannable.replace(start, end, formattedString)

        spanMap.replace(index, getWhat(token.type, index)!!)

        spannable.setSpan(spanMap[index]!!, start, start + formattedString.length)

        spannableInput.value = spannable
    }

    protected open fun addSpan(index: Int) {
        val startIndex = if (index < 0) 0 else index

        for (i in startIndex..viewModel.inputAsTokens.lastIndex) {
            val token = viewModel.inputAsTokens[i]

            val formattedString = TokenFormatter.convertTokenToString(token, false)

            val start = spannable.length

            spannable.replace(spannable.length, spannable.length, formattedString)

            spanMap[i] = getWhat(token.type, i)!!

            spannable.setSpan(spanMap[i]!!, start, spannable.length)
        }

        spannableInput.value = spannable
    }

    protected open fun removeSpan(index: Int) {
        for (i in index downTo viewModel.inputAsTokens.lastIndex + 1) {
            val what = spanMap[i]

            val start = spannable.getSpanStart(what)
            val end = spannable.getSpanEnd(what)

            spannable.delete(start, end)

            spanMap.remove(i)
        }

        spannableInput.value = spannable
    }

    protected fun resetSpannableInput() {
        spannable.clearAll()
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

