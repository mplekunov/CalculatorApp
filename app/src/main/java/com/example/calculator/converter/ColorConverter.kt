package com.example.calculator.converter

import androidx.core.graphics.*

object ColorConverter {
    fun toHexColor(color: Int): String {
        var alpha = color.alpha.toString(16)
        if (alpha.length == 1)
            alpha = "0$alpha"

        var red = color.red.toString(16)
        if (red.length == 1)
            red = "0$red"

        var green = color.green.toString(16)
        if (green.length == 1)
            green = "0$green"

        var blue = color.blue.toString(16)
        if (blue.length == 1)
            blue = "0$blue"

        return "#$alpha$red$green$blue"
    }

    fun toIntColor(color: String): Int {
        return color.toColorInt()
    }
}