package com.example.calculator.model.postfix

import com.example.calculator.datastructure.BigNumber
import com.example.calculator.model.expression.ExpressionEvaluator
import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.number.NumberKind
import com.example.calculator.model.operator.Associativity
import com.example.calculator.model.operator.Operator
import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.model.token.Token
import com.example.calculator.model.token.TokenTypes
import com.example.calculator.parser.FunctionParser
import com.example.calculator.parser.NumberParser
import com.example.calculator.parser.OperatorParser
import java.util.*

class PostfixEvaluator(var infix: MutableList<Token>) {
    private var _infix: MutableList<Token> = mutableListOf<Token>().apply { addAll(infix) }
    private var _postfix: MutableList<Token> = mutableListOf()
    private var _opStack = Stack<Operator>()

    val postfix get() = _postfix

    init {
        getPostfix()
    }

    /**
     * Fixes parentheses by adding missing parentheses at the end of an expression
     */
    private fun fixParentheses() {
        val leftParenthesis = OperatorParser.parse(OperatorKind.LEFT_BRACKET)
        val rightParenthesis = OperatorParser.parse(OperatorKind.RIGHT_BRACKET)

        var parentheses = 0

        for (i in _infix.indices) {
            if (_infix[i] == leftParenthesis)
                parentheses++
            else if (_infix[i] == rightParenthesis)
                parentheses--
        }

        while (parentheses > 0) {
            _infix.add(rightParenthesis)
            parentheses--
        }
    }

    /**
     * Removes operators that don't have any operands that could use them
     */
    private fun removeUnusedOperators() {
        val rightParenthesis = OperatorParser.parse(OperatorKind.RIGHT_BRACKET)
        val leftParenthesis = OperatorParser.parse(OperatorKind.LEFT_BRACKET)

        while (_infix.size > 0 && _infix.last().type == TokenTypes.Operator && _infix.last() != rightParenthesis && _infix.last() != leftParenthesis)
            _infix.removeLast()
    }

    /**
     * Converts infix into Postfix.
     */
    private fun getPostfix() {
        if (_infix.isEmpty()) {
            _postfix.add(NumberParser.parse(NumberKind.ZERO))
            return
        }

        removeUnusedOperators()

        fixParentheses()

        var i = 0
        while (i < _infix.size) {
            i = when (_infix[i].type) {
                TokenTypes.Number -> processNumber(i)
                TokenTypes.Operator -> processOperator(i)
                TokenTypes.Function -> processFunction(i)
            }
        }

        while (_opStack.isNotEmpty())
            _postfix.add(OperatorParser.parse(_opStack.pop()) as Operator)
    }

    /**
     * Processes number in [_infix] at specified position.
     * @param [index] position of an element in [_infix]
     * @return [index] of the next element in [_infix]
     */
    private fun processNumber(index: Int): Int {
        val token = _infix[index]

        _postfix.add(token)
        return index + 1
    }

    private fun isParenthesis(token: Token): Boolean {
        val leftParenthesis = OperatorParser.parse(OperatorKind.LEFT_BRACKET)
        val rightParenthesis = OperatorParser.parse(OperatorKind.RIGHT_BRACKET)

        return token == leftParenthesis || token == rightParenthesis
    }

