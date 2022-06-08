package com.example.calculator.model.input.defaultEditing

import android.text.Spannable
import android.text.SpannableStringBuilder
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.example.calculator.algorithm.Algorithms
import com.example.calculator.formatter.TokenFormatter
import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.model.token.Token
import com.example.calculator.model.token.TokenTypes
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.parser.FunctionParser
import com.example.calculator.parser.OperatorParser
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
        protected val spanMap = HashMap<Int, Clickable?>()
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

    open fun setBindings() {
        buttons.equal.setOnClickListener {
            viewModel.saveResult()
            resetSpannableInput()

            addSpan(0)
        }

        buttons.clear.setOnClickListener {
            val prevIndex = lastIndex

            val old = mutableListOf<Token>().apply{ addAll(viewModel.inputAsTokens) }

            if (viewModel.delete()) {
                if (viewModel.inputAsTokens.lastIndex < 0)
                    resetSpannableInput()
                else {
                    when {
                        prevIndex == lastIndex -> updateSpan(prevIndex)
                        prevIndex > lastIndex -> {
                            var start = 0
                            for (i in prevIndex downTo lastIndex + 1)
                                start += TokenFormatter.convertTokenToString(old[i], false).length

                            removeSpan(spannable.length - start, spannable.length)
                        }
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

        val clickableSpans = spannable.getSpans(0, spannable.length, Clickable::class.java)
        clickableSpans.forEach { what -> spannable.removeSpan(what) }

        var start = 0
        for (i in viewModel.inputAsTokens.indices) {
            val token = viewModel.inputAsTokens[i]
            val formattedString = TokenFormatter.convertTokenToString(token, false)

            val what = getWhat(token.type, i)
            spanMap[i] = what

            val frontOffset = Algorithms.findStartingPosOfPattern(formattedString, token.toString())
            val backOffset = formattedString.length - (token.toString().length + frontOffset)

            if (what != null)
                spannable.setSpan(spanMap[i]!!, start + frontOffset, start + formattedString.length - backOffset)

            start += formattedString.length
        }
    }

    protected open fun updateSpan(index: Int) {
        val token = viewModel.inputAsTokens[index]
        val what = spanMap[index]

        val formattedString = TokenFormatter.convertTokenToString(token, false)

        val frontOffset = Algorithms.findStartingPosOfPattern(formattedString, token.toString())
        val backOffset = formattedString.length - (token.toString().length + frontOffset)

        val start = spannable.getSpanStart(what) - frontOffset
        val end = spannable.getSpanEnd(what) + backOffset

        spannable.replace(start, end, formattedString)

        spanMap.replace(index, getWhat(token.type, index)!!)

        spannable.setSpan(spanMap[index]!!, start + frontOffset, start + formattedString.length - backOffset)

        spannableInput.value = spannable
    }

    protected open fun addSpan(index: Int) {
        val startIndex = if (index < 0) 0 else index

        for (i in startIndex..viewModel.inputAsTokens.lastIndex) {
            val token = viewModel.inputAsTokens[i]

            val formattedString = TokenFormatter.convertTokenToString(token, false)

            val start = spannable.length

            spannable.replace(spannable.length, spannable.length, formattedString)

            val what = getWhat(token.type, i)

            val frontOffset = Algorithms.findStartingPosOfPattern(formattedString, token.toString())
            val backOffset = formattedString.length - (token.toString().length + frontOffset)

            if (what != null) {
                spanMap[i] = what
                spannable.setSpan(spanMap[i]!!, start + frontOffset, spannable.length - backOffset)
            }
        }

        spannableInput.value = spannable
    }

    protected open fun removeSpan(start: Int, end: Int) {
        spannable.delete(start, end)

        spannableInput.value = spannable
    }

    protected fun resetSpannableInput() {
        spannable.clearAll()
    }

    fun SpannableStringBuilder.setSpan(what: Clickable, start: Int, end: Int) {
        this@setSpan.setSpan(what, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannableInput.value = this@setSpan
    }

    protected fun SpannableStringBuilder.clearAll() {
        this@clearAll.clearSpans()
        this@clearAll.clear()

        spannableInput.value = this@clearAll
    }
}