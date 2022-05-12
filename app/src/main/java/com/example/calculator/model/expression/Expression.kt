package com.example.calculator.model.expression

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

    /**
     * Adds specified object to [Expression] at [index]
     *
     * @param  [number] [Number] object that stores representation of a number
     * @param [index] position of [number] in [Expression]
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun addNumber(number: Number, index: Int) : Boolean {
        val token = numberParser.parse(number)

        if (_expression.isEmpty() && number.type != Numbers.Kind.DOT) {
            _expression.add(token)
            return true
        }

        // In case of numbers, each number has it's own token
        // Since we can only input expression in the format: Number Operator Function/Number
        // We are sure that when we encounter number, it's going to be either "new" number
        // or it's going to be addition to the previous number
        @Suppress("NAME_SHADOWING")
        val index = if (index > _expression.lastIndex) _expression.lastIndex else index

        if (index < 0 || (number.type == Numbers.Kind.DOT && _expression[index].type != TokenTypes.Number))
            return false

        val tokenToEdit = _expression[index]

        // If last token is a number, we add new "token" to the previous number
        // Otherwise, we create new number
        if (tokenToEdit.type == TokenTypes.Number) {
            val numberToken = numberParser.parse(tokenToEdit)

            // Dot can only be part of number
            if (number.valueAsTokens.contains(Numbers.Kind.DOT))
                return parseDot(index)

            // Numbers can't have leading zeroes, unless we are dealing with floats
            if (numberToken.valueAsTokens.size == 1 && numberToken.valueAsTokens.last() == Numbers.Kind.ZERO) {
                _expression[index] = token
                return true
            }

            // There should be a limit to the number length
            else if (numberToken.valueAsTokens.size < _tokenLengthLimit) {
                numberToken.valueAsTokens.addAll(number.valueAsTokens)
                numberToken.type = number.type

                _expression[index] = numberParser.parse(numberToken)
                return true
            }
        } else if (tokenToEdit.type == TokenTypes.Operator || (tokenToEdit.type == TokenTypes.Function && functionParser.parse(tokenToEdit).type != Functions.Kind.PERCENTAGE))
            return _expression.add(token)

        return false
    }

    /**
     * Adds specified object to [Expression] at [index]
     *
     * @param  [operator] [Operator] object that stores representation of an operator
     * @param [index] position of [operator] in [Expression]
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
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

    /**
     * Adds specified object to [Expression] at [index]
     *
     * @param  [function] [Function] object that stores representation of a function or function expression
     * @param [index] position of [function] in [Expression]
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun addFunction(function: Function, index: Int) : Boolean {
        val token = functionParser.parse(function)

        if (function.type == Functions.Kind.PERCENTAGE && _expression.isEmpty())
            return false

        // All functions have the same format fun ( expr )
        // The only exception is Percentage
        if (function.type == Functions.Kind.PERCENTAGE && _expression.last().type == TokenTypes.Number) {
            when {
                index <= _expression.lastIndex ->_expression.add(index, token)
                else -> _expression.add(token)
            }

            return true
        }

        // Logic for fun ( expr ) format

        return false
    }

    private fun parseDot(index: Int): Boolean {
        val curIndex = if (index < _expression.lastIndex)
            index
        else
            _expression.lastIndex

        val numberToken = numberParser.parse(_expression[curIndex])

        if (numberToken.type == Numbers.Kind.INTEGER) {
            numberToken.valueAsTokens.add(Numbers.Kind.DOT)
            _expression[curIndex] = numberParser.parse(numberToken)

            return true
        }

        return false
    }

    /**
     * Deletes last [Numbers], [Functions], or [Operators] of the last object in [Expression].
     *
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun delete(index: Int = _expression.size, isRemovable: Boolean = true) : Boolean {
        return if (index == _expression.size)
            deleteAt(_expression.lastIndex, true)
        else
            deleteAt(index, isRemovable)
    }

    /**
     * Deletes last [Numbers], [Functions], or [Operators] of the object in [Expression] at specified [index].
     *
     * @param [index] position of editable object.
     * @param [isRemovable] flag indicating if token should be removed when it is empty or left with default value 0.
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun deleteAt(index: Int, isRemovable: Boolean) : Boolean {
        if (index < 0 || index > _expression.lastIndex)
            return false

        var tokenToEdit = _expression[index]

        when (tokenToEdit.type) {
            TokenTypes.Number -> {
                val number = numberParser.parse(tokenToEdit)

                if (number.valueAsTokens.isNotEmpty()) {
                    // Exponent
                    if (isExponent(index)) {
                        @Suppress("NAME_SHADOWING")
                        var index = -1

                        while (index < number.valueAsTokens.size)
                            if (number.valueAsTokens[index + 1] == Numbers.Kind.EXPONENT)
                                break
                            else
                                index++

                        number.valueAsTokens = number.valueAsTokens.slice(0..index) as MutableList<Numbers.Kind>
                    }
                    else
                        number.valueAsTokens.removeLast()

                    tokenToEdit = numberParser.parse(number)
                }
            }
            TokenTypes.Operator -> {
                _expression.removeAt(index)
            }
            TokenTypes.Function -> { TODO("Not yet implemented") }
        }

        if (tokenToEdit.value.isEmpty()) {
            when {
                isRemovable -> _expression.removeAt(index)
                !isRemovable && tokenToEdit.type == TokenTypes.Number -> _expression[index] = numberParser.parse(
                    Number(Numbers.Kind.ZERO)
                )
            }
        }
        else
            _expression[index] = tokenToEdit

        return true
    }

    private fun isExponent(index: Int): Boolean {
        val number = numberParser.parse(_expression[index])

        return number.valueAsTokens.contains(Numbers.Kind.EXPONENT)
    }

    /**
     * Deletes all object currently stored in the [Expression].
     *
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun deleteAll(index: Int = _expression.size) : Boolean {
        if (index == _expression.size)
            _expression = mutableListOf()
        else
            deleteAllAt(index)
        return true
    }

    /**
     * Deletes all [Functions], [Numbers], or [Operators] stored in [Expression]'s object at specified [index]
     *
     * @param [index] position of editable object in [Expression]
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun deleteAllAt(index: Int) : Boolean {
        val tokenToEdit = _expression[index]

        if (tokenToEdit.type == TokenTypes.Number) {
            _expression[index] = numberParser.parse(Number(Numbers.Kind.ZERO))

            return true
        }

        return false
    }

    /**
     * Replaces old object in [Expression] at specified [index] to [operator]
     *
     * @param  [operator] [Operator] object that stores representation of an operator
     * @param [index] position of [operator] in [Expression]
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun setOperator(operator: Operator, index: Int): Boolean {
        if (index < 0 || index > _expression.lastIndex)
            return false

        _expression[index] = operatorParser.parse(operator)

        return true
    }
}