package com.example.calculator.model.postfix

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
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.util.*
import kotlin.math.log
import kotlin.math.sqrt

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
        if ((index == 0 || _infix[index - 1] == leftParenthesis) && index + 1 < _infix.size && !isParenthesis(token)) {
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
//            FunctionParser.parse(FunctionKind.SQUARED) -> processSquared(index)
            else -> processError()
        }
    }

    private fun processFactorial(index: Int): Int {
        var factorial = BigInteger.ONE

        var i = _postfix.lastIndex
        while (i >= 0 && _postfix[i].type != TokenTypes.Number)
            i--

        val lastKnownNumber = BigInteger(_postfix[i].toString()).toInt()

        for (j in 2..lastKnownNumber)
            factorial = factorial.times(j.toBigInteger())


        infix.removeLast()
        _infix.removeAt(index)

        _infix[index - 1] = Token(factorial.toString(), TokenTypes.Number)
        infix[infix.lastIndex] = _infix[index - 1]

        _postfix.add(infix.last())

            return index
    }


    // In Development
    private fun processSquared(index: Int): Int {
        var i = _postfix.lastIndex
        while (i >= 0 && _postfix[i].type != TokenTypes.Number)
            i--

        val lastKnownNumber = BigDecimal(_postfix[i].toString())

        infix.removeLast()
        _infix.removeAt(index)

        val squared = lastKnownNumber.times(lastKnownNumber)

        _infix[index - 1] = Token(squared.toPlainString(), TokenTypes.Number)

        infix[infix.lastIndex] = _infix[index - 1]

        _postfix.add(Token(squared.toPlainString(), TokenTypes.Number))

        return index
    }

    /**
     * Process square root in [_infix] at specified position.
     * @param [index] position of an element in [_infix]
     * @return [index] of the next element in [_infix]
     */
    private fun processSquareRoot(index: Int): Int {
        val start = index + 2
        var end = getEndOfFunctionBody(start, infix)

        // When start > lastIndex it means we have an empty body... Therefore we return NaN
        if (start > infix.lastIndex)
            return processError()

        // Calculates postfix of the "body" of the function
        val postfixEvaluator = PostfixEvaluator(infix.subList(start, end))
        val expression = ExpressionEvaluator(postfixEvaluator)

        // Replaces current body of the function for its infix calculated/transformed by postfixEvaluator
        infix.replaceRange(start, infix.size, postfixEvaluator.infix)

        val result = expression.result

        // Natural logarithm can't evaluate NaN and can't be <= 0
        if (result == NumberParser.parse(NumberKind.NAN) ||  result.toString().toDouble() < 0)
            return processError()

        _postfix.add(Token(sqrt(result.toString().toDouble()).toString(), TokenTypes.Number))

        // We need to know the "end" of the function body with respect of _infix
        end = getEndOfFunctionBody(start, _infix)

        return end + 1
    }

    /**
     * Process logarithm function in [_infix] at specified position with specified [base].
     * @param [index] position of an element in [_infix]
     * @return [index] of the next element in [_infix]
     */
    private fun processLogarithm(index: Int, base: Double): Int {
        val start = index + 2
        var end = getEndOfFunctionBody(start, infix)

        // When start > lastIndex it means we have an empty body... Therefore we return NaN
        if (start > infix.lastIndex)
            return processError()

        // Calculates postfix of the "body" of the function
        val postfixEvaluator = PostfixEvaluator(infix.subList(start, end))
        val expression = ExpressionEvaluator(postfixEvaluator)

        // Replaces current body of the function for its infix calculated/transformed by postfixEvaluator
        infix.replaceRange(start, infix.size, postfixEvaluator.infix)

        val result = expression.result

        // Natural logarithm can't evaluate NaN and can't be <= 0
        if (result == NumberParser.parse(NumberKind.NAN) || result.toString().toDouble() <= 0)
            return processError()

        _postfix.add(Token(log(result.toString().toDouble(), base).toString(), TokenTypes.Number))

        // We need to know the "end" of the function body with respect of _infix
        end = getEndOfFunctionBody(start, _infix)

        return end + 1
    }

    /**
     * Process percent function in [_infix] at specified position.
     * @param [index] position of an element in [_infix]
     * @return [index] of the next element in [_infix]
     */
    private fun processPercent(index: Int): Int {
        val first = BigDecimal(_postfix.removeLast().toString())
        val second = BigDecimal(100.0)

        var percentage = first.divide(second)

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
            var lastKnownNumber = BigDecimal(last.toString())

            percentage =
                if (lastKnownOperator == OperatorParser.parse(OperatorKind.SUBTRACTION) ||
                    lastKnownOperator == OperatorParser.parse(OperatorKind.ADDITION)
                ) {

                    if (lastKnownNumber.toDouble() == 0.0) {
                        lastKnownNumber =
                            if (lastKnownOperator == OperatorParser.parse(OperatorKind.SUBTRACTION))
                                lastKnownNumber.minus(first)
                            else
                                lastKnownNumber.plus(first)
                    }

                    lastKnownNumber.multiply(percentage)
                } else
                    percentage
        }

        percentage = percentage.stripTrailingZeros()

        // We have to remove percent sign from both, infix and _infix
        infix.removeLast()
        _infix.removeAt(index)

        // We have to make an in-place replacement of the "percent_number %" part for the calculated result
        if (percentage < BigDecimal.ZERO)
            _infix[index - 1] =
                Token(percentage.times(BigDecimal("-1")).toPlainString(), TokenTypes.Number)
        else
            _infix[index - 1] = Token(percentage.toPlainString(), TokenTypes.Number)

        infix[infix.lastIndex] = _infix[index - 1]

        _postfix.add(infix.last())

        return index
    }

//    /**
//     * Process natural logarithm function in [_infix] at specified position.
//     * @param [index] position of an element in [_infix]
//     * @return [index] of the next element in [_infix]
//     */
//    private fun processNaturalLogarithm(index: Int): Int {
//        val start = index + 2
//        var end = getEndOfFunctionBody(start, infix)
//
//        // When start > lastIndex it means we have an empty body... Therefore we return NaN
//        if (start > infix.lastIndex)
//            return processError()
//
//        // Calculates postfix of the "body" of the function
//        val postfixEvaluator = PostfixEvaluator(infix.subList(start, end))
//        val expression = ExpressionEvaluator(postfixEvaluator)
//
//        // Replaces current body of the function for its infix calculated/transformed by postfixEvaluator
//        infix.replaceRange(start, infix.size, postfixEvaluator.infix)
//
//        val result = expression.result
//
//        // Natural logarithm can't evaluate NaN and can't be <= 0
//        if (result == NumberParser.parse(NumberKind.NAN) || result.toString().toDouble() <= 0)
//            return processError()
//
//        _postfix.add(Token(log(result.toString().toDouble(), Math.E).toString(), TokenTypes.Number))
//
//        // We need to know the "end" of the function body with respect of _infix
//        end = getEndOfFunctionBody(start, _infix)
//
//        return end + 1
//    }

    /**
     * Calculates the end of the function body.
     * @param [start] first index of the expression inside of the function
     * @param [source] collection that represents infix from beginning of the function to infix's end
     * @return [Int] the end of the function body
     */
    private fun getEndOfFunctionBody(start: Int, source: List<Token>): Int {
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

        for (i in start until start + source.size)
            newInfix.add(source[i - start])

        for (i in start + source.size until end) {
            if (this[i] != FunctionParser.parse(FunctionKind.PERCENTAGE))
                newInfix.add(this[i])
        }

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