package com.example.calculator.datastructure

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.log
import kotlin.math.pow
import kotlin.math.sqrt

class BigNumber(private val value: String) : Comparable<BigNumber> {
    companion object {
        val ONE: BigNumber = BigNumber(1)
        val ZERO: BigNumber = BigNumber(0)

        fun abs(x: BigNumber, y: BigNumber): BigNumber {
            return if (x.number - y.number > 0)
                x
            else
                y
        }

        fun sqrt(number: BigNumber): BigNumber = BigNumber(sqrt(number.number))

        fun log(number: BigNumber, base: BigNumber): BigNumber =
            BigNumber(log(number.number, base.number))
    }

    private val number get() = value.toDouble()

    constructor(value: Double) : this(value.toString())
    constructor(value: Int) : this(value.toString())
    constructor(value: BigDecimal) : this(value.toString())
    constructor(value: BigInteger) : this(value.toString())

    fun toInt(): Int {
        return this.number.toInt()
    }

    operator fun plus(other: BigNumber): BigNumber = BigNumber(this.number.plus(other.number))

    operator fun minus(other: BigNumber): BigNumber = BigNumber(this.number.minus(other.number))

    operator fun times(other: BigNumber): BigNumber = BigNumber(this.number.times(other.number))

    operator fun div(other: BigNumber): BigNumber = BigNumber(this.number.div(other.number))

    fun pow(other: BigNumber): BigNumber = BigNumber(this.number.pow(other.number))

    fun stripTrailingZeros(): String {
        var i = this.value.lastIndex

        val sb = StringBuilder(this.value)

        while (i >= 0 && (sb[i] == '0' || sb[i] == '.')) {
            if (sb[i] == '.') {
                sb.deleteAt(i)
                break
            }

            sb.deleteAt(i--)
        }

        return sb.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (other is BigNumber)
            return other.number == this.number

        return false
    }

    override fun hashCode(): Int {
        return number.hashCode()
    }

    override fun compareTo(other: BigNumber): Int {
        val x = this.number
        val y = other.number
        val epsilon = 1E-20

        if (abs(this, other).number < epsilon)
            return 0

        if (x > y)
            return 1

        return -1
    }

    override fun toString(): String {
        return this.number.toString()
    }
}