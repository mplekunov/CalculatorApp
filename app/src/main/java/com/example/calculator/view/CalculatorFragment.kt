package com.example.calculator.view

import android.content.res.ColorStateList

import android.os.Bundle

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log

import android.util.TypedValue

import android.view.*

import android.widget.*

import androidx.annotation.ColorInt

import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.ImageViewCompat

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

import com.example.calculator.R
import com.example.calculator.databinding.FragmentCalculatorBinding
import com.example.calculator.formatter.TokenFormatter

import com.example.calculator.miscellaneous.Functions
import com.example.calculator.miscellaneous.Numbers
import com.example.calculator.miscellaneous.Operators
import com.example.calculator.miscellaneous.TokenTypes
import com.example.calculator.model.Token

import com.example.calculator.viewmodel.CalculatorViewModel
import kotlin.math.abs

class CalculatorFragment : Fragment() {
    private var binding: FragmentCalculatorBinding? = null
    private val viewModel: CalculatorViewModel by viewModels()

    private lateinit var digitButtons: Map<View?, Numbers>

    private lateinit var operatorButtons: Map<View?, Operators>

    private lateinit var functionButtons: Map<View?, Functions>

    private val spannableInput: SpannableStringBuilder = SpannableStringBuilder()

    @ColorInt
    private var primaryColor: Int = 0
    @ColorInt
    private var secondaryColor: Int = 0

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

        val typedValue = TypedValue()
        context?.theme?.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
        primaryColor = typedValue.data

        context?.theme?.resolveAttribute(android.R.attr.textColorSecondary, typedValue, true)
        secondaryColor = typedValue.data

        binding?.input?.text = spannableInput
        binding?.input?.movementMethod = LinkMovementMethod.getInstance()
        binding?.input?.highlightColor = requireContext().getColor(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)

        initDigitButtons()
        initOperatorButtons()
        initFunctionButtons()

