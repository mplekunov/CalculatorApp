package com.example.calculator.model

import android.util.Log
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * [Expression] data structure which contains expression in the infix format.
 *
 * Provides interface for performing different manipulations on the [Expression] data structure.
 */
class Expression {
    private val _tokenLengthLimit = 18
    private var _expression = mutableListOf<Token>()

    val expression: List<Token>
        get() = _expression

    /**
     * Appends [Token] to the last token.
     *
     * @param token the token to be appended.
     * @return result indicating success of operation.
     */
    fun appendToken(token: Token) : Boolean = appendTokenAt(token, _expression.lastIndex + 1)

    private fun appendOperator(token: Token, index: Int) : Boolean {
        // Expression can't start with an operator
        if (_expression.isEmpty())
            return false

        // In case of Operators, each operator has its own token
        // No two operators can be appended to each other
        val lastToken = _expression.last()

        // Operators can't follow one another
        // In expression there is always a number between operators (or function)
        // However, user may want to replace previous operator by pressing new operator
        if (lastToken.kind == Kind.Operator) {
            _expression[_expression.lastIndex] = token
            return true
        }

        // Later on there should be another kind of Tokens -> functions (sin/cos/tan/cot etc.)
        // So I add check in advance
        if (lastToken.kind == Kind.Number) {
            return when (token.value) {
                Operator.PERCENTAGE.operator -> parsePercentage(index)
                Operator.DOT.operator -> parseDot(index)
                else -> {
                    if (index <= _expression.lastIndex) {
                        _expression.add(index, token)
                        true
                    }
                    else
                        _expression.add(token)
                }
            }
        }

        return false
    }

    private fun isFloat(token: Token) : Boolean = token.value.any { it == '.' }

    private fun parseDot(index: Int): Boolean {
        val curIndex = if (index < _expression.lastIndex)
            index
        else
            _expression.lastIndex

        val token = _expression[curIndex]

        if (!isFloat(token)) {
            token.value += Operator.DOT.operator

            _expression[curIndex] = token

            return true
        }

        return false
    }

    private fun parsePercentage(index: Int): Boolean {
        val curIndex = if (index < _expression.lastIndex)
            index
        else
            _expression.lastIndex

        val token = _expression[curIndex]

        var percentage = BigDecimal(token.value).setScale(10, RoundingMode.HALF_UP).div(BigDecimal(100.0).setScale(10, RoundingMode.HALF_UP))

        if (index > 2) {
            val lastKnownOperator = _expression[curIndex - 1]
            val lastKnownNumber = BigDecimal(_expression[curIndex - 2].value).setScale(10, RoundingMode.HALF_UP)

            percentage = when (lastKnownOperator.value) {
                Operator.ADDITION.operator -> percentage.multiply(lastKnownNumber)
                Operator.SUBTRACTION.operator -> (BigDecimal(1).setScale(10, RoundingMode.HALF_UP).minus(percentage)).times(lastKnownNumber)
                else -> percentage
            }
        }

        token.value = percentage.toString()
        _expression[curIndex] = token

        Log.d("Calculator", "$percentage")

        return true
    }

    private fun appendNumber(token: Token, index: Int) : Boolean {
        if (_expression.isEmpty()) {
            _expression.add(token)
            return true
        }

        val index = if (index > _expression.lastIndex) _expression.lastIndex else index
        val editableToken = _expression[index]


        if (editableToken.kind == Kind.Number) {
            if (editableToken.value.length == 1 && editableToken.value.last() == '0')
                _expression[index] = token
            else if (editableToken.value.length < _tokenLengthLimit) {
                editableToken.value += token.value
                _expression[index] = editableToken
            }
        } else if (editableToken.kind == Kind.Operator)
            _expression.add(token)

        return true
    }

    /**
     * Appends [Token] to the token at the specified *index*.
     *
     * @param token the token to be appended.
     * @param index the position of editable [Token].
     * @return result indicating success of operation.
     */
    fun appendTokenAt(token: Token, index: Int) : Boolean {
        return when(token.kind) {
            Kind.Number -> appendNumber(token, index)
            Kind.Operator -> appendOperator(token, index)
            else -> false
        }
    }

    /**
     * Deletes last character of the last [Token]'s value.
     *
     * @return result indicating success of operation.
     */
    fun deleteToken() : Boolean = deleteTokenAt(_expression.lastIndex, true)

    /**
     * Deletes last character of the [Token]'s value at the position specified by *index*.
     *
     * @param index the position of editable [Token].
     * @param isRemovable flag indicating if token should be removed when it is empty or left with default value 0.
     * @return result indicating success of operation.
     */
    fun deleteTokenAt(index: Int, isRemovable: Boolean) : Boolean {
        if (index < 0 || index > _expression.lastIndex)
            return false

        val token = _expression[index]
        token.value = token.value.substring(0, token.value.lastIndex)

        if (token.value.isEmpty()) {
            when {
                isRemovable -> _expression.removeAt(index)
                !isRemovable && token.kind == Kind.Number -> _expression[index].value = "0"
            }
        }
        return true
    }

    /**
     * Deletes all tokens currently stored in the expression.
     *
     * @return result indicating success of operation.
     */
    fun deleteAllTokens() : Boolean {
        _expression = mutableListOf()
        return true
    }

    /**
     * Deletes all tokens currently stored at specified *index* in the expression.
     *
     * @param index the position of editable [Token].
     * @return result indicating success of operation.
     */
    fun deleteAllTokensAt(index: Int) : Boolean {
        val token = _expression[index]

        if (token.kind == Kind.Number)
            _expression[index] = Token(token.kind, "0")

        return true
    }

    /**
     * Sets token at destination *index* to the new *token*.
     *
     * @param token the new token.
     * @param index the position of the old token in the expression data structure.
     * @return result indicating success of operation.
     */
    fun setTokenAt(token: Token, index: Int): Boolean {
        if (index < 0 || index > _expression.lastIndex)
            return false

        val oldToken = _expression[index]

        if (token.kind != oldToken.kind)
            return false

        _expression[index] = token
        return true
    }
}