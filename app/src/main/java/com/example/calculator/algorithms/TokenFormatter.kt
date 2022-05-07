package com.example.calculator.algorithms

import com.example.calculator.miscellaneous.TokenTypes
import com.example.calculator.model.Token
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat

/**
 * Helper class that formats [Token] into appropriate format.
 */
object TokenFormatter {
    private val nf = NumberFormat.getNumberInstance()

    init {
        nf.maximumFractionDigits = 10
    }

    /**
     * Converts collection of [Token] to collection of [String] by applying appropriate formatting.
     *
     * @param tokens the collection of [Token].
     * @return collection of formatted [String].
     */
    fun convertTokensToStrings(tokens: List<Token>?): List<String> {
        if (tokens.isNullOrEmpty())
            return emptyList()

        val list = mutableListOf<String>()

        tokens.forEach { list.add(convertTokenToString(it, false)) }
        list[list.lastIndex] += " "

        return list
    }

    /**
     * Converts [Token] to [String] by applying appropriate formatting.
     *
     * @param token the [Token] for conversion.
     * @param removeTrailingZeroes if set to true, all trailing zeroes will be removed (e.g. ".", ".0", ".00").
     * @return formatted [String].
     */
    fun convertTokenToString(token: Token?, removeTrailingZeroes: Boolean): String {
        if (token == null || token.value.isEmpty())
            return " 0 "

        return if (token.type == TokenTypes.Number) {
            var formattedToken = nf.format(BigDecimal(token.value).setScale(10, RoundingMode.HALF_UP))

            if (!removeTrailingZeroes) {
                if (token.value.last() == '.')
                    formattedToken += "."
                else if (token.value.length >= 2 && token.value.substring(token.value.lastIndex - 1, token.value.length) == ".0")
                    formattedToken += ".0"
            }

            " $formattedToken"
        } else
            " ${token.value}"
    }
}