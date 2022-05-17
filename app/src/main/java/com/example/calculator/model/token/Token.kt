package com.example.calculator.model.token

open class Token(
    private var _value: String,
    val type: TokenTypes
    ) {

    val length get() = _value.length
    val lastIndex get() = _value.lastIndex

    fun last(): Token = Token(_value.last().toString(), type)
    fun first(): Token = Token(_value.first().toString(), type)

    fun isEmpty(): Boolean = _value.isEmpty()
    fun isNotEmpty(): Boolean = _value.isNotEmpty()

    fun slice(indices: IntRange): Token = Token(_value.slice(indices), type)

    operator fun get(int: Int): Token = Token(_value[int].toString(), type)

    override fun equals(other: Any?): Boolean {
        return this._value == (other as Token)._value
    }

    fun contains(other: Token): Boolean {
        return this._value.contains(other._value)
    }

    operator fun plusAssign(other: Token) {
        this._value += other._value
    }

    override fun toString(): String {
        return this._value
    }

    override fun hashCode(): Int {
        var result = _value.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}