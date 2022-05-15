package com.example.calculator.model.token

interface TokenType<K> {
    enum class Kind
    val buttons: Map<K, Kind>
}