    /**
     * Process operator in [_infix] at specified position.
     * @param [index] position of an element in [_infix]
     * @return [index] of the next element in [_infix]
     */
    private fun processOperator(index: Int): Int {
        val token = _infix[index]

        val operatorKind = OperatorParser.parse<OperatorKind>(token)

        val leftParenthesis = OperatorParser.parse(OperatorKind.LEFT_BRACKET)

        // Adds 0 if - or + sign is found in the beginning of a "new" expression
        // "new" expression is an expression defined by two rules:
        // 1. Beginning of the complete expression (e.g. "- 2", "+ 2")
        // 2. Beginning of an expression defined by parentheses (e.g. "(- 2)", "(+ 2)")
        if ((index == 0 || _infix[index - 1] == leftParenthesis) && index + 1 < _infix.size && !isParenthesis(
                token
            )
        ) {
            if (operatorKind == OperatorKind.SUBTRACTION || operatorKind == OperatorKind.ADDITION)
                _postfix.add(NumberParser.parse(NumberKind.ZERO))
            else
                return processError()
        }

        when (operatorKind) {
            OperatorKind.LEFT_BRACKET -> _opStack.push(OperatorParser.parse(token))
            OperatorKind.RIGHT_BRACKET -> {
                while (_opStack.isNotEmpty() && _opStack.peek() != OperatorParser.parse(OperatorKind.LEFT_BRACKET))
                    _postfix.add(_opStack.pop() as Operator)

                if (_opStack.isNotEmpty())
                    _opStack.pop()
            }
            else -> {
                while (_opStack.isNotEmpty() && isAssociativeRule(
                        OperatorParser.parse(token),
                        _opStack.peek()
                    )
                ) {
                    _postfix.add(OperatorParser.parse(_opStack.pop()) as Operator)
                }

                _opStack.push(OperatorParser.parse(token))
            }
        }

        return index + 1
    }

    /**
     * Process function in [_infix] at specified position.
     * @param [index] position of an element in [_infix]
     * @return [index] of the next element in [_infix]
     */
    private fun processFunction(index: Int): Int {
        return when (_infix[index]) {
            FunctionParser.parse(FunctionKind.PERCENTAGE) -> processPercent(index)
            FunctionParser.parse(FunctionKind.NATURAL_LOG) -> processLogarithm(index, Math.E)
            FunctionParser.parse(FunctionKind.LOG) -> processLogarithm(index, 10.0)
            FunctionParser.parse(FunctionKind.SQUARE_ROOT) -> processSquareRoot(index)
            FunctionParser.parse(FunctionKind.FACTORIAL) -> processFactorial(index)
            FunctionParser.parse(FunctionKind.SQUARED) -> processSquared(index)
            else -> processError()
        }
    }

    private fun processFactorial(index: Int): Int {
        var factorial = BigNumber.ONE

        var i = _postfix.lastIndex
        while (i >= 0 && _postfix[i].type != TokenTypes.Number)
            i--

        val lastKnownNumber = BigNumber(_postfix[i].toString()).toInt()

        for (j in 2..lastKnownNumber)
            factorial = factorial.times(BigNumber(j))


        infix.removeLast()
        _infix.removeAt(index)

        _infix[index - 1] = Token(factorial.stripTrailingZeros(), TokenTypes.Number)
        infix[infix.lastIndex] = _infix[index - 1]

        _postfix.add(infix.last())

        return index
    }

    private fun processSquared(index: Int): Int {
        infix.removeLast()
        _infix.removeAt(index)

        val prevToken = infix[index - 1]

        val end = index
        var start = getStartOfExpressionBody(end - 1, infix)

        if (prevToken == OperatorParser.parse(OperatorKind.RIGHT_BRACKET)) {
            if (start - 1 >= 0 && infix[start - 1].type == TokenTypes.Function)
                start--
        }
        else if (start - 1 == 0 && infix[start - 1].type == TokenTypes.Operator && infix[start - 1] != OperatorParser.parse(OperatorKind.LEFT_BRACKET))
            start--
        else if (start - 2 >= 0 && infix[start - 2] == OperatorParser.parse(OperatorKind.LEFT_BRACKET))
            start--

        val postfixEvaluator = PostfixEvaluator(infix.subList(start, end))

        val tempPostfix = postfixEvaluator.postfix
        tempPostfix.add(NumberParser.parse(NumberKind.TWO))
        tempPostfix.add(OperatorParser.parse(OperatorKind.POWER))

        val expressionEvaluator = ExpressionEvaluator(tempPostfix)

        var token = Token(
            BigNumber(expressionEvaluator.result.toString()).stripTrailingZeros(),
            TokenTypes.Number
        )

        if (token == NumberParser.parse(NumberKind.INFINITY))
            token = NumberParser.parse(NumberKind.ZERO)

        _postfix.clear()
        _postfix.add(token)

        infix.replaceRange(start, end, mutableListOf(token))

        return index
    }

