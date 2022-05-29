package com.example.calculator.view

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.SpannableStringBuilder

import android.text.method.LinkMovementMethod
import android.util.Log

import android.view.*
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.*
import androidx.core.widget.ImageViewCompat

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager

import com.example.calculator.R
import com.example.calculator.converter.ColorConverter
import com.example.calculator.databinding.CalculatorExpandedBinding
import com.example.calculator.databinding.CalculatorNormalBinding
import com.example.calculator.databinding.FragmentCalculatorBinding
import com.example.calculator.datastructure.BiMap
import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.input.defaultEditing.InputAdapter
import com.example.calculator.model.input.expandedEditing.ExpandedInputAdapter
import com.example.calculator.model.number.NumberKind
import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.model.settings.SettingsManager
import com.example.calculator.model.wrapper.Buttons

import com.example.calculator.viewmodel.CalculatorViewModel

class CalculatorFragment : Fragment() {
    private var binding: FragmentCalculatorBinding? = null

    private var defaultCalculatorBinding: CalculatorNormalBinding? = null
    private var expandedCalculatorBinding: CalculatorExpandedBinding? = null

    private val viewModel: CalculatorViewModel by viewModels()

    private var buttons = Buttons()

    private lateinit var defaultInputAdapter: InputAdapter
    private lateinit var expandedInputAdapter: ExpandedInputAdapter

    private var liveInput = MutableLiveData<SpannableStringBuilder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalculatorBinding.inflate(inflater, container, false)

        defaultCalculatorBinding = CalculatorNormalBinding.inflate(inflater, null, false)
        expandedCalculatorBinding = CalculatorExpandedBinding.inflate(inflater, null, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            calculatorFragment = this@CalculatorFragment
        }

        // Adds Default layout and assigns binding to default DataViewBinding object
        binding?.calculatorLayout?.addView(defaultCalculatorBinding!!.root)

        // initializes default InputAdapter
        initDefaultBindings()

        // Binds an observer to liveInput...
        // On liveData object modification, updates both input and output textview

        liveInput.observe(viewLifecycleOwner) {
            binding?.input?.text = it
            binding?.output?.text = viewModel.formattedOutput
        }

        // Init for spannable string support
        binding?.input?.movementMethod = LinkMovementMethod.getInstance()
        binding?.input?.highlightColor =
            requireContext().getColor(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)


        // Change Layout Button
        defaultCalculatorBinding?.changeLayout?.setOnClickListener {
            initExpandedBindings()

            binding?.calculatorLayout?.removeAllViews()
            binding?.calculatorLayout?.addView(expandedCalculatorBinding!!.root)
        }

