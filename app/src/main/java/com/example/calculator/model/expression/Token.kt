package com.example.calculator.model.expression

import com.example.calculator.miscellaneous.TokenTypes

interface Token {
    val value: String
    val type: TokenTypes
}