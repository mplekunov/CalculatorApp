package com.example.calculator.parser

import com.example.calculator.model.expression.Token

import com.example.calculator.datastructure.BiMap

interface TokenParser<T, K : Any, V : Any> {
    val TokenParser<T, K, V>.map: BiMap<K, V>
    fun parse(input: T) : Token
}