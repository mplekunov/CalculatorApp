package com.example.calculator.model.expression

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
import java.math.RoundingMode
import java.util.*
import kotlin.math.log

class PostfixEvaluator(var infix: MutableList<Token>) {
    private var _infix: MutableList<Token> = mutableListOf<Token>().apply { addAll(infix) }
    private var _postfix: MutableList<Token> = mutableListOf()
    private var _opStack = Stack<Operator>()

    val postfix get() = _postfix

    init {
        getPostfix()
    }

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

    private fun removeUnusedOperators() {
        val rightParenthesis = OperatorParser.parse(OperatorKind.RIGHT_BRACKET)
        val leftParenthesis = OperatorParser.parse(OperatorKind.LEFT_BRACKET)

        while (_infix.size > 0 && _infix.last().type == TokenTypes.Operator && _infix.last() != rightParenthesis && _infix.last() != leftParenthesis)
            _infix.removeLast()
    }

    /**
     * Converts infix into Postfix.
     * @param [infix] representation of a mathematical expression.
     * @return [_postfix] representation of a mathematical expression.
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

    private fun processNumber(index: Int): Int {
        val token = _infix[index]

        _postfix.add(token)
        return index + 1
    }

    private fun processOperator(index: Int): Int {
        val token = _infix[index]

        val kind = OperatorParser.parse<OperatorKind>(token)

        val leftParenthesis = OperatorParser.parse(OperatorKind.LEFT_BRACKET)
        val rightParenthesis = OperatorParser.parse(OperatorKind.RIGHT_BRACKET)

        if ((index == 0 || _infix[index - 1] == leftParenthesis) && index + 1 < _infix.size && _infix[index] != leftParenthesis && _infix[index] != rightParenthesis) {
            if (kind == OperatorKind.SUBTRACTION || kind == OperatorKind.ADDITION)
                _postfix.add(NumberParser.parse(NumberKind.ZERO))
            else
                return processError()
        }

        when (kind) {
            OperatorKind.LEFT_BRACKET -> _opStack.push(OperatorParser.parse(token))
            OperatorKind.RIGHT_BRACKET -> {
                while (_opStack.isNotEmpty() && _opStack.peek() != OperatorParser.parse(OperatorKind.LEFT_BRACKET))
                    _postfix.add(_opStack.pop() as Operator)

                if (_opStack.isNotEmpty())
                    _opStack.pop()
            }
            else -> {
                while (_opStack.isNotEmpty()
                    && isAssociativeRule(OperatorParser.parse(token), _opStack.peek())
                ) {
                    _postfix.add(OperatorParser.parse(_opStack.pop()) as Operator)
                }

                _opStack.push(OperatorParser.parse(token))
            }
        }

        return index + 1
    }

    private fun processFunction(index: Int): Int {
        val token = _infix[index]

        if (token == FunctionParser.parse(FunctionKind.PERCENTAGE)) {
            val first = BigDecimal(_postfix.removeLast().toString()).setScale(10, RoundingMode.HALF_UP)
            val second = BigDecimal(100.0).setScale(10, RoundingMode.HALF_UP)
            var percentage = first.divide(second)

            if (_postfix.isNotEmpty()) {
                val lastKnownOperator = _opStack.peek()



                var i = _postfix.lastIndex
                while (i >= 0 && _postfix[i].type != TokenTypes.Number)
                    i--

                val last = if (_postfix[i].type == TokenTypes.Number)
                    _postfix[i]
                else
                    NumberParser.parse(NumberKind.ZERO)

                var lastKnownNumber = BigDecimal(last.toString()).setScale(10, RoundingMode.HALF_UP)

                percentage =
                    if (lastKnownOperator == OperatorParser.parse(OperatorKind.SUBTRACTION) ||
                        lastKnownOperator == OperatorParser.parse(OperatorKind.ADDITION)) {

                        if (lastKnownNumber.toDouble() == 0.0) {
                            lastKnownNumber = if (lastKnownOperator == OperatorParser.parse(OperatorKind.SUBTRACTION))
                                lastKnownNumber.minus(first)
                            else
                                lastKnownNumber.plus(first)
                        }

                        lastKnownNumber.multiply(percentage)
                    }
                    else
                        percentage
            }

            percentage = percentage.stripTrailingZeros()

            infix.removeLast()
            _infix.removeLast()

            if (percentage < BigDecimal.ZERO)
                _infix[_infix.lastIndex] =
                    Token(percentage.times(BigDecimal("-1")).toPlainString(), TokenTypes.Number)
            else
                _infix[_infix.lastIndex] = Token(percentage.toPlainString(), TokenTypes.Number)

            infix[infix.lastIndex] = _infix[_infix.lastIndex]

            _postfix.add(Token(percentage.toPlainString(), TokenTypes.Number))
        }
        else if (token == FunctionParser.parse(FunctionKind.NATURAL_LOG)) {
            val start = index + 2
            var end = getEndOfFunctionBody(start, infix)

            if (start > infix.lastIndex)
                return processError()
            else {
                val postfixEvaluator = PostfixEvaluator(infix.subList(start, end))
                val expression = ExpressionEvaluator(postfixEvaluator)

                infix.replaceRange(start, postfixEvaluator.infix)

                val result = expression.result

                if (result == NumberParser.parse(NumberKind.NAN) || result.toString().toDouble() <= 0)
                    return processError()

                _postfix.add(Token(log(result.toString().toDouble(), Math.E).toString(), TokenTypes.Number))

                end = getEndOfFunctionBody(start, _infix)

                return end + 1
            }
        }

        return index + 1
    }

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

    private fun processError(): Int {
        _postfix.clear()
        _opStack.clear()
        _postfix.add(NumberParser.parse(NumberKind.NAN))
        return _infix.size
    }

    private fun MutableList<Token>.replaceRange(start: Int, source: MutableList<Token>) {
        val newInfix = mutableListOf<Token>()

        for (i in 0 until start)
            newInfix.add(this[i])

        for (i in start until start + source.size)
            newInfix.add(source[i - start])

        for (i in start + source.size until this.size) {
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