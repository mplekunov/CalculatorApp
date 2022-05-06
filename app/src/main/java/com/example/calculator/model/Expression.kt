package com.example.calculator.model

import android.util.Log
import com.example.calculator.miscellaneous.Functions
import com.example.calculator.miscellaneous.Numbers
import com.example.calculator.miscellaneous.Operators
import com.example.calculator.miscellaneous.TokenTypes
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
//    fun appendToken(token: Token) : Boolean = appendTokenAt(token, _expression.lastIndex + 1)

    /**
     * Appends [Token] to the token at the specified *index*.
     *
     * @param token the token to be appended.
     * @param index the position of editable [Token].
     * @return result indicating success of operation.
     */
//    fun appendTokenAt(token: Token, index: Int) : Boolean {
//        return when(token.type) {
//            TokenTypes.Number -> appendNumber((token), index)
//            TokenTypes.Operator -> appendOperator(token, index)
//            TokenTypes.Function -> appendFunction(token, index)
//        }
//    }

    fun appendNumber(number: Numbers, index: Int) : Boolean {
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
        val tokenToEdit = _expression[index]

        // If last token is a number, we add new "token" to the previous number
        // Otherwise, we create new number
        if (tokenToEdit.type == TokenTypes.Number) {
            val numberToken = Number.parseToken(tokenToEdit)

            // Numbers can't have leading zeroes, unless we are dealing with floats
            if (numberToken.valueAsTokens.size == 1 && numberToken.valueAsTokens.last() == Numbers.ZERO)
                _expression[index] = token

//            if (tokenToEdit.value.length == 1 && tokenToEdit.value.last() == '0')
//                _expression[index] =

            // There should be a limit to the number length
            else if (numberToken.valueAsTokens.size < _tokenLengthLimit)
                _expression[index].value += Number.parseNumber(number)?.value ?: 0

//            else if (tokenToEdit.value.length < _tokenLengthLimit) {
//                tokenToEdit.value += numberToken.value
//                _expression[index] = tokenToEdit
//            }
        } else if (tokenToEdit.type == TokenTypes.Operator || tokenToEdit.type == TokenTypes.Function)
            _expression.add(token)

        return true
    }

    private fun appendOperator(operator: Operators, index: Int) : Boolean {
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
                operatorMap[token.value]?.type == Operators.DOT -> parseDot(index)
                index <= _expression.lastIndex -> _expression.add(index, token)
                else -> _expression.add(token)
            }

            return true
        }

        return false
    }

    private fun appendFunction(token: Token, index: Int): Boolean {
        // Expression can't start with an operator
        if (_expression.isEmpty())
            return false

        // In case of Operators, each operator has its own token
        // No two operators can be appended to each other
        val lastToken = _expression.last()

        if (lastToken.type == TokenTypes.Number) {
            return when {
                functionMap[token.value]?.type == Functions.PERCENTAGE -> parsePercentage(index)
                else -> false
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
            token.value += "."

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

            percentage = when (operatorMap[lastKnownOperator.value]?.type) {
                Operators.ADDITION -> percentage.multiply(lastKnownNumber)
                Operators.SUBTRACTION -> (BigDecimal(1).setScale(10, RoundingMode.HALF_UP).minus(percentage)).times(lastKnownNumber)
                else -> percentage
            }
        }

        token.value = percentage.toString()
        _expression[curIndex] = token

        Log.d("Calculator", "$percentage")

        return true
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