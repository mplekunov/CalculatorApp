package com.example.calculator.model

import com.example.calculator.algorithms.InputEvaluator
import java.text.NumberFormat

object StringFormatter {
    private val nf = NumberFormat.getNumberInstance()

    fun formatInput(input: List<String>): String {
        val inputCopy = mutableListOf<String>().apply { addAll(input) }
        inputCopy.replaceAll { token ->
            if (InputEvaluator.isNumber(token))
                nf.format(NumberFormat.getNumberInstance().parse(token))
            else
                token
        }

        val formattedInput = StringBuilder()

        inputCopy.forEach{ token -> formattedInput.append(token)}

        return formattedInput.toString()
    }

    fun formatOutput(output: Double): String {
        return nf.format(output)
    }
}