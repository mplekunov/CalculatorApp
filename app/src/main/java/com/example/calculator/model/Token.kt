package com.example.calculator.model

import com.example.calculator.miscellaneous.TokenTypes

/**
 *
 */
interface Token {
    var value: String
    val type: TokenTypes
}