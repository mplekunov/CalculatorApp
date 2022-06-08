package com.example.calculator

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.example.calculator.model.settings.SettingsManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadDefaultSettings()
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    private fun loadDefaultSettings() {
        val settingsManager = SettingsManager(this)

        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true)

//        settingsManager.setColor(R.string.saved_input_font_color_key, ContextCompat.getColor(this, typedValue.resourceId))
        settingsManager.setColor(R.string.saved_output_font_color_key, ContextCompat.getColor(this, typedValue.resourceId))

        settingsManager.setColor(R.string.saved_number_button_color_key, ContextCompat.getColor(this, typedValue.resourceId))
        settingsManager.setColor(R.string.saved_function_button_color_key, resources.getColor(R.color.calc_function_button, theme))
        settingsManager.setColor(R.string.saved_operator_button_color_key, resources.getColor(R.color.calc_operation_button, theme))

        settingsManager.setColor(R.string.saved_clear_button_color_key, resources.getColor(R.color.calc_clear_button, theme))
        settingsManager.setColor(R.string.saved_clear_all_button_color_key, resources.getColor(R.color.calc_clear_all_button, theme))

        settingsManager.setColor(R.string.saved_disabled_button_color_key, resources.getColor(R.color.calc_disabled_button, theme))
        settingsManager.setColor(R.string.saved_highlighting_color_key, resources.getColor(R.color.highlighted_text, theme))

//        settingsManager.setString(R.string.saved_input_font_size_key, "35")
//        settingsManager.setString(R.string.saved_output_font_size_key, "20")
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.settings_item -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.action_calculatorFragment_to_settingsFragment)
                true
            }
            R.id.about_item -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://github.com/mplekunov/CalculatorApp")

                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
