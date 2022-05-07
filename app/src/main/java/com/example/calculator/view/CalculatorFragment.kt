package com.example.calculator.view

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.calculator.R
import com.example.calculator.databinding.FragmentCalculatorBinding
import com.example.calculator.algorithms.TokenFormatter
import com.example.calculator.miscellaneous.Functions
import com.example.calculator.miscellaneous.Numbers
import com.example.calculator.miscellaneous.Operators
import com.example.calculator.miscellaneous.TokenTypes
import com.example.calculator.model.Function
import com.example.calculator.model.Number
import com.example.calculator.model.Operator
import com.example.calculator.model.Token
import com.example.calculator.viewmodel.CalculatorViewModel

class CalculatorFragment : Fragment() {
    private var binding: FragmentCalculatorBinding? = null
    private val viewModel: CalculatorViewModel by viewModels()

    private val digitButtons = mapOf(
        binding?.number0 to Numbers.ZERO,
        binding?.number1 to Numbers.ONE,
        binding?.number2 to Numbers.TWO,
        binding?.number3 to Numbers.THREE,
        binding?.number4 to Numbers.FOUR,
        binding?.number5 to Numbers.FIVE,
        binding?.number6 to Numbers.SIX,
        binding?.number7 to Numbers.SEVEN,
        binding?.number8 to Numbers.EIGHT,
        binding?.number9 to Numbers.NINE,
        binding?.dotSign to Numbers.DOT
    )

    private val operatorButtons = mapOf(
        binding?.additionSign to Operators.ADDITION,
        binding?.subtractionSign to Operators.SUBTRACTION,
        binding?.divisionSign to Operators.DIVISION,
        binding?.multiplicationSign to Operators.MULTIPLICATION
    )

    private val functionButtons = mapOf(
        binding?.percentSign to Functions.PERCENTAGE
    )

    @ColorInt
    private var primaryColor: Int = 0
    @ColorInt
    private var secondaryColor: Int = 0

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
            calculatorFragment = this@CalculatorFragment
        }

        val typedValue = TypedValue()
        context?.theme?.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
        primaryColor = typedValue.data

        context?.theme?.resolveAttribute(android.R.attr.textColorSecondary, typedValue, true)
        secondaryColor = typedValue.data

    }

    private fun setClickableInputField() {
        val tokens = viewModel.inputAsTokens

        val tokensAsStrings = TokenFormatter.convertTokensToStrings(tokens)
        val sb = StringBuilder()
        tokensAsStrings.forEach { sb.append(it) }

        val text = sb.toString()

        setOutputField()

        val input = binding?.input!!

        if (text.isEmpty()) {
            input.text = ""
            return
        }

        input.movementMethod = LinkMovementMethod.getInstance()
        input.highlightColor = requireContext().getColor(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)

        val spans = SpannableStringBuilder(text)

        var start = 0
        var end = 0

        for (index in tokensAsStrings.indices) {
            end += tokensAsStrings[index].length
            spans.setSpan(toClickableSpan(tokens[index], index, start, end), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            start = end
        }

        input.text = spans
    }

    private fun applyEditingStyle(view: TextView, start: Int, end: Int, @ColorInt color: Int) {
        val spans = SpannableString(view.text.toString())
        spans.setSpan(ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        view.text = spans
    }

    private fun toClickableSpan(token: Token, index: Int, start: Int, end: Int): ClickableSpan {
        return object :  ClickableSpan() {
            override fun onClick(view: View) {
                when {
                    token.type == TokenTypes.Number -> bindNumbersToEditableToken()
                    token.type == TokenTypes.Operator -> bindOperatorsToEditableToken()
                }

                applyEditingStyle(view as TextView, start, end, ContextCompat.getColor(requireContext(), R.color.calc_image_button_normal))

                binding?.equalSign?.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_check, requireContext().theme))
                binding?.equalSign?.setOnClickListener {
                    setDefaultBindings()

                    binding?.equalSign?.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_equal, requireContext().theme))
                    setInputField()
                }
            }

            private fun bindOperatorsToEditableToken() {
                binding?.calculator?.children?.forEach { child ->
                    when {
                        binding?.additionSign?.setOnClickListener {
                            viewModel
                        }

                        operatorsMap.containsKey(child) -> {
                            child.setOnClickListener {
                                viewModel.setTokenAt(operatorsMap[child], index)

                                onInputEdit(start, index, ContextCompat.getColor(requireContext(), R.color.calc_image_button_normal))
                            }
                        }
                        child == binding?.equalSign -> {}
                        else -> { disableButton(child, ContextCompat.getColor(requireContext(), R.color.calc_button_pressed)) }
                    }
                }
            }

            private fun bindNumbersToEditableToken() {
                binding?.calculator?.children?.forEach { child ->
                    when {
                        child == binding?.delete -> binding?.delete?.setOnClickListener {
                            viewModel.deleteTokenAt(index)

                            onInputEdit(start, index, ContextCompat.getColor(requireContext(), R.color.calc_image_button_normal))
                        }
                        child == binding?.deleteAll -> binding?.deleteAll?.setOnClickListener {
                            viewModel.deleteAllTokensAt(index)

                            onInputEdit(start, index, ContextCompat.getColor(requireContext(), R.color.calc_image_button_normal))
                        }
                        operatorsMap.containsKey(child) -> { disableButton(child, ContextCompat.getColor(requireContext(), R.color.calc_button_pressed)) }
                        child == binding?.percentSign -> { disableButton(child, ContextCompat.getColor(requireContext(), R.color.calc_button_pressed)) }
                        child == binding?.changeLayout -> { disableButton(child, ContextCompat.getColor(requireContext(), R.color.calc_button_pressed)) }
                        else -> child.setOnClickListener {
                            viewModel.addTokenAt((it as Button).text.toString(), index)

                            onInputEdit(start, index, ContextCompat.getColor(requireContext(), R.color.calc_image_button_normal))
                        }
                    }
                }
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }
    }

