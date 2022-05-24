package com.example.calculator.model.input.expandedEditing

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.calculator.model.input.defaultEditing.Clickable
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.viewmodel.CalculatorViewModel

class ExpandedClickableFunction(
    context: Context,
    buttons: Buttons,
    viewModel: CalculatorViewModel,
    liveInput: MutableLiveData<SpannableStringBuilder>,
    index: Int
) : Clickable(context, buttons, viewModel, liveInput, index) {

    override lateinit var oldString: String

    override val what
        get() = ExpandedClickableFunction(context, buttons, viewModel, liveInput, index)

    override fun onClick(view: View) {
//        super.onClick(view)
    }

    override fun bindToEditableToken() {
        buttons.operators.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }
        buttons.numbers.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }

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