    /**
     * Process square root in [_infix] at specified position.
     * @param [index] position of an element in [_infix]
     * @return [index] of the next element in [_infix]
     */
    private fun processSquareRoot(index: Int): Int {
        val start = index + 2
        var end = getEndOfExpressionBody(start, infix)

        // When start > lastIndex it means we have an empty body... Therefore we return NaN
        if (start > infix.lastIndex)
            return processError()

        // Calculates postfix of the "body" of the function
        val postfixEvaluator = PostfixEvaluator(infix.subList(start, end))
        val expressionEvaluator = ExpressionEvaluator(postfixEvaluator.postfix)

        // Replaces current body of the function for its infix calculated/transformed by postfixEvaluator
        infix.replaceRange(start, end - 1, postfixEvaluator.infix)

        val result = expressionEvaluator.result

        // Natural logarithm can't evaluate NaN and can't be <= 0
        if (result == NumberParser.parse(NumberKind.NAN) || BigNumber(result.toString()) < BigNumber.ZERO)
            return processError()

        _postfix.add(
            Token(
                BigNumber.sqrt(BigNumber(result.toString())).toString(),
                TokenTypes.Number
            )
        )

        // We need to know the "end" of the function body with respect of _infix
        end = getEndOfExpressionBody(start, _infix)

        return end + 1
    }

    /**
     * Process logarithm function in [_infix] at specified position with specified [base].
     * @param [index] position of an element in [_infix]
     * @return [index] of the next element in [_infix]
     */
    private fun processLogarithm(index: Int, base: Double): Int {
        val start = index + 2
        var end = getEndOfExpressionBody(start, infix)

        // When start > lastIndex it means we have an empty body... Therefore we return NaN
        if (start > infix.lastIndex)
            return processError()

        // Calculates postfix of the "body" of the function
        val postfixEvaluator = PostfixEvaluator(infix.subList(start, end))
        val expression = ExpressionEvaluator(postfixEvaluator.postfix)

        // Replaces current body of the function for its infix calculated/transformed by postfixEvaluator
        infix.replaceRange(start, end - 1, postfixEvaluator.infix)

        val result = expression.result

        // Natural logarithm can't evaluate NaN and can't be <= 0
        if (result == NumberParser.parse(NumberKind.NAN) || BigNumber(result.toString()) <= BigNumber.ZERO)
            return processError()

        _postfix.add(
            Token(
                BigNumber.log(BigNumber(result.toString()), BigNumber(base)).toString(),
                TokenTypes.Number
            )
        )

        // We need to know the "end" of the function body with respect of _infix
        end = getEndOfExpressionBody(start, _infix)

        return end + 1
    }

    /**
     * Process percent function in [_infix] at specified position.
     * @param [index] position of an element in [_infix]
     * @return [index] of the next element in [_infix]
     */
    private fun processPercent(index: Int): Int {
        val first = BigNumber(_postfix.removeLast().toString())
        val second = BigNumber(100.0)

        var percentage = first.div(second)

        // If _postfix is not empty... then there was a percent expression in the format:
        // parent_number +or-or*or/ percent_number %
        if (_postfix.isNotEmpty()) {
            val tempStack = Stack<Operator>()

            while (_opStack.isNotEmpty() && isParenthesis(_opStack.peek()))
                tempStack.push(_opStack.pop())

            val lastKnownOperator = _opStack.peek()

            while (tempStack.isNotEmpty())
                _opStack.push(tempStack.pop())


            // Finds parent_number in the postfix expression that
            var i = _postfix.lastIndex
            while (i >= 0 && _postfix[i].type != TokenTypes.Number)
                i--

            // If there was no parent_number, then we are dealing with a percent expression in the format:
            // -or+ percent_number %
            val last = if (_postfix[i].type == TokenTypes.Number)
                _postfix[i]
            else
                NumberParser.parse(NumberKind.ZERO)

            // Transforms last token into BigDecimal
            var lastKnownNumber = BigNumber(last.toString())

            percentage =
                if (lastKnownOperator == OperatorParser.parse(OperatorKind.SUBTRACTION) ||
                    lastKnownOperator == OperatorParser.parse(OperatorKind.ADDITION)
                ) {

                    if (lastKnownNumber == BigNumber.ZERO) {
                        lastKnownNumber =
                            if (lastKnownOperator == OperatorParser.parse(OperatorKind.SUBTRACTION))
                                lastKnownNumber.minus(first)
                            else
                                lastKnownNumber.plus(first)
                    }

                    lastKnownNumber.times(percentage)
                } else
                    percentage
        }

        // We have to remove percent sign from both, infix and _infix
        infix.removeLast()
        _infix.removeAt(index)

        // We have to make an in-place replacement of the "percent_number %" part for the calculated result
        if (percentage < BigNumber.ZERO)
            _infix[index - 1] =
                Token(percentage.times(BigNumber("-1")).toString(), TokenTypes.Number)
        else
            _infix[index - 1] = Token(percentage.toString(), TokenTypes.Number)

        infix[infix.lastIndex] = _infix[index - 1]

        _postfix.add(infix.last())

        return index
    }

