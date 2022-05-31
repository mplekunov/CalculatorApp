//package com.example.calculator.model.input.expandedEditing
//
//import android.text.Spannable
//import android.text.SpannableStringBuilder
//import android.text.style.DynamicDrawableSpan
//import android.text.style.ImageSpan
//import android.view.View
//import android.widget.TextView
//import androidx.core.content.res.ResourcesCompat
//import androidx.fragment.app.FragmentActivity
//import androidx.lifecycle.MutableLiveData
//import com.example.calculator.R
//import com.example.calculator.model.function.FunctionKind
//import com.example.calculator.model.input.defaultEditing.Clickable
//import com.example.calculator.model.number.NumberKind
//import com.example.calculator.model.operator.OperatorKind
//import com.example.calculator.model.token.TokenTypes
//import com.example.calculator.model.wrapper.Buttons
//import com.example.calculator.parser.FunctionParser
//import com.example.calculator.parser.NumberParser
//import com.example.calculator.parser.OperatorParser
//import com.example.calculator.viewmodel.CalculatorViewModel
//import kotlin.math.roundToInt
//
//abstract class ExpandedClickable(
//    activity: FragmentActivity,
//    buttons: Buttons,
//    viewModel: CalculatorViewModel,
//    liveInput: MutableLiveData<SpannableStringBuilder>,
//    index: Int
//) : Clickable(activity, buttons, viewModel, liveInput, index) {
//    private val token get() = viewModel.inputAsTokens[curIndex]
//
//    override fun onClick(view: View) {
//        resetSpannableFocus()
//
//        setButtonState(buttons.changeLayout, disabledButtonColor, false)
//        bindToEditableToken()
//
//        applyColorToSpan(highlightedColor, newStart, newEnd)
//
//        buttons.equal.setImageDrawable(ResourcesCompat.getDrawable(activity.resources, R.drawable.check_mark_ic, activity.theme))
//
//        buttons.equal.setOnClickListener {
//            ExpandedInputAdapter(activity, buttons, viewModel, liveInput).setBindings()
//
//            resetSpannableFocus()
//            buttons.equal.setImageDrawable(ResourcesCompat.getDrawable(activity.resources, R.drawable.equal_ic, activity.theme))
//        }
//    }
//
//    override fun resetSpannableFocus() {
//        super.resetSpannableFocus()
//
//        val originalIndex = curIndex
//
//        for (i in viewModel.inputAsTokens.indices) {
//            curIndex = i
//            if (token.type == TokenTypes.Function)
//                setDrawableSpan(defaultTextColor)
//        }
//
//        curIndex = originalIndex
//    }
//
//    override fun bindToEditableToken() {}
//
//    override fun applyColorToSpan(color: Int, start: Int, end: Int) {
//        super.applyColorToSpan(color, start, end)
//
//        if (token.type == TokenTypes.Function)
//            setDrawableSpan(color)
//    }
//
//    protected fun setDrawableSpan(color: Int = defaultTextColor, start: Int = newStart, end: Int = newEnd) {
//        val drawable = when(token.type) {
//            TokenTypes.Function -> buttons.functions[FunctionParser.parse<FunctionKind>(token)]!!.drawable.constantState!!.newDrawable().mutate()
//            TokenTypes.Number -> buttons.numbers[NumberParser.parse<NumberKind>(token)]!!.drawable.constantState?.newDrawable()!!.mutate()
//            TokenTypes.Operator -> buttons.operators[OperatorParser.parse<OperatorKind>(token)]!!.drawable.constantState!!.newDrawable().mutate()
//        }
//
//        drawable.setTint(color)
//        val size: Int = activity.findViewById<TextView>(R.id.input).textSize.toInt()
//        drawable.setBounds(0, 0,  (size / 1.2).roundToInt(), (size / 1.2).roundToInt())
//
//        if (FunctionParser.parse<FunctionKind>(token) == FunctionKind.LOG)
//            drawable.setBounds(0, 0, (size * 1.5).roundToInt(), size)
//
//        spannable.setSpan(
//            ImageSpan(
//                drawable,
//                DynamicDrawableSpan.ALIGN_CENTER
//            ), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//
//        liveInput.value = spannable
//    }
//}