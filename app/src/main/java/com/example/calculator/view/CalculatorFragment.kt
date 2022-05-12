package com.example.calculator.view

import android.os.Bundle

import android.text.method.LinkMovementMethod

import android.view.*

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

import com.example.calculator.R
import com.example.calculator.databinding.FragmentCalculatorBinding
import com.example.calculator.model.text.SpannableInput
import com.example.calculator.model.wrapper.Buttons

import com.example.calculator.viewmodel.CalculatorViewModel

class CalculatorFragment : Fragment() {
    private var binding: FragmentCalculatorBinding? = null
    private val viewModel: CalculatorViewModel by viewModels()

    private lateinit var buttons: Buttons
    private lateinit var spannableInput: SpannableInput

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

//        val typedValue = TypedValue()
//        context?.theme?.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
//        primaryColor = typedValue.data
//
//        context?.theme?.resolveAttribute(android.R.attr.textColorSecondary, typedValue, true)
//        secondaryColor = typedValue.data

        buttons = Buttons(binding!!)

        spannableInput = SpannableInput(requireContext(), binding!!, viewModel)
        spannableInput.setBindings()

        binding?.input?.text = spannableInput.spannableInput
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