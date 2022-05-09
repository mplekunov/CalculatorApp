package com.example.calculator.model

import com.example.calculator.miscellaneous.Functions
import com.example.calculator.miscellaneous.Numbers
import com.example.calculator.miscellaneous.TokenTypes
import com.example.calculator.parser.FunctionParser

import com.example.calculator.parser.NumberParser
import com.example.calculator.parser.OperatorParser

/**
 * [Expression] data structure which contains expression in the infix format.
 *
 * Provides interface for performing different manipulations on the [Expression] data structure.
 */
class Expression {
    private val _tokenLengthLimit = 18
    private var _expression = mutableListOf<Token>()

    private val numberParser = NumberParser()
    private val operatorParser = OperatorParser()
    private val functionParser = FunctionParser()


    val expression: List<Token>
        get() = _expression

    @Throws(NullPointerException::class)
    fun addNumber(number: Number, index: Int) : Boolean {
        val token = numberParser.parse(number)

        if (_expression.isEmpty() && number.type != Numbers.DOT) {
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

        if (number.type == Numbers.DOT && tokenToEdit.type != TokenTypes.Number)
            return false

        // If last token is a number, we add new "token" to the previous number
        // Otherwise, we create new number
        if (tokenToEdit.type == TokenTypes.Number) {
            val numberToken = numberParser.parse(tokenToEdit)

            // Dot can only be part of number
            if (number.valueAsTokens.contains(Numbers.DOT))
                return parseDot(index)

            // Numbers can't have leading zeroes, unless we are dealing with floats
            if (numberToken.valueAsTokens.size == 1 && numberToken.valueAsTokens.last() == Numbers.ZERO)
                _expression[index] = token

            // There should be a limit to the number length
            else if (numberToken.valueAsTokens.size < _tokenLengthLimit) {
                numberToken.valueAsTokens.addAll(number.valueAsTokens)
                numberToken.type = number.type

                _expression[index] = numberParser.parse(numberToken)
            }
        } else if (tokenToEdit.type == TokenTypes.Operator || (tokenToEdit.type == TokenTypes.Function && FunctionParser().parse(tokenToEdit).type != Functions.PERCENTAGE))
            _expression.add(token)

        return true
    }

    fun addOperator(operator: Operator, index: Int) : Boolean {
        val token = operatorParser.parse(operator)

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

    fun addFunction(function: Function, index: Int) : Boolean {
        val token = functionParser.parse(function)

        if (function.type == Functions.PERCENTAGE && _expression.isEmpty())
            return false

        // All functions have the same format fun ( expr )
        // The only exception is Percentage
        if (function.type == Functions.PERCENTAGE && _expression.last().type == TokenTypes.Number) {
            when {
                index <= _expression.lastIndex ->_expression.add(index, token)
                else -> _expression.add(token)
            }
        }

        // Logic for fun ( expr ) format

        return true
    }

    private fun parseDot(index: Int): Boolean {
        val curIndex = if (index < _expression.lastIndex)
            index
        else
            _expression.lastIndex

        val numberToken = numberParser.parse(_expression[curIndex])

        if (numberToken.type == Numbers.INTEGER) {
            numberToken.valueAsTokens.add(Numbers.DOT)
            _expression[curIndex] = numberParser.parse(numberToken)
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

        var tokenToEdit = _expression[index]

        when (tokenToEdit.type) {
            TokenTypes.Number -> {
                val number = numberParser.parse(tokenToEdit)

                if (number.valueAsTokens.isNotEmpty()) {
                    number.valueAsTokens.removeLast()

                    tokenToEdit = numberParser.parse(number)
//                    _expression[index] = numberParser.parse(number)
                }
            }
            TokenTypes.Operator -> {
                //slow...
                _expression.removeAt(index)
            }
            TokenTypes.Function -> { TODO("Not yet implemented") }
        }

        if (tokenToEdit.value.isEmpty()) {
            when {
                isRemovable -> _expression.removeAt(index)
                !isRemovable && tokenToEdit.type == TokenTypes.Number -> _expression[index] = numberParser.parse(Number(Numbers.ZERO))
            }
        }
        else
            _expression[index] = tokenToEdit

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

        if (tokenToEdit.type == TokenTypes.Number) {
            _expression[index] = object : Token {
                override val value: String = ""
                override val type: TokenTypes = _expression[index].type
            }
        }

        return true
    }

//    /**
//     * Sets token at destination *index* to the new *token*.
//     *
//     * @param token the new token.
//     * @param index the position of the old token in the expression data structure.
//     * @return result indicating success of operation.
//     */
//    fun setTokenAt(token: Token, index: Int): Boolean {
//        if (index < 0 || index > _expression.lastIndex)
//            return false
//
//        val oldToken = _expression[index]
//
//        if (token.type != oldToken.type)
//            return false
//
//        _expression[index] = token
//        return true
//    }
}