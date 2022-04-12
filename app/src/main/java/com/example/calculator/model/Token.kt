package com.example.calculator.model

class Token(val kind: Kind?, var value: String) {

    override fun toString(): String {
        return value
    }
}