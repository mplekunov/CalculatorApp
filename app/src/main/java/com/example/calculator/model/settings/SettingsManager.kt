package com.example.calculator.model.settings

import android.content.Context
import androidx.preference.PreferenceManager
import com.example.calculator.converter.ColorConverter

class SettingsManager(private val context: Context) {

    fun getString(settingsKey: Int): String =
        PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(settingsKey), "")!!

    fun getInt(settingsKey: Int): Int =
        PreferenceManager.getDefaultSharedPreferences(context).getInt(context.getString(settingsKey), 0)

    fun getColor(settingsKey: Int): Int = ColorConverter.toIntColor(getString(settingsKey))

    fun setColor(settingsKey: Int, color: Int) = setString(settingsKey, ColorConverter.toHexColor(color))

    fun setString(settingsKey: Int, value: String) {
        with(PreferenceManager.getDefaultSharedPreferences(context).edit()) {
            putString(context.getString(settingsKey), value)
            apply()
        }
    }

    fun setInt(settingsKey: Int, value: Int) {
        with(PreferenceManager.getDefaultSharedPreferences(context).edit()) {
            putInt(context.getString(settingsKey), value)
            apply()
        }
    }
}