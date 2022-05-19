package com.example.calculator.model.wrapper

import android.view.View
import android.widget.Button
import android.widget.ImageButton
import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.number.NumberKind
import com.example.calculator.model.operator.OperatorKind

class   Buttons {
    lateinit var functions: MutableMap<View, FunctionKind>

    lateinit var operators: MutableMap<View, OperatorKind>

    lateinit var numbers: MutableMap<View, NumberKind>

    lateinit var clear: ImageButton

    lateinit var clearAll: Button

    lateinit var equal: ImageButton
}