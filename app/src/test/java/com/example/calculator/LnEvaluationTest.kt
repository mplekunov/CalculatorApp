package com.example.calculator

import com.example.calculator.model.expression.ExpressionEvaluator
import com.example.calculator.model.postfix.PostfixEvaluator
import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.model.token.Token
import com.example.calculator.model.token.TokenTypes
import com.example.calculator.parser.FunctionParser
import com.example.calculator.parser.OperatorParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.math.RoundingMode

class LnEvaluationTest {
    private val expressions = listOf(
        mutableListOf(
            FunctionParser.parse(FunctionKind.NATURAL_LOG),
            OperatorParser.parse(OperatorKind.LEFT_BRACKET),
            Token("10", TokenTypes.Number)
        ) to 2.30258509,

        mutableListOf(
            FunctionParser.parse(FunctionKind.NATURAL_LOG),
            OperatorParser.parse(OperatorKind.LEFT_BRACKET),
            Token("10", TokenTypes.Number),
            OperatorParser.parse(OperatorKind.ADDITION),
            Token("10", TokenTypes.Number),
        ) to 2.99573227,

        mutableListOf(
            FunctionParser.parse(FunctionKind.NATURAL_LOG),
            OperatorParser.parse(OperatorKind.LEFT_BRACKET),
            Token("10", TokenTypes.Number),
            OperatorParser.parse(OperatorKind.ADDITION),
            Token("10", TokenTypes.Number),
            OperatorParser.parse(OperatorKind.ADDITION),
            OperatorParser.parse(OperatorKind.LEFT_BRACKET),
            Token("10", TokenTypes.Number),

            ) to 3.40119738,

        mutableListOf(
            FunctionParser.parse(FunctionKind.NATURAL_LOG),
            OperatorParser.parse(OperatorKind.LEFT_BRACKET),
            Token("10", TokenTypes.Number),
            OperatorParser.parse(OperatorKind.ADDITION),
            Token("10", TokenTypes.Number),
            OperatorParser.parse(OperatorKind.MULTIPLICATION),
            OperatorParser.parse(OperatorKind.LEFT_BRACKET),
            Token("10", TokenTypes.Number),

            ) to 4.70048037,

        mutableListOf(
            FunctionParser.parse(FunctionKind.NATURAL_LOG),
            OperatorParser.parse(OperatorKind.LEFT_BRACKET),
            Token("10", TokenTypes.Number),
            OperatorParser.parse(OperatorKind.ADDITION),
            Token("10", TokenTypes.Number),
            OperatorParser.parse(OperatorKind.MULTIPLICATION),
            OperatorParser.parse(OperatorKind.LEFT_BRACKET),
            Token("10", TokenTypes.Number),
            OperatorParser.parse(OperatorKind.MULTIPLICATION),
            FunctionParser.parse(FunctionKind.NATURAL_LOG),
            OperatorParser.parse(OperatorKind.LEFT_BRACKET),
            Token("10", TokenTypes.Number),

            ) to 5.48171547,

        // ln ( 10 + 10 * ( 10 * ln ( 10 )
        mutableListOf(
            FunctionParser.parse(FunctionKind.NATURAL_LOG),
            OperatorParser.parse(OperatorKind.LEFT_BRACKET),
            Token("10", TokenTypes.Number),
            OperatorParser.parse(OperatorKind.ADDITION),
            Token("10", TokenTypes.Number),
            OperatorParser.parse(OperatorKind.MULTIPLICATION),
            OperatorParser.parse(OperatorKind.LEFT_BRACKET),
            Token("10", TokenTypes.Number),
            OperatorParser.parse(OperatorKind.MULTIPLICATION),
            FunctionParser.parse(FunctionKind.NATURAL_LOG),
            OperatorParser.parse(OperatorKind.LEFT_BRACKET),
            Token("10", TokenTypes.Number),

            ) to 5.48171547,

        // ln ( 10 + 10 * ( 10 * ln ( 10 ) + ln (- 10
//        mutableListOf(
//            FunctionParser.parse(FunctionKind.NATURAL_LOG),
//            OperatorParser.parse(OperatorKind.LEFT_BRACKET),
//            Token("10", TokenTypes.Number),
//            OperatorParser.parse(OperatorKind.ADDITION),
//            Token("10", TokenTypes.Number),
//            OperatorParser.parse(OperatorKind.MULTIPLICATION),
//            OperatorParser.parse(OperatorKind.LEFT_BRACKET),
//            Token("10", TokenTypes.Number),
//            OperatorParser.parse(OperatorKind.MULTIPLICATION),
//            FunctionParser.parse(FunctionKind.NATURAL_LOG),
//            OperatorParser.parse(OperatorKind.LEFT_BRACKET),
//            Token("10", TokenTypes.Number),
//            OperatorParser.parse(OperatorKind.RIGHT_BRACKET),
//            OperatorParser.parse(OperatorKind.ADDITION),
//            FunctionParser.parse(FunctionKind.NATURAL_LOG),
//            OperatorParser.parse(OperatorKind.LEFT_BRACKET),
//            OperatorParser.parse(OperatorKind.SUBTRACTION),
//            Token("10", TokenTypes.Number)
//
//            ) to "NaN",
        // ln ( 10 + 10 * ( 10 * ln ( 10 ) + ln ( ln ( 10

        mutableListOf(
            FunctionParser.parse(FunctionKind.NATURAL_LOG),
            OperatorParser.parse(OperatorKind.LEFT_BRACKET),
            Token("10", TokenTypes.Number),
            OperatorParser.parse(OperatorKind.ADDITION),
            Token("10", TokenTypes.Number),
            OperatorParser.parse(OperatorKind.MULTIPLICATION),
            OperatorParser.parse(OperatorKind.LEFT_BRACKET),
            Token("10", TokenTypes.Number),
            OperatorParser.parse(OperatorKind.MULTIPLICATION),
            FunctionParser.parse(FunctionKind.NATURAL_LOG),
            OperatorParser.parse(OperatorKind.LEFT_BRACKET),
            Token("10", TokenTypes.Number),
            OperatorParser.parse(OperatorKind.RIGHT_BRACKET),
            OperatorParser.parse(OperatorKind.ADDITION),
            FunctionParser.parse(FunctionKind.NATURAL_LOG),
            OperatorParser.parse(OperatorKind.LEFT_BRACKET),
            FunctionParser.parse(FunctionKind.NATURAL_LOG),
            OperatorParser.parse(OperatorKind.LEFT_BRACKET),
            Token("10", TokenTypes.Number)

        ) to 5.51584049,
    )

    @TestFactory
    fun `test evaluation of natural logarithm`() = expressions
        .map { (expr, expected) ->
            DynamicTest.dynamicTest("$expr to $expected") {
                assertEquals(expected, ExpressionEvaluator(PostfixEvaluator(expr)).result.toString().toBigDecimal().setScale(8, RoundingMode.HALF_UP).toDouble())
            }
        }
}