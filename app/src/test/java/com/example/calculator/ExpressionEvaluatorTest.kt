package com.example.calculator

import com.example.calculator.model.expression.ExpressionEvaluator
import com.example.calculator.model.expression.PostfixEvaluator
import com.example.calculator.model.token.Token
import com.example.calculator.model.token.TokenTypes

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.TestFactory

class ExpressionEvaluatorTest {
    private val expressionsWithParentheses = listOf(
        mutableListOf(
            Token("(", TokenTypes.Operator),
            Token("5", TokenTypes.Number)
        ) to 5,

        mutableListOf(
            Token("(", TokenTypes.Operator),
            Token("5", TokenTypes.Number),
            Token("+", TokenTypes.Operator)
        ) to 5,

        mutableListOf(
            Token("(", TokenTypes.Operator),
            Token("5", TokenTypes.Number),
            Token("+", TokenTypes.Operator),
            Token("6", TokenTypes.Number)
        ) to 11,

        mutableListOf(
            Token("(", TokenTypes.Operator),
            Token("5", TokenTypes.Number),
            Token("+", TokenTypes.Operator),
            Token("6", TokenTypes.Number),
            Token("+", TokenTypes.Operator)
        ) to 11,

        mutableListOf(
            Token("(", TokenTypes.Operator),
            Token("5", TokenTypes.Number),
            Token("+", TokenTypes.Operator),
            Token("6", TokenTypes.Number),
            Token(")", TokenTypes.Operator)
        ) to 11,

        mutableListOf(
            Token("(", TokenTypes.Operator),
            Token("5", TokenTypes.Number),
            Token("+", TokenTypes.Operator),
            Token("6", TokenTypes.Number),
            Token(")", TokenTypes.Operator),

            Token("*", TokenTypes.Operator),

            Token("(", TokenTypes.Operator),
            Token("5", TokenTypes.Number),
            Token("+", TokenTypes.Operator),
            Token("6", TokenTypes.Number),
            Token(")", TokenTypes.Operator)
        ) to 121,

        mutableListOf(
            Token("(", TokenTypes.Operator),
            Token("5", TokenTypes.Number),
            Token("+", TokenTypes.Operator),
            Token("6", TokenTypes.Number),
            Token(")", TokenTypes.Operator),

            Token("*", TokenTypes.Operator),

            Token("(", TokenTypes.Operator),
            Token("5", TokenTypes.Number),
            Token("+", TokenTypes.Operator),
            Token("6", TokenTypes.Number),
            Token(")", TokenTypes.Operator),
            Token("+", TokenTypes.Operator),

        ) to 121,

        //  - ( - 5
        mutableListOf(
        Token("-", TokenTypes.Operator),
        Token("(", TokenTypes.Operator),
        Token("-", TokenTypes.Operator),
        Token("5", TokenTypes.Number),
        ) to 5,

        // - 5 - 5
        mutableListOf(
            Token("-", TokenTypes.Operator),
            Token("5", TokenTypes.Number),
            Token("-", TokenTypes.Operator),
            Token("5", TokenTypes.Number),
        ) to -10,

        // - 5 + ( - 5
        mutableListOf(
            Token("-", TokenTypes.Operator),
            Token("5", TokenTypes.Number),
            Token("+", TokenTypes.Operator),
            Token("(", TokenTypes.Operator),
            Token("-", TokenTypes.Operator),
            Token("5", TokenTypes.Number),
            ) to -10,

        mutableListOf(
            Token("-", TokenTypes.Operator),
            Token("5", TokenTypes.Number),
            Token("+", TokenTypes.Operator),
            Token("(", TokenTypes.Operator),
        ) to -5,

        mutableListOf(
            Token("-", TokenTypes.Operator),
            Token("5", TokenTypes.Number),
            Token("+", TokenTypes.Operator),
            Token("(", TokenTypes.Operator),
            Token("+", TokenTypes.Operator),
            ) to -5,
    )

    @TestFactory
    fun `test evaluation of an expression with parentheses`() = expressionsWithParentheses
        .map { (expression, expected) ->
        DynamicTest.dynamicTest("$expression evaluates to $expected") {
            assertEquals(expected, ExpressionEvaluator(PostfixEvaluator(expression)).result.toString().toInt())
        }
    }


    @Test
    fun `test empty expression`() = assertEquals(0, ExpressionEvaluator(
        PostfixEvaluator(
            mutableListOf())
    ).result.toString().toInt())
}