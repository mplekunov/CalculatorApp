package com.example.calculator

import com.example.calculator.model.expression.Expression
import com.example.calculator.model.token.Token
import com.example.calculator.model.token.TokenTypes
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class ExpressionTest {
    companion object {
        private lateinit var expression: Expression

        @BeforeAll
        @JvmStatic
        fun setUp() {
            expression = Expression()
        }
    }

    private val operations = listOf(
        Token(")", TokenTypes.Operator) to false,
        Token("(", TokenTypes.Operator) to true,
        Token("5", TokenTypes.Number) to true,
        Token("+", TokenTypes.Operator) to true,
        // +( is not allowed
        Token(")", TokenTypes.Operator) to false,
        Token("(", TokenTypes.Operator) to true,
        // ( 5 + (
        Token("(", TokenTypes.Operator) to true,
        // ( 5 + ((
        Token("+", TokenTypes.Operator) to false,
        Token("10", TokenTypes.Number) to true,
        Token(")", TokenTypes.Operator) to true,
        Token("+", TokenTypes.Operator) to true,
        // ( 5 + ( ( 10 ) +
        Token("5", TokenTypes.Number) to true,
        Token(")", TokenTypes.Operator) to true,
        Token(")", TokenTypes.Operator) to true,
        // ( 5 + ( ( 10 ) + 5 ) )
        Token(")", TokenTypes.Operator) to false,
        // )( is not allowed
        Token("(", TokenTypes.Operator) to false
        )

    @TestFactory
    fun `test adding parenthesis operator to expression`() = operations
        .map { (token, expected) ->
            DynamicTest.dynamicTest("Did it add $token? - $expected") {
                assertEquals(expected, expression.add(token))
                print(expression.expression)
            }
        }
}