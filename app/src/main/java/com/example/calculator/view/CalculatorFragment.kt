package com.example.calculator.view

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.widget.ImageViewCompat
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

    private lateinit var operatorsMap: Map<ImageButton, String>

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

        operatorsMap = mapOf(
            binding?.additionSign!! to "+",
            binding?.minusSign!! to "-",
            binding?.multiplicationSign!! to "*",
            binding?.percentSign!! to "%",
            binding?.divisionSign!! to "/"
        )
    }

    fun update() {
        val text = calculatorViewModel.expression.value

        if (text.isNullOrEmpty())
            return

        val input = binding?.input!!
        input.movementMethod = LinkMovementMethod.getInstance()

//        input.setText(text, TextView.BufferType.SPANNABLE)

        input.highlightColor = requireContext().getColor(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)

        val spans = SpannableStringBuilder(text)
//            (input.text as Spannable)

        var pos = 0
        var start = 0
        var end = 0

        Log.d("Calculator", "${calculatorViewModel.tokens}")

        var iterator = BreakIterator.getWordInstance()

        calculatorViewModel.tokens.forEach { token ->
            end += token.length
            spans.setSpan(toClickableSpan(token, pos, start, end), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            start = end
            pos++
        }

        input.text = spans
    }


    private fun toClickableSpan(token: String, pos: Int, start: Int, end: Int): ClickableSpan {
        return object:  ClickableSpan() {
            override fun onClick(view: View) {
                when {
                    InputEvaluator.isNumber(token) -> numbers()
//                    InputEvaluator.isOperator(token) -> tokens()
                }

                val spans = SpannableString(((view as TextView).text.toString()))
                spans.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.calc_image_button_normal)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                view.text = spans

                binding?.equalSign?.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_check, requireContext().theme))
                binding?.equalSign?.setOnClickListener {
                    if (calculatorViewModel.tokens[pos].isEmpty())
                        calculatorViewModel.changeToken("0", pos)

                    rebind()
                    binding?.equalSign?.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_equal, requireContext().theme))
                    update()
                }
            }

            private fun rebind() {
                binding?.calculator?.children?.forEach { it ->
                    if (it == binding?.equalSign)
                        binding?.equalSign?.setOnClickListener { onClickEqualButton() }
                    else if (it == binding?.delete)
                        binding?.delete?.setOnClickListener { deleteToken() }
                    else if (it == binding?.clear)
                        binding?.clear?.setOnClickListener { deleteAll() }
                    else if (operatorsMap.containsKey(it)) {
                        ImageViewCompat.setImageTintList(it as ImageView, ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.calc_image_button_normal)))
                        it.setOnClickListener { sendToViewModel(operatorsMap[it]!!) }
                    }
                    else
                        it.setOnClickListener { sendToViewModel((it as Button).text.toString()) }
                }
            }

            private fun tokens() {
                TODO("Not yet implemented")
            }

            private fun numbers() {
                binding?.calculator?.children?.forEach { it ->
                    if (it == binding?.delete)
                        binding?.delete?.setOnClickListener { deleteTokenAt(pos, start) }
                    else if (it == binding?.clear)
                        binding?.clear?.setOnClickListener { clearAllAt(pos, start) }
                    else if (operatorsMap.containsKey(it)) {
                        ImageViewCompat.setImageTintList(it as ImageButton, ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.calc_button_pressed)))
                        it.setOnClickListener(null)
                    }
                    else
                        it.setOnClickListener { concatTokenAt(it as Button, pos, start) }
                }
            }

            override fun updateDrawState(ds: TextPaint) {
//                ds.isUnderlineText = true
                ds.isUnderlineText = false
//                ds.setColor(ContextCompat.getColor(requireContext(), R.color.calc_image_button_normal))
            }
        }
    }

    fun concatTokenAt(btn: Button, pos: Int, start: Int) {
        calculatorViewModel.concatToken(btn.text.toString(), pos)

        val end = calculatorViewModel.tokens[pos].length + start

        val spans = SpannableString(calculatorViewModel.expression.value)
        spans.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.calc_image_button_normal)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding?.input?.text = spans
    }

    fun clearAllAt(pos: Int, start: Int) {
        calculatorViewModel.clearAllAt(pos)

        val end = calculatorViewModel.tokens[pos].length + start

        val spans = SpannableString(calculatorViewModel.expression.value)
        spans.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.calc_image_button_normal)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding?.input?.text = spans
    }

    fun deleteTokenAt(pos: Int, start: Int) {
        calculatorViewModel.deleteLastTokenAt(pos)

        val end = calculatorViewModel.tokens[pos].length + start

        val spans = SpannableString(calculatorViewModel.expression.value)
        spans.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.calc_image_button_normal)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding?.input?.text = spans
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

    fun deleteAt(pos: Int) {
        calculatorViewModel.deleteLastTokenAt(pos)
    }

    fun onInputChange() {
        if (calculatorViewModel.expression.value.isNullOrEmpty())
            binding?.clear?.text = getText(R.string.all_cleared)
        else
            binding?.clear?.text = getText(R.string.clear)
    }
}