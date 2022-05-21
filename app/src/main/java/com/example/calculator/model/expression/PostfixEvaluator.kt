package com.example.calculator.model.expression

import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.number.Number
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

class PostfixEvaluator(val infix: MutableList<Token>) {
    private var _postfix: MutableList<Token> = mutableListOf()
    private var _opStack = Stack<Operator>()

    val postfix: List<Token>
        get() {
            getPostfix()
            return _postfix
        }

    private fun fixParentheses() {
        val leftParenthesis = OperatorParser.parse(OperatorKind.LEFT_BRACKET)
        val rightParenthesis = OperatorParser.parse(OperatorKind.RIGHT_BRACKET)

        var parentheses = 0

        for (i in infix.indices) {
            if (infix[i] == leftParenthesis)
                parentheses++
            else if (infix[i] == rightParenthesis)
                parentheses--
        }

        while (parentheses > 0) {
            infix.add(rightParenthesis)
            parentheses--
        }
    }

    private fun removeUnusedOperators() {
        val rightParenthesis = OperatorParser.parse(OperatorKind.RIGHT_BRACKET)

        while (infix.size > 0 && infix.last().type == TokenTypes.Operator && infix.last() != rightParenthesis)
            infix.removeLast()
    }

    /**
     * Converts infix into Postfix.
     * @param [infix] representation of a mathematical expression.
     * @return [_postfix] representation of a mathematical expression.
     */
    private fun getPostfix() {
        if (infix.isEmpty()) {
            _postfix.add(NumberParser.parse(NumberKind.ZERO))
            return
        }

        removeUnusedOperators()

        fixParentheses()

        var i = 0
        while (i < infix.size) {
            i = when (infix[i].type) {
                TokenTypes.Number -> processNumber(i)
                TokenTypes.Operator -> processOperator(i)
                TokenTypes.Function -> processFunction(i)
            }
        }

        while (_opStack.isNotEmpty())
            _postfix.add(OperatorParser.parse(_opStack.pop()) as Operator)
    }

    private fun processNumber(index: Int): Int {
        val token = infix[index]

        _postfix.add(token)
        return index + 1
    }

    private fun processOperator(index: Int): Int {
        val token = infix[index]

        val kind = OperatorParser.parse<OperatorKind>(token)

        val leftParenthesis = OperatorParser.parse(OperatorKind.LEFT_BRACKET)

        if ((index == 0 || infix[index - 1] == leftParenthesis) && index + 1 < infix.size && (kind == OperatorKind.SUBTRACTION || kind == OperatorKind.ADDITION)) {
            if (kind == OperatorKind.SUBTRACTION || kind == OperatorKind.ADDITION)
                _postfix.add(NumberParser.parse(NumberKind.ZERO))
            else {
                _postfix = mutableListOf(NumberParser.parse(NumberKind.NAN))
                return infix.size
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
        val token = infix[index]

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
            if (percentage < BigDecimal.ZERO)
                infix[infix.lastIndex] =
                    Token(percentage.times(BigDecimal("-1")).toPlainString(), TokenTypes.Number)
            else
                infix[infix.lastIndex] = Token(percentage.toPlainString(), TokenTypes.Number)

            _postfix.add(Token(percentage.toPlainString(), TokenTypes.Number))
        }
        else if (token == FunctionParser.parse(FunctionKind.NATURAL_LOG)) {
            var i = index + 1

            while (i < infix.size) {
                if (infix[i].type == TokenTypes.Function && FunctionParser.parse(FunctionKind.RIGHT_PARENTHESIS) == infix[i])
                    break
                else
                    i++
            }

            if (infix.subList(index + 2, i).isEmpty())
                _postfix.add(NumberParser.parse(NumberKind.ZERO))
            else {
                val result = ExpressionEvaluator(PostfixEvaluator(infix.subList(index + 2, i))).result.toString()
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