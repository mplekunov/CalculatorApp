package com.example.calculator

import com.example.calculator.algorithm.Algorithms
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    @DisplayName("Pattern Finder")
    fun find_Pattern_Algo() {
        assertEquals(2, Algorithms.findStartingPosOfPattern("xxawerwqase", "aw"))

        assertEquals(0, Algorithms.findStartingPosOfPattern("1", "1"))
        assertEquals(1, Algorithms.findStartingPosOfPattern("_1", "1"))
        assertEquals(0, Algorithms.findStartingPosOfPattern("1_", "1"))
        assertEquals(1, Algorithms.findStartingPosOfPattern("_1_", "1"))
        assertEquals(6, Algorithms.findStartingPosOfPattern("_--_-_12345_", "12345"))


        assertEquals(3, Algorithms.findStartingPosOfPattern("textestx", "test"))
    }
}