        setDefaultButtonBindings()
    }

    private fun initOperatorButtons() {
        operatorButtons = mapOf(
            binding?.additionSign to Operators.ADDITION,
            binding?.subtractionSign to Operators.SUBTRACTION,
            binding?.divisionSign to Operators.DIVISION,
            binding?.multiplicationSign to Operators.MULTIPLICATION
        )
    }

    private fun initFunctionButtons() {
        functionButtons = mapOf(
            binding?.percentSign to Functions.PERCENTAGE
        )
    }

    private fun initDigitButtons() {
        digitButtons = mapOf(
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
    }

    private fun disableButton(btn: View, @ColorInt color: Int) {
        if (btn is ImageButton)
            ImageViewCompat.setImageTintList(btn, ColorStateList.valueOf(color))
        else
            (btn as Button).setTextColor(color)

        btn.isClickable = false
    }

    private fun enableButton(btn: View, @ColorInt color: Int) {
        if (btn is ImageButton)
            ImageViewCompat.setImageTintList(btn, ColorStateList.valueOf(color))
        else
            (btn as Button).setTextColor(color)

        btn.isClickable = true
    }

    private fun replaceTokenInSpannableString(newToken: Token, oldToken: Token? = null, index: Int = viewModel.inputAsTokens.lastIndex) {
        val start = if (index < 0) 0 else getStartingPosOfTokenAt(index)

        val oldEnd =
            if (oldToken != null)
                start + TokenFormatter.convertTokenToString(oldToken, false).length
            else
                spannableInput.length

        val newEnd = start + TokenFormatter.convertTokenToString(newToken, false).length

        spannableInput.replace(start, oldEnd, TokenFormatter.convertTokenToString(newToken, false))

        val what = toClickableSpan(newToken, index)
        spannableInput.setSpan(what, start + 1, newEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding?.input?.text = spannableInput
        binding?.output?.text = viewModel.result
    }

    private fun getStartingPosOfTokenAt(index: Int) : Int {
        var startingIndex = 0
        viewModel.input.subList(0, index).forEach { token -> startingIndex += token.length }
        return startingIndex
    }

    private fun resetSpannableInput() {
        spannableInput.clearSpans()
        spannableInput.clear()

        binding?.input?.text = spannableInput
        binding?.output?.text = viewModel.result
    }

    fun setDefaultButtonBindings() {
        binding?.equalSign?.setOnClickListener {
            applyInputOutputStyling(20F, 40F, secondaryColor, primaryColor)
            viewModel.saveResult()

            resetSpannableInput()
            replaceTokenInSpannableString(viewModel.inputAsTokens[viewModel.inputAsTokens.lastIndex])
        }

        binding?.delete?.setOnClickListener {
            applyInputOutputStyling(40F, 20F, primaryColor, secondaryColor)

            if (viewModel.delete()) {
                if (viewModel.inputAsTokens.lastIndex > 0) {
                    val newToken = viewModel.inputAsTokens[viewModel.inputAsTokens.lastIndex]
                    replaceTokenInSpannableString(newToken)
                }
                else
                    resetSpannableInput()
            }
        }

        binding?.deleteAll?.setOnClickListener {
            applyInputOutputStyling(40F, 20F, primaryColor, secondaryColor)

            if (viewModel.deleteAll())
                resetSpannableInput()
        }

        digitButtons.forEach { (button, number) ->
            button?.setOnClickListener {
                applyInputOutputStyling(40F, 20F, primaryColor, secondaryColor)

                if (viewModel.add(number)) {
                    val newToken = viewModel.inputAsTokens[viewModel.inputAsTokens.lastIndex]
                    replaceTokenInSpannableString(newToken)
                }
            }
        }

        operatorButtons.forEach { (button, operator) ->
            button?.setOnClickListener {
                applyInputOutputStyling(40F, 20F, primaryColor, secondaryColor)

                if (viewModel.add(operator)) {
                    val newToken = viewModel.inputAsTokens[viewModel.inputAsTokens.lastIndex]
                    replaceTokenInSpannableString(newToken)
                }
            }
        }

        functionButtons.forEach { (button, function) ->
            button?.setOnClickListener {
                applyInputOutputStyling(40F, 20F, primaryColor, secondaryColor)

                if (viewModel.add(function)) {
                    val newToken = viewModel.inputAsTokens[viewModel.inputAsTokens.lastIndex]
                    replaceTokenInSpannableString(newToken)
                }
            }
        }
    }

    private fun applyColorToSpan(view: TextView, start: Int, end: Int, @ColorInt color: Int) {
        spannableInput.setSpan(ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        view.text = spannableInput
    }

    private fun toClickableSpan(token: Token, index: Int): ClickableSpan {
        return object :  ClickableSpan() {
            val start: Int get() = getStartingPosOfTokenAt(index)
            val end: Int get() = start + TokenFormatter.convertTokenToString(viewModel.inputAsTokens[index], false).length

            override fun onClick(view: View) {
                // Resets All other "selected" spannable
                resetSpannableFocus(view as TextView)
                applyColorToSpan(view, start, end, ContextCompat.getColor(requireContext(), R.color.calc_image_button_normal))

                when (token.type) {
                    TokenTypes.Number -> bindNumbersToEditableToken()
                    TokenTypes.Operator -> bindOperatorsToEditableToken()
                    TokenTypes.Function -> bindFunctionsToEditableToken()
                }


                binding?.equalSign?.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_check, requireContext().theme))
                binding?.equalSign?.setOnClickListener {
                    resetSpannableFocus(view)
                    binding?.equalSign?.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_equal, requireContext().theme))
                }
            }

            private fun resetSpannableFocus(view: TextView) {
                applyColorToSpan(view, 0, spannableInput.length, ContextCompat.getColor(requireContext(), R.color.white))
                setDefaultButtonBindings()
                setButtonsAsClickable()
            }

            private fun bindFunctionsToEditableToken() {
                operatorButtons.forEach { (button, _) ->
                    disableButton(button!!, ContextCompat.getColor(requireContext(), R.color.calc_button_pressed))
                }

                functionButtons.forEach { (button, function) ->
                    button?.setOnClickListener {
                        val oldToken = viewModel.inputAsTokens[index]

                        if (viewModel.set(function, index)) {

                            val newToken = viewModel.inputAsTokens[index]

                            replaceTokenInSpannableString(newToken, oldToken, index)

                            applyColorToSpan(
                                binding?.input!!,
                                start,
                                end,
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.calc_image_button_normal
                                )
                            )
                        }
                    }
                }

                digitButtons.forEach { (button, _) ->
                    disableButton(button!!,ContextCompat.getColor(requireContext(), R.color.calc_button_pressed))
                }

                disableButton(binding?.delete!!, ContextCompat.getColor(requireContext(), R.color.calc_button_pressed))
                disableButton(binding?.deleteAll!!, ContextCompat.getColor(requireContext(), R.color.calc_button_pressed))
            }

            private fun bindOperatorsToEditableToken() {
                operatorButtons.forEach { (button, operator) ->
                    button?.setOnClickListener {
                        val oldToken = viewModel.inputAsTokens[index]

                        if (viewModel.set(operator, index)) {
                            val newToken = viewModel.inputAsTokens[index]

                            replaceTokenInSpannableString(newToken, oldToken, index)

                            applyColorToSpan(
                                binding?.input!!,
                                start,
                                end,
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.calc_image_button_normal
                                )
                            )
                        }
                    }
                }

                functionButtons.forEach { (button, _) ->
                    disableButton(button!!,ContextCompat.getColor(requireContext(), R.color.calc_button_pressed))
                }

                digitButtons.forEach { (button, _) ->
                    disableButton(button!!,ContextCompat.getColor(requireContext(), R.color.calc_button_pressed))
                }

                disableButton(binding?.delete!!, ContextCompat.getColor(requireContext(), R.color.calc_button_pressed))
                disableButton(binding?.deleteAll!!, ContextCompat.getColor(requireContext(), R.color.calc_button_pressed))
            }

            private fun bindNumbersToEditableToken() {
                operatorButtons.forEach { (button, _) ->
                    disableButton(button!!,ContextCompat.getColor(requireContext(), R.color.calc_button_pressed))
                }

                functionButtons.forEach { (button, _) ->
                    disableButton(button!!,ContextCompat.getColor(requireContext(), R.color.calc_button_pressed))
                }

                digitButtons.forEach { (button, number) ->
                    button?.setOnClickListener {
                        val oldToken = viewModel.inputAsTokens[index]

                        if (viewModel.add(number, index)) {
                            val newToken = viewModel.inputAsTokens[index]

                            replaceTokenInSpannableString(newToken, oldToken, index)

                            applyColorToSpan(
                                binding?.input!!,
                                start,
                                end,
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.calc_image_button_normal
                                )
                            )
                        }
                    }
                }

                binding?.delete?.setOnClickListener {
                    val oldToken = viewModel.inputAsTokens[index]

                    if (viewModel.deleteAt(index)) {
                        val newToken = viewModel.inputAsTokens[index]

                        replaceTokenInSpannableString(newToken, oldToken, index)

                        applyColorToSpan(
                            binding?.input!!,
                            start,
                            end,
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.calc_image_button_normal
                            )
                        )
                    }
                }

                binding?.deleteAll?.setOnClickListener {
                    val oldToken = viewModel.inputAsTokens[index]

                    if (viewModel.deleteAllAt(index)) {
                        val newToken = viewModel.inputAsTokens[index]

                        replaceTokenInSpannableString(newToken, oldToken, index)

                        applyColorToSpan(
                            binding?.input!!,
                            start,
                            end,
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.calc_image_button_normal
                            )
                        )
                    }
                }
            }

            private fun setButtonsAsClickable() {
                operatorButtons.forEach { (button, _) -> enableButton(button!!, ContextCompat.getColor(requireContext(), R.color.calc_image_button_normal)) }
                functionButtons.forEach { (button, _) -> enableButton(button!!, ContextCompat.getColor(requireContext(), R.color.calc_image_button_normal)) }
                digitButtons.forEach { (button, _) -> enableButton(button!!, primaryColor)}
                enableButton(binding?.deleteAll!!, ContextCompat.getColor(requireContext(), R.color.calc_image_button_normal))
                enableButton(binding?.delete!!, ContextCompat.getColor(requireContext(), R.color.calc_image_button_normal))
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }
    }

    private fun applyInputOutputStyling(min: Float, max: Float, @ColorInt primaryColor: Int, @ColorInt secondaryColor: Int) {
        binding?.input?.setTextColor(primaryColor)
        binding?.output?.setTextColor(secondaryColor)

        binding?.output!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, max)
        binding?.input!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, min)
    }

    fun onInputChange() {
        if (viewModel.input.isEmpty() )
            binding?.deleteAll?.text = getText(R.string.all_cleared)
        else
            binding?.deleteAll?.text = getText(R.string.clear)
    }
}