package com.example.calculator.parser

import com.example.calculator.model.token.Token

import com.example.calculator.datastructure.BiMap
import com.example.calculator.model.operator.Operator
import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.parser.OperatorParser.map

interface TokenParser<V : Any> {
     val TokenParser<V>.map: BiMap<Token, V>
    fun parse(input: V) : Token
}