package com.example.calculator.formatter

import com.example.calculator.model.number.Number
import com.example.calculator.model.number.NumberKind
import com.example.calculator.model.token.TokenTypes

import com.example.calculator.model.token.Token
import com.example.calculator.parser.NumberParser

import java.math.BigDecimal

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
        if (token == null || token.isEmpty())
            return " 0 "

        return if (token.type == TokenTypes.Number) {
            return when {
                token == NumberParser.parse(NumberKind.INFINITY) -> " $token"
                token.last() == NumberParser.parse(NumberKind.DOT) -> " $token"
                else -> {
                    if (removeTrailingZeroes)
                        " ${BigDecimal(token.toString()).stripTrailingZeros()}"
                    else
                        " ${BigDecimal(token.toString())}"
                }
            }
        } else
            " $token"
    }
}