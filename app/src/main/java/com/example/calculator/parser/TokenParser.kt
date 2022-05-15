package com.example.calculator.parser

import com.example.calculator.model.token.Token

import com.example.calculator.datastructure.BiMap

interface TokenParser<V : Any> {
    val TokenParser<V>.map: BiMap<String, V>
    fun parse(input: V) : Any
}