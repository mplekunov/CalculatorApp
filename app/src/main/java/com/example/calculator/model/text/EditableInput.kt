package com.example.calculator.model.text

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.calculator.databinding.FragmentCalculatorBinding
import com.example.calculator.model.text.editing.InputAdapter
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.viewmodel.CalculatorViewModel

class EditableInput(
    private val context: Context,
    private val buttons: Buttons,
    private val viewModel: CalculatorViewModel,
    ) {
    val liveInput = MutableLiveData<SpannableStringBuilder>()

    private lateinit var inputAdapter: InputAdapter

    fun setBindings() {
        inputAdapter = InputAdapter(context, buttons, viewModel, liveInput)
        inputAdapter.setBindings()
    }

}
