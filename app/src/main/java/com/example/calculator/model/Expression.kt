package com.example.calculator.model

import com.example.calculator.miscellaneous.Functions
import com.example.calculator.miscellaneous.Numbers
import com.example.calculator.miscellaneous.Operators
import com.example.calculator.miscellaneous.TokenTypes

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

    @Throws(NullPointerException::class)
    fun addNumber(number: Numbers, index: Int) : Boolean {
        val token =
            object : Token {
                override var value = (Number.parseNumber(number)?.value ?: 0) as String
                override val type = TokenTypes.Number
        }

        if (_expression.isEmpty() && number != Numbers.DOT) {
            _expression.add(token)
            return true
        }

        // In case of numbers, each number has it's own token
        // Since we can only input expression in the format: Number Operator Function/Number
        // We are sure that when we encounter number, it's going to be either "new" number
        // or it's going to be addition to the previous number
        val index = if (index > _expression.lastIndex) _expression.lastIndex else index

        if (index < 0)
            return true

        val tokenToEdit = _expression[index]

        // If last token is a number, we add new "token" to the previous number
        // Otherwise, we create new number
        if (tokenToEdit.type == TokenTypes.Number) {
            val numberToken = Number.parseToken(tokenToEdit) ?: throw NullPointerException("Empty Number Token")

            // Dot can only be part of number
            if (number == Numbers.DOT)
                return parseDot(index)

            // Numbers can't have leading zeroes, unless we are dealing with floats
            if (numberToken.valueAsTokens.size == 1 && numberToken.valueAsTokens.last() == Numbers.ZERO)
                _expression[index] = token

            // There should be a limit to the number length
            else if (numberToken.valueAsTokens.size < _tokenLengthLimit)
                _expression[index].value += Number.parseNumber(number)?.value ?: 0

        } else if ((tokenToEdit.type == TokenTypes.Operator ||
                    (tokenToEdit.type == TokenTypes.Function && Function.parseToken(tokenToEdit)?.subType != Functions.PERCENTAGE)) && number != Numbers.DOT)
            _expression.add(token)

        return true
    }

    fun addOperator(operator: Operators, index: Int) : Boolean {
        val token =
            object : Token {
                override var value = Operator.parseOperator(operator)?.value ?: ""
                override val type = TokenTypes.Operator
        }

        // Expression can't start with an operator
        if (_expression.isEmpty())
            return false

        // In case of Operators, each operator has its own token
        // No two operators can be appended to each other
        val lastToken = _expression.last()

        // Operators can't follow one another
        // In expression there is always a number between operators (or function)
        // However, user may want to replace previous operator by pressing new operator
        if (lastToken.type == TokenTypes.Operator) {
            _expression[_expression.lastIndex] = token
            return true
        }

        // We are using operators on either Numbers or Functions
        if (lastToken.type == TokenTypes.Number || lastToken.type == TokenTypes.Function) {
            when {
                index <= _expression.lastIndex -> _expression.add(index, token)
                else -> _expression.add(token)
            }

            return true
        }

        return false
    }

    fun addFunction(function: Functions, index: Int) : Boolean {
        val token =
            object : Token {
                override var value = Function.parseFunction(function)?.value ?: ""
                override val type = TokenTypes.Function
            }

        if (function == Functions.PERCENTAGE && _expression.isEmpty())
            return false

        // All functions have the same format fun ( expr )
        // The only exception is Percentage
        if (function == Functions.PERCENTAGE && _expression.last().type == TokenTypes.Number) {
            when {
                index <= _expression.lastIndex ->_expression.add(index, token)
                else -> _expression.add(token)
            }
        }

        // Logic for fun ( expr ) format

        return true
    }

    @Throws(NullPointerException::class)
    private fun parseDot(index: Int): Boolean {
        val curIndex = if (index < _expression.lastIndex)
            index
        else
            _expression.lastIndex

        val tokenToEdit = _expression[curIndex]
        val numberToken = Number.parseToken(tokenToEdit) ?: throw NullPointerException("Empty Number Token")

        if (numberToken.subType == Numbers.INTEGER) {
            _expression[curIndex].value += "."
            return true
        }

        return false
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

        val tokenToEdit = _expression[index]

        when (tokenToEdit.type) {
            TokenTypes.Number -> tokenToEdit.value = tokenToEdit.value.substring(0, tokenToEdit.value.lastIndex)
            TokenTypes.Operator -> tokenToEdit.value = ""
            TokenTypes.Function -> {}
        }

        if (tokenToEdit.value.isEmpty()) {
            when {
                isRemovable -> _expression.removeAt(index)
                !isRemovable && tokenToEdit.type == TokenTypes.Number -> _expression[index].value = "0"
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
        val tokenToEdit = _expression[index]

        if (tokenToEdit.type == TokenTypes.Number)
            _expression[index].value = "0"

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

        if (token.type != oldToken.type)
            return false

        _expression[index] = token
        return true
    }
}