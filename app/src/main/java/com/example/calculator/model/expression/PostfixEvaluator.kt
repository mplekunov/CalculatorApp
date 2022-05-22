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

        while (_infix.size > 0 && _infix.last().type == TokenTypes.Operator && _infix.last() != rightParenthesis)
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
            else {
                _postfix = mutableListOf(NumberParser.parse(NumberKind.NAN))
                return _infix.size
            }
        }

        when (kind) {
            OperatorKind.LEFT_BRACKET -> _opStack.push(OperatorParser.parse(token))
            OperatorKind.RIGHT_BRACKET -> {
                while (_opStack.peek() != OperatorParser.parse(OperatorKind.LEFT_BRACKET))
                    _postfix.add(_opStack.pop() as Operator)

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

                var last = _postfix.last()

                if (_postfix.lastIndex - 1 >= 0 && last.type == TokenTypes.Operator)
                    last = _postfix[_postfix.lastIndex - 1]

                val lastKnownNumber = BigDecimal(last.toString()).setScale(10, RoundingMode.HALF_UP)

                percentage =
                    if (lastKnownOperator == OperatorParser.parse(OperatorKind.SUBTRACTION) || lastKnownOperator == OperatorParser.parse(
                            OperatorKind.ADDITION
                        )
                    )
                        lastKnownNumber.multiply(percentage)
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
            var i = index + 2

            var parenthesis = 1

            val leftParenthesis = OperatorParser.parse(OperatorKind.LEFT_BRACKET)
            val rightParenthesis = OperatorParser.parse(OperatorKind.RIGHT_BRACKET)

            while (i < _infix.size) {
                if (parenthesis == 0)
                    break

                if (_infix[i] == leftParenthesis)
                    parenthesis++
                else if (_infix[i] == rightParenthesis)
                    parenthesis--

                i++
            }

            if (_infix.subList(index + 2, i - 1).isEmpty())
                _postfix.add(NumberParser.parse(NumberKind.ZERO))
            else {
                val result = ExpressionEvaluator(PostfixEvaluator(_infix.subList(index + 2, i - 1))).result.toString()

                if (result.toDouble() < 0) {
                    _postfix.clear()
                    _opStack.clear()
                    _postfix.add(NumberParser.parse(NumberKind.NAN))
                    return _infix.size
                }
                _postfix.add(Token(log(result.toDouble(), Math.E).toString(), TokenTypes.Number))
            }

            return i
        }

        return index + 1
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