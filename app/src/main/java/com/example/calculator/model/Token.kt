package com.example.calculator.model

import com.example.calculator.miscellaneous.TokenTypes

interface Token {
    val value: String
    val type: TokenTypes
}