    /**
     * Calculates the end of the function body.
     * @param [start] first index of the expression inside of the function
     * @param [source] collection that represents infix from beginning of the function to infix's end
     * @return [Int] the end of the function body
     */
    private fun getEndOfExpressionBody(start: Int, source: List<Token>): Int {
        val leftParenthesis = OperatorParser.parse(OperatorKind.LEFT_BRACKET)
        val rightParenthesis = OperatorParser.parse(OperatorKind.RIGHT_BRACKET)

        var parenthesis = 1

        var i = start

        while (i < source.size) {
            if (source[i] == leftParenthesis)
                parenthesis++
            else if (source[i] == rightParenthesis)
                parenthesis--

            if (parenthesis == 0)
                break

            i++
        }

        return i
    }

    private fun getStartOfExpressionBody(end: Int, source: List<Token>): Int {
        val leftParenthesis = OperatorParser.parse(OperatorKind.LEFT_BRACKET)
        val rightParenthesis = OperatorParser.parse(OperatorKind.RIGHT_BRACKET)

        var parenthesis = 0

        var i = end
        while (i >= 0) {
            if (source[i] == leftParenthesis)
                parenthesis++
            else if (source[i] == rightParenthesis)
                parenthesis--

            if (parenthesis == 0)
                break

            i--
        }

        return if (i < 0)
            i + 1
        else
            i
    }

    /**
     * Processes error routine.
     * Replaces [_postfix] with [NumberKind.NAN]
     * @return [Int] that denotes ending index of [_infix]
     */
    private fun processError(): Int {
        _postfix.clear()
        _opStack.clear()
        _postfix.add(NumberParser.parse(NumberKind.NAN))
        return _infix.size
    }

    private fun MutableList<Token>.replaceRange(start: Int, end: Int, source: MutableList<Token>) {
        val newInfix = mutableListOf<Token>()

        for (i in 0 until start)
            newInfix.add(this[i])

        for (i in (start + 1) until (start + source.size + 1))
            newInfix.add(source[i - start - 1])

        for (i in end + 1 until this.size)
            if (this[i] != FunctionParser.parse(FunctionKind.PERCENTAGE))
                newInfix.add(this[i])

        this.clear()
        this.addAll(newInfix)
    }

    /**
     * Helper function.
     * Checks for an associative rule between two Operators.
     *
     * @param x of type [Operator] representing information about first operator.
     * @param y of type [Operator] representing information about second operator.
     * @return [Boolean] indicating adherence to associative rule or the lack of.
     */
    private fun isAssociativeRule(x: Operator, y: Operator): Boolean =
        isLeftRule(x, y) || isRightRule(x, y)

    /**
     * Helper function.
     * Checks for the left rule of [Operator].
     *
     * @param x of type [Operator] representing information about first operator.
     * @param y of type [Operator] representing information about second operator.
     */
    private fun isLeftRule(x: Operator, y: Operator): Boolean =
        x.associativity == Associativity.LEFT && x.precedence <= y.precedence

    /**
     * Helper function.
     * Checks for the right rule of [Operator].
     *
     * @param x of type [Operator] representing information about first operator.
     * @param y of type [Operator] representing information about second operator.
     */
    private fun isRightRule(x: Operator, y: Operator): Boolean =
        x.associativity == Associativity.RIGHT && x.precedence < y.precedence
}