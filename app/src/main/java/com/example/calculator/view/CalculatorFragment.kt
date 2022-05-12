package com.example.calculator.view

import android.os.Bundle
import android.text.SpannableStringBuilder

import android.text.method.LinkMovementMethod

import android.view.*

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer

import com.example.calculator.R
import com.example.calculator.databinding.FragmentCalculatorBinding
import com.example.calculator.model.input.EditableInput
import com.example.calculator.model.wrapper.Buttons

import com.example.calculator.viewmodel.CalculatorViewModel

class CalculatorFragment : Fragment() {
    private var binding: FragmentCalculatorBinding? = null
    private val viewModel: CalculatorViewModel by viewModels()

    private lateinit var buttons: Buttons
    private lateinit var editingMode: EditableInput

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            calculatorFragment = this@CalculatorFragment
        }

        buttons = Buttons(binding!!)

        editingMode = EditableInput(requireContext(), buttons, viewModel)
        editingMode.setBindings()

        val observer = Observer<SpannableStringBuilder> {
            binding?.input?.text = it
            binding?.output?.text = viewModel.result
        }

        editingMode.liveInput.observe(viewLifecycleOwner, observer)

        binding?.input?.movementMethod = LinkMovementMethod.getInstance()
        binding?.input?.highlightColor = requireContext().getColor(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)
    }

//
//    private fun applyInputOutputStyling(min: Float, max: Float, @ColorInt primaryColor: Int, @ColorInt secondaryColor: Int) {
//        binding?.input?.setTextColor(primaryColor)
//        binding?.output?.setTextColor(secondaryColor)
//
//        binding?.output!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, max)
//        binding?.input!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, min)
//    }

    fun onInputChange() {
        if (viewModel.input.isEmpty() )
            binding?.deleteAll?.text = getText(R.string.all_cleared)
        else
            binding?.deleteAll?.text = getText(R.string.clear)
    }
}