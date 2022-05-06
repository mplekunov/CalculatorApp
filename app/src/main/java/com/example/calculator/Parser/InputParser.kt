//package com.example.calculator.Parser
//
//import com.example.calculator.miscellaneous.TokenTypes
//import com.example.calculator.model.*
//
///**
// * Helper class that parses string into token
// */
//object InputParser {
//    /**
//     * Parses token [String] into [Token].
//     *
//     * @param token [String] representation of a token to be parsed.
//     * @return [Token] representation of the [String] passed.
//     */
//    fun parseToken(input: String) : Token? {
//        return when {
//            isOperator(input) -> Token(input, TokenTypes.Operator)
//            isNumber(input) -> Token(input, TokenTypes.Number)
//            isFunction(input) -> Token(input, TokenTypes.Function)
//            else -> null
//        }
//    }
//
//    /**
//     * Helper function.
//     * Finds if token is a function.
//     *
//     * @param token the token in question.
//     * @return [Boolean] indicating the result of the query.
//     */
//    private fun isFunction(token: String): Boolean = isNumber(token) == isOperator(token)
//
//    /**
//     * Helper function.
//     * Finds if token is a number.
//     *
//     * @param token the token in question.
//     * @return [Boolean] indicating the result of the query.
//     */
//    private fun isNumber(token: String): Boolean = token[0].isDigit()
//
//    /**
//     * Helper function.
//     * Finds if token is an operator.
//     *
//     * @param token the token in question.
//     * @return [Boolean] indicating the result of the query.
//     */
//    private fun isOperator(token: String): Boolean = operatorMap.contains(token)
//}