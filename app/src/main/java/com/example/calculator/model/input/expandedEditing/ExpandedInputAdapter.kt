package com.example.calculator.model.input.expandedEditing

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.lifecycle.MutableLiveData
import com.example.calculator.model.function.Function
import com.example.calculator.model.function.FunctionBody
import com.example.calculator.model.input.defaultEditing.Clickable
import com.example.calculator.model.input.defaultEditing.InputAdapter
import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.model.token.TokenTypes
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.parser.FunctionParser
import com.example.calculator.viewmodel.CalculatorViewModel

class ExpandedInputAdapter(
    context: Context,
    buttons: Buttons,
    viewModel: CalculatorViewModel,
    spannableInput: MutableLiveData<SpannableStringBuilder>
) : InputAdapter(context, buttons, viewModel, spannableInput) {

    override val what : Clickable
        get() {
            return when(token.type) {
                TokenTypes.Number -> ExpandedClickableNumber(context, buttons, viewModel, spannableInput, index)
                TokenTypes.Operator -> ExpandedClickableOperator(context, buttons, viewModel, spannableInput, index)
                TokenTypes.Function -> ExpandedClickableFunction(context, buttons, viewModel, spannableInput, index)
            }
        }

    override fun setBindings() {
        super.setBindings()

        buttons.functions.forEach { (button, function) ->
            button.setOnClickListener {
                if (viewModel.add(function))
                    replaceSpan()
            }
        }
    }
}