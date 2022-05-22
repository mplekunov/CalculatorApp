package com.example.calculator.model.input.expandedEditing

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.calculator.model.function.Function
import com.example.calculator.model.function.FunctionBody
import com.example.calculator.model.input.defaultEditing.ClickableFunction
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.parser.FunctionParser
import com.example.calculator.viewmodel.CalculatorViewModel

class ExpandedClickableFunction(
    context: Context,
    buttons: Buttons,
    viewModel: CalculatorViewModel,
    liveInput: MutableLiveData<SpannableStringBuilder>,
    index: Int
) : ClickableFunction(context, buttons, viewModel, liveInput, index) {
    override val what
        get() = ExpandedClickableFunction(context, buttons, viewModel, liveInput, index)

    override fun onClick(view: View) {
//        super.onClick(view)
    }

    override fun bindToEditableToken() {
//        super.bindToEditableToken()
//
//        buttons.functions.forEach { (button, function) ->
//            button.setOnClickListener {
//                    oldString = viewModel.formattedInput[index]
//
//                if (viewModel.set(function, index)) {
//                    replaceSpan(viewModel.formattedInput[index])
//                    applyColorToSpan(highlightedColor, newStart, newEnd)
//                }
//            }
//        }
    }
}