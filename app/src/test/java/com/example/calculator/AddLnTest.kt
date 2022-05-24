package com.example.calculator

import com.example.calculator.model.expression.Expression
import com.example.calculator.model.function.FunctionKind
import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.model.token.Token
import com.example.calculator.model.token.TokenTypes
import com.example.calculator.parser.FunctionParser
import com.example.calculator.parser.NumberParser
import com.example.calculator.parser.OperatorParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class AddLnTest {
    companion object {
        private lateinit var expression: Expression

        @BeforeAll
        @JvmStatic
        fun setUp() {
            expression = Expression()
        }
    }

    private val operations = listOf(
        FunctionParser.parse(FunctionKind.NATURAL_LOG) to true,
        OperatorParser.parse(OperatorKind.RIGHT_BRACKET) to false,
        OperatorParser.parse(OperatorKind.MULTIPLICATION) to false,
        OperatorParser.parse(OperatorKind.SUBTRACTION) to true,
        Token("11", TokenTypes.Number) to true,
        FunctionParser.parse(FunctionKind.NATURAL_LOG) to false,
        OperatorParser.parse(OperatorKind.ADDITION) to true,
        FunctionParser.parse(FunctionKind.NATURAL_LOG) to true,
        Token("1", TokenTypes.Number) to true,
        // ln ( - 11 + ln ( 1
        OperatorParser.parse(OperatorKind.MULTIPLICATION) to true,
        FunctionParser.parse(FunctionKind.NATURAL_LOG) to true,
        FunctionParser.parse(FunctionKind.NATURAL_LOG) to true,
        // ln ( - 11 + ln ( 1 * ln ( ln (
        Token("1", TokenTypes.Number) to true,
        OperatorParser.parse(OperatorKind.SUBTRACTION) to true,
        OperatorParser.parse(OperatorKind.LEFT_BRACKET) to true,
        )

    @TestFactory
    fun `test adding ln function to expression`() = operations
        .map { (token, expected) ->
            DynamicTest.dynamicTest("Add $token - $expected") {
                assertEquals(expected, expression.add(token))
                print(expression.expression)
            }
        }
}