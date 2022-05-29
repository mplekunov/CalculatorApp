package com.example.calculator.view

import android.graphics.Typeface
import android.graphics.fonts.Font
import android.graphics.fonts.FontFamily
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.*
import androidx.preference.*
import com.example.calculator.R
import com.example.calculator.databinding.FragmentColorPickerBinding
import com.example.calculator.model.settings.SettingsManager
import java.util.*

class SettingsFragment : PreferenceFragmentCompat() {
    var colorPickerBiding: FragmentColorPickerBinding? = null
    private lateinit var preferenceMap: MutableMap<Int, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ColorPicker", "Settings onCreate")
        // override default option menu defined by activity
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        // removes option menu from current fragment
        menu.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        colorPickerBiding = FragmentColorPickerBinding.inflate(inflater, container, false)
        Log.d("ColorPicker", "Settings onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun setOnPreferenceClickTextSize(preferenceKey: Int) {
        findPreference<EditTextPreference>(getString(preferenceKey))?.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_CLASS_NUMBER
            it.selectAll()

            it.filters = arrayOf(InputFilter.LengthFilter(2))
        }
    }

    private fun setOnPreferenceClickColorPicker(preferenceKey: Int) {
        findPreference<Preference>(getString(preferenceKey))?.setOnPreferenceClickListener {
            val popupWindow = PopupWindow(context)

            popupWindow.contentView = colorPickerBiding!!.root
            popupWindow.isOutsideTouchable = true
            popupWindow.isFocusable = true

            val background = ContextCompat.getDrawable(requireContext(), R.drawable.round_corners)
            background!!.setTint(requireContext().getColor(R.color.highlighted_text))

            popupWindow.setBackgroundDrawable(background)


            popupWindow.showAtLocation(this.view, Gravity.CENTER, 0, 0)

            setColorSpectrumSeekBars()
            initColorSpectrumSeekBars(preferenceKey)

            colorPickerBiding?.colorApplyButton?.setOnClickListener {
                SettingsManager(requireContext()).setColor(preferenceKey, getHexColor().toColorInt())

                colorPickerBiding?.colorCancelButton?.performClick()
            }

            colorPickerBiding?.colorCancelButton?.setOnClickListener {
                popupWindow.dismiss()
            }

            true
        }
    }

    private fun initColorSpectrumSeekBars(preferenceKey: Int) {
        val hexColor = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(getString(preferenceKey), "")

        with(colorPickerBiding!!) {
            alphaSeekbar.progress = hexColor!!.subSequence(1..2).toString().toInt(16)
            redSeekbar.progress = hexColor.subSequence(3..4).toString().toInt(16)
            greenSeekbar.progress = hexColor.subSequence(5..6).toString().toInt(16)
            blueSeekbar.progress = hexColor.subSequence(7..8).toString().toInt(16)
        }
    }

    private fun setColorSpectrumSeekBars() {
        with(colorPickerBiding!!) {
            setOnColorSeekBarChangeListener(alphaSeekbar)
            setOnColorSeekBarChangeListener(redSeekbar)
            setOnColorSeekBarChangeListener(greenSeekbar)
            setOnColorSeekBarChangeListener(blueSeekbar)
        }
    }

    private fun setOnColorSeekBarChangeListener(seekBar: SeekBar) {
        seekBar.max = 255
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(
                seekBar: SeekBar, progress: Int,
                fromUser: Boolean
            ) {
                val colorStr = getHexColor()
                with(colorPickerBiding!!) {
                    colorText.text = colorStr.replace("#", "").uppercase(Locale.ROOT)
                    colorPreview.setBackgroundColor(colorStr.toColorInt())
                }
            }
        })
    }

    private fun getHexColor(): String {
        with(colorPickerBiding!!) {
            var alpha = alphaSeekbar.progress.toString(16)
            if (alpha.length == 1)
                alpha = "0$alpha"

            var red = redSeekbar.progress.toString(16)
            if (red.length == 1)
                red = "0$red"

            var green = greenSeekbar.progress.toString(16)
            if (green.length == 1)
                green = "0$green"

            var blue = blueSeekbar.progress.toString(16)
            if (blue.length == 1)
                blue = "0$blue"

            return "#$alpha$red$green$blue"
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        Log.d("ColorPicker", "Settings onCreatePreferences")

        preferenceMap = mutableMapOf(
            R.string.saved_input_font_color_key to getString(R.string.saved_input_font_color_key),
            R.string.saved_output_font_color_key to getString(R.string.saved_output_font_color_key),
            R.string.saved_function_button_color_key to getString(R.string.saved_function_button_color_key),
            R.string.saved_number_button_color_key to getString(R.string.saved_number_button_color_key),
            R.string.saved_operator_button_color_key to getString(R.string.saved_operator_button_color_key),
            R.string.saved_clear_button_color_key to getString(R.string.saved_clear_button_color_key),
            R.string.saved_clear_all_button_color_key to getString(R.string.saved_clear_all_button_color_key),
            R.string.saved_disabled_button_color_key to getString(R.string.saved_disabled_button_color_key),
            R.string.saved_highlighting_color_key to getString(R.string.saved_highlighting_color_key)
        )

        preferenceMap.forEach { (key, _) -> setOnPreferenceClickColorPicker(key) }

//        setOnPreferenceClickTextSize(R.string.saved_input_font_size_key)
//        setOnPreferenceClickTextSize(R.string.saved_output_font_size_key)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("ColorPicker", "Settings onViewCreated")

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Settings"
    }

    override fun onStart() {
        super.onStart()
        Log.d("ColorPicker", "Settings onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("ColorPicker", "Settings onResume")
    }
}