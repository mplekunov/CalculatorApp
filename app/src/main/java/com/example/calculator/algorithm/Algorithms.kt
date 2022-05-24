package com.example.calculator.algorithm

object Algorithms {
    fun findStartingPosOfPattern(source: String, pattern: String) : Int {
        if (source.length < pattern.length)
            throw IndexOutOfBoundsException("Source is shorter than pattern")

        var start = 0

        // O(n)
        var k = 0
        for (i in 0..source.length step 1) {
            if (source[i] != pattern[k]) {
                start =
                    if (source[i] != pattern[0])
                        i + 1
                    else
                        i

                k = 0
            }
            else
                k++

            if (k == pattern.length)
                return start
        }

       throw Exception("$pattern doesn't exist in $source")
    }
}