package com.example.calculator.model.input.expandedEditing

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.lifecycle.MutableLiveData
import com.example.calculator.model.input.defaultEditing.ClickableOperator
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.viewmodel.CalculatorViewModel

class ExpandedClickableOperator(
    context: Context,
    buttons: Buttons,
    viewModel: CalculatorViewModel,
    liveInput: MutableLiveData<SpannableStringBuilder>,
    index: Int
) : ClickableOperator(context, buttons, viewModel, liveInput, index) {
    override val what
        get() = ExpandedClickableOperator(context, buttons, viewModel, liveInput, index)

    override fun bindToEditableToken() {
        super.bindToEditableToken()

    }
}