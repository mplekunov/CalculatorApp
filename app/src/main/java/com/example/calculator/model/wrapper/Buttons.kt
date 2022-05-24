package com.example.calculator.model.wrapper

import android.widget.Button
import android.widget.ImageButton
import com.example.calculator.datastructure.BiMap
import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.number.NumberKind
import com.example.calculator.model.operator.OperatorKind

class   Buttons {
    lateinit var functions: BiMap<ImageButton, FunctionKind>

    lateinit var operators: BiMap<ImageButton, OperatorKind>

    lateinit var numbers: BiMap<ImageButton, NumberKind>

    lateinit var clear: ImageButton

    lateinit var clearAll: Button

    lateinit var equal: ImageButton

    lateinit var changeLayout: ImageButton
}