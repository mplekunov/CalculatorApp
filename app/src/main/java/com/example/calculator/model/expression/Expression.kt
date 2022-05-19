package com.example.calculator.model.expression

import com.example.calculator.model.function.Function
import com.example.calculator.model.function.FunctionKind
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
    protected val _tokenLengthLimit = 18

    private var _expression = mutableListOf<Token>()
    private var _result = NumberParser.parse(NumberKind.ZERO)

    private var _expressionEvaluator: ExpressionEvaluator? = null

    val result: Token get() {
//        CoroutineScope(Dispatchers.Default).launch { _expressionEvaluator = getResultAsync().await() }
        return _expressionEvaluator?.result ?: _result
//        return ExpressionEvaluator(_expression).result
//        return _result
    }

    val expression: List<Token> get() {
//        GlobalScope.launch { _expressionEvaluator = getResultAsync().await() }
//        _result = _expressionEvaluator?.result ?: _result
        return _expressionEvaluator?.expression ?: _expression

//        return ExpressionEvaluator(_expression).expression
    }


    /**
     * Adds specified object to [Expression] at [index]
     *
     * @param  [number] [Number] object that stores representation of a number
     * @param [index] position of [number] in [Expression]
     * @return [TRUE] upon successful operation, otherwise [FALSE]
     */
    fun add(token: Token, index: Int) : Boolean {
        if (token.type == TokenTypes.Operator && OperatorParser.parse(token) as OperatorKind == OperatorKind.SUBTRACTION)
            return processMinusSign(token, index)

        return when(token.type) {
            TokenTypes.Number -> {
               val result = addNumber(token, index)
                if (result)
                    getResult()
//                    CoroutineScope(Dispatchers.Default).launch { getResultAsync() }

                return result
            }
            TokenTypes.Function -> {
                val result = addFunction(token, index)
                if (result)
                    getResult()
//                    CoroutineScope(Dispatchers.Default).launch { getResultAsync() }

                return result
            }
            TokenTypes.Operator -> addOperator(token, index)
        }
    }

    private fun processMinusSign(token: Token, index: Int) : Boolean {
        if (_expression.isEmpty())
            return _expression.add(OperatorParser.parse(OperatorKind.SUBTRACTION))

        @Suppress("NAME_SHADOWING")
        val index = if (index > _expression.lastIndex) _expression.lastIndex else index

        val prevToken = _expression[index]

        return if (prevToken.type == TokenTypes.Operator) {
            _expression[_expression.lastIndex] = token
            true
        } else
            _expression.add(token)
    }

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

        // If last token is a number, we add new "token" to the previous number
        // Otherwise, we create new number
        if (tokenToEdit.type == TokenTypes.Number) {
            // Numbers can't have leading zeroes, unless we are dealing with floats
            if (tokenToEdit.length == 1 && tokenToEdit.last() == NumberParser.parse(NumberKind.ZERO)) {
                _expression[index] = token
                return true
            }
            // There should be a limit to the number length
            else if (tokenToEdit.length < _tokenLengthLimit) {
                _expression[index] += token

                return true
            }
        } else if (tokenToEdit.type == TokenTypes.Operator || (tokenToEdit.type == TokenTypes.Function && tokenToEdit != FunctionParser.parse(FunctionKind.PERCENTAGE)))
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
    private fun addOperator(token: Token, index: Int) : Boolean {
        // Expression can't start with an operator
        if (_expression.isEmpty()) {
            return false
        }

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
    private fun addFunction(token: Token, index: Int) : Boolean {
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

        // Logic for fun ( expr ) format

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

        getResult()
//        CoroutineScope(Dispatchers.Default).launch { getResultAsync() }

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
                if (tokenToEdit.isNotEmpty()) {
                    // Exponent
                    if (isExponent(index)) {
                        @Suppress("NAME_SHADOWING")
                        var index = -1

                        while (index < tokenToEdit.length)
                            if (tokenToEdit[index + 1] == NumberParser.parse(NumberKind.EXPONENT))
                                break
                            else
                                index++

                        tokenToEdit = tokenToEdit.slice(0..index)
                    }
                    else

                        tokenToEdit = tokenToEdit.slice(0 until tokenToEdit.lastIndex)
                }
            }
            TokenTypes.Operator -> {
                _expression.removeAt(index)
                return true
            }
            TokenTypes.Function -> { TODO("Not yet implemented") }
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

    private fun isExponent(index: Int): Boolean {
        return _expression[index].contains(NumberParser.parse(NumberKind.EXPONENT))
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

        getResult()
//        CoroutineScope(Dispatchers.Default).launch { getResultAsync() }

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

        getResult()
//        CoroutineScope(Dispatchers.Default).launch { getResultAsync() }

        return true
    }

    private fun getResult() {
//        CoroutineScope(Dispatchers.Main).launch {
            _expressionEvaluator = ExpressionEvaluator(_expression)
//        }

//        return withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
////            delay(5000)
//
//            ExpressionEvaluator(_expression)
//        }
    }
}