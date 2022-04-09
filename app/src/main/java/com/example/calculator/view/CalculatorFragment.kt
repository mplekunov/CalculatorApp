package com.example.calculator.view

import android.os.Bundle
import android.text.Spannable
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.calculator.R
import com.example.calculator.algorithms.InputEvaluator
import com.example.calculator.databinding.FragmentCalculatorBinding
import com.example.calculator.viewmodel.CalculatorViewModel
import java.text.BreakIterator

class CalculatorFragment : Fragment() {
    private var binding: FragmentCalculatorBinding? = null
    private val calculatorViewModel: CalculatorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = calculatorViewModel
            calculatorFragment = this@CalculatorFragment
        }
    }

    fun update() {
        val text = calculatorViewModel.expression.value

        if (text.isNullOrEmpty())
            return

        val input = binding?.input!!
        input.movementMethod = LinkMovementMethod.getInstance()

        input.setText(text, TextView.BufferType.SPANNABLE)

        val spans = (input.text as Spannable)
        val iterator = BreakIterator.getWordInstance()

        iterator.setText(text)

        var end = iterator.next()
        var start = iterator.first()

        while (end != BreakIterator.DONE) {
            val token = text.substring(start, end)

            if (!token.isNullOrEmpty() && (InputEvaluator.isNumber(token) || InputEvaluator.isOperator(token)))
                spans.setSpan(toClickableSpan(token), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            start = end
            end = iterator.next()
        }
    }


    private fun toClickableSpan(token: String): ClickableSpan {
        return object:  ClickableSpan() {
            override fun onClick(p0: View) {
                Toast.makeText(view?.context, token, Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
                ds.linkColor = context?.getColor(androidx.appcompat.R.color.material_blue_grey_800)!!
            }
        }
    }

    fun onClickEqualButton() {
        val value = TypedValue()
        context?.theme?.resolveAttribute(android.R.attr.textColorSecondary, value, true)
        binding?.input?.setTextColor(value.data)

        context?.theme?.resolveAttribute(android.R.attr.textColorPrimary, value, true)
        binding?.output?.setTextColor(value.data)

        binding?.output!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40F)
        binding?.input!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)

        calculatorViewModel.useResult()
    }

    fun deleteToken() {
        textStyling()

        calculatorViewModel.deleteLastToken()
    }

    fun deleteAll() {
        textStyling()

        calculatorViewModel.clearAll()
    }

    private fun textStyling() {
        if (binding?.output?.autoSizeTextType == TextViewCompat.AUTO_SIZE_TEXT_TYPE_NONE) {
            val value = TypedValue()
            context?.theme?.resolveAttribute(android.R.attr.textColorSecondary, value, true)
            binding?.output?.setTextColor(value.data)

            context?.theme?.resolveAttribute(android.R.attr.textColorPrimary, value, true)
            binding?.input?.setTextColor(value.data)

            binding?.output!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)
            binding?.input!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40F)
        }
    }

    fun inputTextOnTouch(motionEvent: MotionEvent): Boolean {
//        if (motionEvent.action == MotionEvent.ACTION_DOWN) {
//            binding?.input!!.setTextColor(resources.getColor(R.color.yellow_dark, context?.theme))
//        }

        return true
    }

    fun sendToViewModel(token: String) {
        textStyling()

        calculatorViewModel.parseToken(token)
        update()
    }

    fun onInputChange() {
        if (calculatorViewModel.expression.value.isNullOrEmpty())
            binding?.clear?.text = getText(R.string.all_cleared)
        else
            binding?.clear?.text = getText(R.string.clear)
    }
}