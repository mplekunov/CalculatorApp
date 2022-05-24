package com.example.calculator.model.expression

import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.number.Number
import com.example.calculator.model.number.NumberKind
import com.example.calculator.model.operator.Operator
import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.model.token.Token
import com.example.calculator.model.token.TokenTypes
import com.example.calculator.parser.FunctionParser
import com.example.calculator.parser.NumberParser
import com.example.calculator.parser.OperatorParser
import kotlinx.coroutines.*

/**
 * [Expression] data structure which contains expression in the infix format.
 *
 * Provides interface for performing different manipulations on the [Expression] data structure.
 */
class Expression {
    private val _tokenLengthLimit = 18

    private var _expression = mutableListOf<Token>()

    val expression: List<Token> get() = _expression

    /**
     * Adds [token] to [Expression] at [index]
     *
     * @param  [token] token to be added
     * @param [index] position of [token] in [Expression]
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun add(token: Token, index: Int = _expression.size) : Boolean {
        if (token.type == TokenTypes.Operator && OperatorParser.parse(token) as OperatorKind == OperatorKind.SUBTRACTION)
            return processMinusSign(token, index)

        if (token.type == TokenTypes.Operator && isParenthesis(token))
            return processParentheses(token)

        return when(token.type) {
            TokenTypes.Number -> addNumber(token, index)
            TokenTypes.Function -> addFunction(token, index)
            TokenTypes.Operator -> addOperator(token, index)
        }
    }

    /**
     * Checks if [token] is a parenthesis.
     * @return [TRUE] upon successful check, otherwise [FALSE]
     */
    private fun isParenthesis(token: Token): Boolean {
        val leftParenthesis = OperatorParser.parse(OperatorKind.LEFT_BRACKET)
        val rightParenthesis = OperatorParser.parse(OperatorKind.RIGHT_BRACKET)

        return token == leftParenthesis || token == rightParenthesis
    }

