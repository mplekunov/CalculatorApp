package com.example.calculator.algorithms

import com.example.calculator.model.Function
import com.example.calculator.model.Kind
import com.example.calculator.model.Operator
import com.example.calculator.model.Token
import com.example.calculator.model.contains

/**
 * Helper class that parses string into token
 */
object InputParser {
    /**
     * Parses token [String] into [Token].
     *
     * @param token [String] representation of a token to be parsed.
     * @return [Token] representation of the [String] passed.
     */
    fun parseToken(token: String) : Token {
        var kind: Kind? = null

        when {
            isOperator(token) -> kind = Kind.Operator
            isNumber(token) -> kind = Kind.Number
            isFunction(token) -> kind = Kind.Function
        }

        return Token(kind, token)
    }

    /**
     * Helper function.
     * Finds if token is a function.
     *
     * @param token the token in question.
     * @return [Boolean] indicating the result of the query.
     */
    private fun isFunction(token: String): Boolean = contains<Function>(token)

    /**
     * Helper function.
     * Finds if token is a number.
     *
     * @param token the token in question.
     * @return [Boolean] indicating the result of the query.
     */
    private fun isNumber(token: String): Boolean = token[0].isDigit()

    /**
     * Helper function.
     * Finds if token is an operator.
     *
     * @param token the token in question.
     * @return [Boolean] indicating the result of the query.
     */
    private fun isOperator(token: String): Boolean = contains<Operator>(token)
}