package com.example.calculator.model.wrapper

import android.widget.Button
import android.widget.ImageButton
import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.number.NumberKind
import com.example.calculator.model.operator.OperatorKind

class   Buttons {
    lateinit var functions: MutableMap<ImageButton, FunctionKind>

    lateinit var operators: MutableMap<ImageButton, OperatorKind>

    lateinit var numbers: MutableMap<ImageButton, NumberKind>

    lateinit var clear: ImageButton

    lateinit var clearAll: Button

    lateinit var equal: ImageButton

    lateinit var changeLayout: ImageButton
}