    /**
     * Processes parenthesis as the [token]
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    private fun processParentheses(token: Token): Boolean {
        val leftParenthesis = OperatorParser.parse(OperatorKind.LEFT_BRACKET)
        val rightParenthesis = OperatorParser.parse(OperatorKind.RIGHT_BRACKET)

        // If expression is empty... only left parenthesis is allowed
        if (_expression.isEmpty()) {
            return if (token == leftParenthesis)
                _expression.add(token)
            else
                false
        }

        val prevToken = _expression.last()

        // left parenthesis can be used after operators or another left parenthesis
        if (token == leftParenthesis && (prevToken != rightParenthesis && prevToken.type == TokenTypes.Operator))
            return _expression.add(token)

        // right parenthesis can be used only as closing parenthesis or after number
        if (token == rightParenthesis) {
            var parentheses = 0

            for (index in _expression.indices) {
                if (_expression[index] == leftParenthesis)
                    parentheses++

                if (_expression[index] == rightParenthesis)
                    parentheses--
            }

            if (parentheses <= 0)
                return false

            if (prevToken.type == TokenTypes.Number)
                return _expression.add(token)

            if (prevToken == rightParenthesis)
                return _expression.add(token)
        }

        return false
    }

    /**
     * Processes minus sign as the [token] at specified [index]
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    private fun processMinusSign(token: Token, index: Int) : Boolean {
        val leftParenthesis = OperatorParser.parse(OperatorKind.LEFT_BRACKET)

        // Minus sign can be in the beginning of an expression
        if (_expression.isEmpty())
            return _expression.add(OperatorParser.parse(OperatorKind.SUBTRACTION))

        // Minus sign can be after left parenthesis
        if (_expression.last().type == TokenTypes.Operator && _expression.last() == leftParenthesis)
            return _expression.add(OperatorParser.parse(OperatorKind.SUBTRACTION))

        return addOperator(token, index)
    }

    /**
     * Adds number to [Expression] at [index]
     *
     * @param  [token] token to be added
     * @param [index] position of [token] in [Expression]
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    private fun addNumber(token: Token, index: Int) : Boolean {
        if (token == NumberParser.parse(NumberKind.DOT))
            return parseDot(index)

        if (_expression.isEmpty()) {
            _expression.add(token)
            return true
        }

        // In case of numbers, each number has it's own token
        // Since we can only input expression in the format: Number Operator Function/Number
        // We are sure that when we encounter number, it's going to be either "new" number
        // or it's going to be addition to the previous number
        @Suppress("NAME_SHADOWING")
        val index = if (index > _expression.lastIndex) _expression.lastIndex else index


        val tokenToEdit = _expression[index]

        val leftParenthesis = OperatorParser.parse(OperatorKind.LEFT_BRACKET)
        val rightParenthesis = OperatorParser.parse(OperatorKind.RIGHT_BRACKET)

        // If last token is a number, we add new "token" to the previous number
        // Otherwise, we create new number
        if (tokenToEdit.type == TokenTypes.Number) {
            // Numbers can't have leading zeroes, unless we are dealing with floats
            if (tokenToEdit.length == 1 && tokenToEdit.last() == NumberParser.parse(NumberKind.ZERO)) {
                _expression[index] = token
                return true
            }
            else if (NumberParser.parse<Number>(token).isConstant) {
                _expression[index] = token
                return true
            }
            // There should be a limit to the number length
            else if (tokenToEdit.length < _tokenLengthLimit) {
                _expression[index] += token
                return true
            }
            // Dirty code needs to be fixed...
        } else if ((tokenToEdit.type == TokenTypes.Operator && tokenToEdit != rightParenthesis) || (tokenToEdit.type == TokenTypes.Function && tokenToEdit != FunctionParser.parse(FunctionKind.PERCENTAGE)))
            return _expression.add(token)

        return false
    }

    /**
     * Adds operator to [Expression] at [index]
     *
     * @param  [token] token to be added
     * @param [index] position of [token] in [Expression]
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    private fun addOperator(token: Token, index: Int) : Boolean {
        // Expression can't start with an operator
        if (_expression.isEmpty())
            return false

        // In case of Operators, each operator has its own token
        // No two operators can be appended to each other
        val prevToken = _expression.last()

        // Operators can't follow one another
        // In expression there is always a number between operators (or function)
        // However, user may want to replace previous operator by pressing new operator
        val leftParenthesis = OperatorParser.parse(OperatorKind.LEFT_BRACKET)
        val rightParenthesis = OperatorParser.parse(OperatorKind.RIGHT_BRACKET)

        if (prevToken.type == TokenTypes.Operator && prevToken != leftParenthesis && prevToken != rightParenthesis) {
            _expression[_expression.lastIndex] = token
            return true
        }

        if (prevToken != leftParenthesis)
            return _expression.add(token)

        return false
    }

    /**
     * Adds function to [Expression] at [index]
     *
     * @param  [token] token to be added
     * @param [index] position of [token] in [Expression]
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    private fun addFunction(token: Token, index: Int) : Boolean {
        return when(token) {
            FunctionParser.parse(FunctionKind.PERCENTAGE) -> addPercent(token, index)
            FunctionParser.parse(FunctionKind.NATURAL_LOG) -> addRightSidedFunction(token, index)
            FunctionParser.parse(FunctionKind.LOG) -> addRightSidedFunction(token, index)
            FunctionParser.parse(FunctionKind.SQUARE_ROOT) -> addRightSidedFunction(token, index)
            else -> false
        }
    }

    private fun addRightSidedFunction(token: Token, index: Int): Boolean {
        if (_expression.isNotEmpty()) {
            val last = _expression.last()

            val rightParenthesis = OperatorParser.parse(OperatorKind.RIGHT_BRACKET)

            if (last.type == TokenTypes.Number || last == rightParenthesis)
                return false
        }

        _expression.add(token)
        _expression.add(OperatorParser.parse(OperatorKind.LEFT_BRACKET))

        return true
    }

    private fun addPercent(token: Token, index: Int): Boolean {
        if (token == FunctionParser.parse(FunctionKind.PERCENTAGE) && _expression.isEmpty())
            return false

        // All functions have the same format fun ( expr )
        // The only exception is Percentage
        if (token == FunctionParser.parse(FunctionKind.PERCENTAGE) && _expression.last().type == TokenTypes.Number) {
            when {
                index <= _expression.lastIndex ->_expression.add(index, token)
                else -> _expression.add(token)
            }

            return true
        }

        return false
    }

    private fun parseDot(index: Int): Boolean {
        val curIndex =
            if (index < _expression.lastIndex)
                index
            else
                _expression.lastIndex

        if (curIndex < 0)
            return _expression.add(NumberParser.parse(NumberKind.ZERO) + NumberParser.parse(NumberKind.DOT))

        if (_expression[curIndex].type == TokenTypes.Number && !_expression[curIndex].contains(NumberParser.parse(NumberKind.DOT))) {
            _expression[curIndex] += NumberParser.parse(NumberKind.DOT)

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
        val status = if (index == _expression.size)
            deleteAt(_expression.lastIndex, true)
        else
            deleteAt(index, isRemovable)

        return status
    }

    /**
     * Deletes last [Numbers], [Functions], or [Operators] of the object in [Expression] at specified [index].
     *
     * @param [index] position of editable object.
     * @param [isRemovable] flag indicating if token should be removed when it is empty or left with default value 0.
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    private fun deleteAt(index: Int, isRemovable: Boolean) : Boolean {
        if (index < 0 || index > _expression.lastIndex)
            return false

        var tokenToEdit = _expression[index]

        when (tokenToEdit.type) {
            TokenTypes.Number -> {
                if (tokenToEdit.isNotEmpty())
                    tokenToEdit = tokenToEdit.slice(0 until tokenToEdit.lastIndex)
            }
            TokenTypes.Operator -> {
                _expression.removeAt(index)

                val lastIndex = index - 1
                if (lastIndex >= 0 && _expression[lastIndex].type == TokenTypes.Function && tokenToEdit == OperatorParser.parse(OperatorKind.LEFT_BRACKET))
                    _expression.removeLast()

                return true
            }
            TokenTypes.Function -> {
                _expression.removeLast()
            }
        }

        if (tokenToEdit.isEmpty()) {
            when {
                isRemovable -> _expression.removeAt(index)
                !isRemovable && tokenToEdit.type == TokenTypes.Number -> _expression[index] = NumberParser.parse(NumberKind.ZERO)
            }
        }
        else
            _expression[index] = tokenToEdit

        return true
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
    private fun deleteAllAt(index: Int) : Boolean {
        val tokenToEdit = _expression[index]

        if (tokenToEdit.type == TokenTypes.Number) {
            _expression[index] = NumberParser.parse(NumberKind.ZERO)

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
    fun setOperator(token: Token, index: Int): Boolean {
        if (index < 0 || index > _expression.lastIndex)
            return false

        _expression[index] = token

        return true
    }
}