//package com.example.calculator.model.input.expandedEditing
//
//import android.text.SpannableStringBuilder
//import android.view.View
//import androidx.fragment.app.FragmentActivity
//import androidx.lifecycle.MutableLiveData
//import com.example.calculator.model.operator.OperatorKind
//import com.example.calculator.model.wrapper.Buttons
//import com.example.calculator.parser.OperatorParser
//import com.example.calculator.viewmodel.CalculatorViewModel
//
//class ExpandedClickableOperator(
//    activity: FragmentActivity,
//    buttons: Buttons,
//    viewModel: CalculatorViewModel,
//    liveInput: MutableLiveData<SpannableStringBuilder>,
//    index: Int
//) : ExpandedClickable(activity, buttons, viewModel, liveInput, index) {
//    override lateinit var oldString: String
//
//    override val what
//        get() = ExpandedClickableOperator(activity,buttons, viewModel, liveInput, curIndex)
//
//    override fun onClick(view: View) {
//        val leftParenthesis = OperatorParser.parse(OperatorKind.LEFT_BRACKET)
//        val rightParenthesis = OperatorParser.parse(OperatorKind.RIGHT_BRACKET)
//
//        if (viewModel.inputAsTokens[curIndex] != leftParenthesis && viewModel.inputAsTokens[curIndex] != rightParenthesis)
//            super.onClick(view)
//    }
//
//    override fun bindToEditableToken() {
//        buttons.functions.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }
//        buttons.numbers.forEach { (button, _) -> setButtonState(button, disabledButtonColor, false) }
//
//        setButtonState(buttons.clear, disabledButtonColor, false)
//        setButtonState(buttons.clearAll, disabledButtonColor, false)
//
//        buttons.operators.forEach { (button, operator) ->
//            if (operator == OperatorKind.LEFT_BRACKET || operator == OperatorKind.RIGHT_BRACKET)
//                setButtonState(button, disabledButtonColor, false)
//
//            button.setOnClickListener {
//                oldString = viewModel.formattedInput[curIndex]
//
//                if (viewModel.set(operator, curIndex)) {
//                    replaceSpan(viewModel.formattedInput[curIndex])
//                    applyColorToSpan(highlightedColor, newStart, newEnd)
//                }
//            }
//        }
//    }
//}