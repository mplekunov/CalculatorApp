package com.example.calculator.model.text

import android.content.Context
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import com.example.calculator.databinding.FragmentCalculatorBinding
import com.example.calculator.miscellaneous.TokenTypes
import com.example.calculator.model.text.editing.ClickableFunction
import com.example.calculator.model.text.editing.ClickableNumber
import com.example.calculator.model.text.editing.ClickableOperator
import com.example.calculator.model.text.editing.InputAdapter
import com.example.calculator.model.wrapper.Buttons
import com.example.calculator.viewmodel.CalculatorViewModel

class SpannableInput(
    val context: Context,
    val binding: FragmentCalculatorBinding,
    val viewModel: CalculatorViewModel,
    ) {
    val spannableInput = SpannableStringBuilder()
    private lateinit var inputAdapter: InputAdapter

    fun setBindings() {
//        spannableInput.filters = arrayOf(object : InputFilter {
//            override fun filter(
//                source: CharSequence?,
//                start: Int,
//                end: Int,
//                dest: Spanned?,
//                dstart: Int,
//                dend: Int
//            ): CharSequence {
//
//            }
//        })

        inputAdapter = InputAdapter(context, binding, viewModel, spannableInput)

        inputAdapter.setBindings()
    }

}