        expandedCalculatorBinding?.changeLayout?.setOnClickListener {
            initDefaultBindings()

            binding?.calculatorLayout?.removeAllViews()
            binding?.calculatorLayout?.addView(defaultCalculatorBinding!!.root)
        }
    }

    private fun applyCalculatorSettings() {
        val settingsManager = SettingsManager(requireContext())

//        binding!!.input.setTextColor(
//            settingsManager.getColor(R.string.saved_input_font_color_key))

        binding!!.output.setTextColor(
            settingsManager.getColor(R.string.saved_output_font_color_key))

        ImageViewCompat.setImageTintList(buttons.clear, ColorStateList.valueOf(
            settingsManager.getColor(R.string.saved_clear_button_color_key)
        ))

        buttons.clearAll.setTextColor(ColorStateList.valueOf(
            settingsManager.getColor(R.string.saved_clear_all_button_color_key)
        ))

        buttons.functions.forEach { (button, _) ->
            ImageViewCompat.setImageTintList(button, ColorStateList.valueOf(
                settingsManager.getColor(R.string.saved_function_button_color_key)
            ))
        }

        buttons.operators.forEach { (button, _) ->
            ImageViewCompat.setImageTintList(button, ColorStateList.valueOf(
                settingsManager.getColor(R.string.saved_operator_button_color_key)
            ))
        }

        buttons.numbers.forEach { (button, _) ->
            ImageViewCompat.setImageTintList(button, ColorStateList.valueOf(
                settingsManager.getColor(R.string.saved_number_button_color_key)
            ))
        }

//        binding!!.input.textSize = settingsManager.getString(R.string.saved_input_font_size_key).toFloat()
//        binding!!.output.textSize = settingsManager.getString(R.string.saved_output_font_size_key).toFloat()
    }

    private fun initExpandedBindings() {
        buttons.changeLayout = expandedCalculatorBinding?.changeLayout!!

        buttons.functions = BiMap<ImageButton, FunctionKind>().apply {
            putAll(
                mutableMapOf(
                    expandedCalculatorBinding?.percent!! to FunctionKind.PERCENTAGE,
                    expandedCalculatorBinding?.ln!! to FunctionKind.NATURAL_LOG,
                    expandedCalculatorBinding?.log!! to FunctionKind.LOG,
                    expandedCalculatorBinding?.squareRoot!! to FunctionKind.SQUARE_ROOT,
                    expandedCalculatorBinding?.squared!! to FunctionKind.SQUARED,
                    expandedCalculatorBinding?.factorial!! to FunctionKind.FACTORIAL
                )
            )
        }

        buttons.operators = BiMap<ImageButton, OperatorKind>().apply {
            putAll(
                mutableMapOf(
                    expandedCalculatorBinding?.add!! to OperatorKind.ADDITION,
                    expandedCalculatorBinding?.subtract!! to OperatorKind.SUBTRACTION,
                    expandedCalculatorBinding?.multiply!! to OperatorKind.MULTIPLICATION,
                    expandedCalculatorBinding?.divide!! to OperatorKind.DIVISION,
                    expandedCalculatorBinding?.leftParenthesis!! to OperatorKind.LEFT_BRACKET,
                    expandedCalculatorBinding?.rightParenthesis!! to OperatorKind.RIGHT_BRACKET,
                    expandedCalculatorBinding?.power!! to OperatorKind.POWER
                )
            )
        }

        buttons.numbers = BiMap<ImageButton, NumberKind>().apply {
            putAll(
                mutableMapOf(
                    expandedCalculatorBinding?.number0!! to NumberKind.ZERO,
                    expandedCalculatorBinding?.number1!! to NumberKind.ONE,
                    expandedCalculatorBinding?.number2!! to NumberKind.TWO,
                    expandedCalculatorBinding?.number3!! to NumberKind.THREE,
                    expandedCalculatorBinding?.number4!! to NumberKind.FOUR,
                    expandedCalculatorBinding?.number5!! to NumberKind.FIVE,
                    expandedCalculatorBinding?.number6!! to NumberKind.SIX,
                    expandedCalculatorBinding?.number7!! to NumberKind.SEVEN,
                    expandedCalculatorBinding?.number8!! to NumberKind.EIGHT,
                    expandedCalculatorBinding?.number9!! to NumberKind.NINE,
                    expandedCalculatorBinding?.dot!! to NumberKind.DOT,
                    expandedCalculatorBinding?.pi!! to NumberKind.PI,
                    expandedCalculatorBinding?.epsilon!! to NumberKind.EPSILON
                )
            )
        }

        buttons.clear = expandedCalculatorBinding?.clear!!
        buttons.clearAll = expandedCalculatorBinding?.clearAll!!
        buttons.equal = expandedCalculatorBinding?.equal!!

        expandedInputAdapter =
            ExpandedInputAdapter(requireActivity(), buttons, viewModel, liveInput)
        expandedInputAdapter.setBindings()

        applyCalculatorSettings()
    }

    private fun initDefaultBindings() {
        buttons.changeLayout = defaultCalculatorBinding?.changeLayout!!

        buttons.functions = BiMap<ImageButton, FunctionKind>().apply {
            putAll(
                mutableMapOf(
                    defaultCalculatorBinding?.percent!! to FunctionKind.PERCENTAGE
                )
            )
        }

        buttons.operators = BiMap<ImageButton, OperatorKind>().apply {
            putAll(
                mutableMapOf(
                    defaultCalculatorBinding?.add!! to OperatorKind.ADDITION,
                    defaultCalculatorBinding?.subtract!! to OperatorKind.SUBTRACTION,
                    defaultCalculatorBinding?.multiply!! to OperatorKind.MULTIPLICATION,
                    defaultCalculatorBinding?.divide!! to OperatorKind.DIVISION
                )
            )
        }

        buttons.numbers = BiMap<ImageButton, NumberKind>().apply {
            putAll(
                mutableMapOf(
                    defaultCalculatorBinding?.number0!! to NumberKind.ZERO,
                    defaultCalculatorBinding?.number1!! to NumberKind.ONE,
                    defaultCalculatorBinding?.number2!! to NumberKind.TWO,
                    defaultCalculatorBinding?.number3!! to NumberKind.THREE,
                    defaultCalculatorBinding?.number4!! to NumberKind.FOUR,
                    defaultCalculatorBinding?.number5!! to NumberKind.FIVE,
                    defaultCalculatorBinding?.number6!! to NumberKind.SIX,
                    defaultCalculatorBinding?.number7!! to NumberKind.SEVEN,
                    defaultCalculatorBinding?.number8!! to NumberKind.EIGHT,
                    defaultCalculatorBinding?.number9!! to NumberKind.NINE,
                    defaultCalculatorBinding?.dot!! to NumberKind.DOT
                )
            )
        }

        buttons.clear = defaultCalculatorBinding?.clear!!
        buttons.clearAll = defaultCalculatorBinding?.clearAll!!
        buttons.equal = defaultCalculatorBinding?.equal!!

        defaultInputAdapter = InputAdapter(requireActivity(), buttons, viewModel, liveInput)
        defaultInputAdapter.setBindings()

        applyCalculatorSettings()
    }

    fun onInputChange() {
        if (viewModel.formattedInput.isEmpty())
            defaultCalculatorBinding?.clearAll?.text = getText(R.string.all_cleared)
        else
            defaultCalculatorBinding?.clearAll?.text = getText(R.string.clear)
    }
}