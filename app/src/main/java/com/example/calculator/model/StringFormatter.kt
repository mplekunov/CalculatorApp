package com.example.calculator.model

import com.example.calculator.algorithms.InputEvaluator
import java.math.RoundingMode
import java.text.NumberFormat

object StringFormatter {
    private val nf = NumberFormat.getNumberInstance()

    init {
        nf.roundingMode = RoundingMode.HALF_UP
    }

    fun formatInput(input: List<String>): String {
        val inputCopy = mutableListOf<String>().apply { addAll(input) }
        inputCopy.replaceAll { token ->
            if (InputEvaluator.isNumber(token)) {

                var formattedToken = nf.format(nf.parse(token))

                if (token.last() == '.')
                    formattedToken += "."
                else if (token.length >= 2 && token.substring(token.lastIndex - 1, token.length) == ".0")
                    formattedToken += ".0"

                formattedToken
            }
            else
                token
        }

        val formattedInput = StringBuilder()

        inputCopy.forEach{ token -> formattedInput.append(token)}

        return formattedInput.toString()
    }

    fun formatOutput(output: Double): String {
        return nf.format(output.toBigDecimal().setScale(6, RoundingMode.HALF_UP).toDouble())
    }
}