//    private fun disableButton(btn: View, @ColorInt color: Int) {
//        if (btn is ImageButton)
//            ImageViewCompat.setImageTintList(btn, ColorStateList.valueOf(color))
//        else
//            (btn as Button).setTextColor(color)
//
//        btn.setOnClickListener(null)
//        btn.isClickable = false
//    }

//    private fun enableButton(btn: View, @ColorInt color: Int) {
//        if (btn is ImageButton)
//            ImageViewCompat.setImageTintList(btn, ColorStateList.valueOf(color))
//        else
//            (btn as Button).setTextColor(color)
//    }

//    private fun onInputEdit(start: Int, index: Int, @ColorInt color: Int) {
//        val tokens = TokenFormatter.convertTokensToStrings(viewModel.inputAsTokens)
//        val sb = StringBuilder()
//        tokens.forEach { sb.append(it) }
//
//        binding?.input?.text = sb.toString()
//
//        val end = tokens[index].length + start
//
//        applyEditingStyle(binding?.input!!, start, end, color)
//        setOutputField()
//    }

    // For Equal button
    fun setDefaultCalculatorButtons() {
        binding?.equalSign?.setOnClickListener {
            applyInputOutputStyling(20F, 40F, secondaryColor, primaryColor)
            viewModel.saveResult()
        }

        binding?.delete?.setOnClickListener {
            applyInputOutputStyling(40F, 20F, primaryColor, secondaryColor)
            viewModel.deleteToken()
        }

        binding?.deleteAll?.setOnClickListener {
            applyInputOutputStyling(40F, 20F, primaryColor, secondaryColor)
            viewModel.deleteAllTokens()
        }

        digitButtons.forEach { (button, number) ->
            button?.setOnClickListener {
                applyInputOutputStyling(40F, 20F, primaryColor, secondaryColor)
                viewModel.addToken(Number.parseNumber(number)!!)
            }
        }

        operatorButtons.forEach { (button, operator) ->
            button?.setOnClickListener {
                applyInputOutputStyling(40F, 20F, primaryColor, secondaryColor)
                viewModel.addToken(Operator.parseOperator(operator)!!)
            }
        }

        functionButtons.forEach { (button, function) ->
            button?.setOnClickListener {
                applyInputOutputStyling(40F, 20F, primaryColor, secondaryColor)
                viewModel.addToken(Function.parseFunction(function)!!)
            }
        }

        //setInputField()
    }


//    private fun setDefaultBindings() {
//        binding?.calculator?.children?.forEach { child ->
//            enableButton(child, ContextCompat.getColor(requireContext(), R.color.calc_image_button_normal))
//
//            when {
//                child == binding?.percentSign -> {
//                    child.setOnClickListener {
//                        applyInputOutputStyling(40F, 20F, primaryColor, secondaryColor)
//
//                        viewModel.addToken(Function.PERCENTAGE.operator)
//                        setInputField()
//                    }
//                }
//                operatorsMap.containsKey(child) -> {
//                    child.setOnClickListener {
//                        applyInputOutputStyling(40F, 20F, primaryColor, secondaryColor)
//
//                        viewModel.addToken(operatorsMap[it]!!)
//                        setInputField()
//                    }
//                }
//                child == binding?.changeLayout -> {}
//                else -> {
//                    (child as Button).setTextColor(primaryColor)
//                    child.setOnClickListener {
//                        applyInputOutputStyling(40F, 20F, primaryColor, secondaryColor)
//
//                        viewModel.addToken((it as Button).text.toString())
//                        setInputField()
//                    }
//                }
//            }
//        }
//    }

    private fun setOutputField() {
        binding?.output?.text = TokenFormatter.convertTokenToString(viewModel.resultOfExpression, true)
    }

    private fun applyInputOutputStyling(min: Float, max: Float, @ColorInt primaryColor: Int, @ColorInt secondaryColor: Int) {
        binding?.input?.setTextColor(primaryColor)
        binding?.output?.setTextColor(secondaryColor)

        binding?.output!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, max)
        binding?.input!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, min)
    }

    fun onInputChange() {
        if (viewModel.inputAsTokens.isEmpty() )
            binding?.deleteAll?.text = getText(R.string.all_cleared)
        else
            binding?.deleteAll?.text = getText(R.string.clear)
    }
}