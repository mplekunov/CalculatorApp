package com.example.calculator.model.text.editing

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import com.example.calculator.databinding.FragmentCalculatorBinding
import com.example.calculator.viewmodel.CalculatorViewModel

class ClickableOperator(
    context: Context,
    binding: FragmentCalculatorBinding,
    viewModel: CalculatorViewModel,
    spannableInput: SpannableStringBuilder,
    index: Int
    ) : Clickable(context, binding, viewModel, spannableInput, index) {
    override val what get() = ClickableOperator(context, binding, viewModel, spannableInput, index)

    override fun bindToEditableToken() {
        buttons.functions.functionButtons.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }
        buttons.numbers.numberButtons.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }

        buttons.operators.operatorButtons.forEach { (button, operator) ->
            button?.setOnClickListener {
                val oldString = viewModel.input[index]

                if (viewModel.set(operator, index)) {
                    replaceSpan(viewModel.input[index], oldString)
                    applyColorToSpan(highlightedColor, start, end)
                }
            }
        }

        setButtonState(buttons.clear, disabledButtonColor, false)
        setButtonState(buttons.clearAll, disabledButtonColor, false)
    }

    private fun replaceSpan(newString: String, oldString: String) {
        val oldEnd = oldString.length + start

        spannableInput.replace(start, oldEnd, newString)

        spannableInput.setSpan(what, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        textView.text = spannableInput
        binding.output.text = viewModel.result
    }
}