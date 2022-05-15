package com.example.calculator.model.number

import com.example.calculator.model.token.Token
import com.example.calculator.model.token.TokenTypes

class Number(value: String) : Token(value, TokenTypes.Number) {
}
