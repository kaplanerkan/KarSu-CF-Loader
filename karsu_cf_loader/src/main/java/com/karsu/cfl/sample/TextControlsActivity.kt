/**
 * TextControlsActivity - Demonstrates text overlay controls for the KarSuCfLoaders widget.
 *
 * Displays a single loader widget (without source image) alongside interactive
 * controls for adjusting text properties in real time, including:
 * - **Overlay text**: custom text input
 * - **Progress toggle**: show/hide auto progress percentage
 * - **Text size**: adjustable font size slider
 * - **Text offset**: horizontal and vertical position sliders
 * - **Text color**: color picker for overlay text
 *
 * @author Erkan Kaplan
 * @since 2026-02-09
 */
package com.karsu.cfl.sample

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.karsu.cfl.sample.databinding.ActivityTextControlsBinding

class TextControlsActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "KarSuCFL"
    }

    private lateinit var binding: ActivityTextControlsBinding
    private var textWatcher: TextWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextControlsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        setupTextControls()
    }

    /**
     * Sets up text overlay controls: text input, size slider, progress toggle,
     * offset sliders, and text color picker.
     */
    private fun setupTextControls() {
        // Text input: sets custom overlay text (overrides progress text)
        textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString()?.ifEmpty { null }
                binding.karSuCfLoadersNoSrc.setText(text)
                Log.d(TAG, "Text: ${text ?: "(null, using progress text)"}")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        binding.editTextOverlay.addTextChangedListener(textWatcher)

        // Show progress % toggle
        binding.switchShowProgress.setOnCheckedChangeListener { _, isChecked ->
            binding.karSuCfLoadersNoSrc.setShowProgressText(isChecked)
            Log.d(TAG, "ShowProgressText: $isChecked")
        }

        // Text size slider (in sp, converted to px)
        binding.seekBarTextSize.addOnChangeListener { _, value, _ ->
            val sp = value.toInt()
            val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), resources.displayMetrics)
            binding.labelTextSize.text = "Text Size: $sp"
            Log.d(TAG, "TextSize: ${sp}sp (px: $px)")
            binding.karSuCfLoadersNoSrc.setTextSize(px)
        }

        // Horizontal offset slider (in dp, converted to px)
        binding.seekBarTextOffsetX.addOnChangeListener { _, value, _ ->
            val dp = value.toInt()
            val px = dp * resources.displayMetrics.density
            binding.labelTextOffsetX.text = "Offset X: $dp"
            Log.d(TAG, "TextOffsetX: ${dp}dp (px: $px)")
            binding.karSuCfLoadersNoSrc.setTextOffsetX(px)
        }

        // Vertical offset slider (in dp, converted to px)
        binding.seekBarTextOffsetY.addOnChangeListener { _, value, _ ->
            val dp = value.toInt()
            val px = dp * resources.displayMetrics.density
            binding.labelTextOffsetY.text = "Offset Y: $dp"
            Log.d(TAG, "TextOffsetY: ${dp}dp (px: $px)")
            binding.karSuCfLoadersNoSrc.setTextOffsetY(px)
        }

        // Text color slider: hue 0-360
        binding.sliderTextColor.addOnChangeListener { _, value, _ ->
            val color = Color.HSVToColor(floatArrayOf(value, 1f, 1f))
            binding.karSuCfLoadersNoSrc.setTextColor(color)
        }

        // Wave color slider: hue 0-360
        binding.sliderWaveColor.addOnChangeListener { _, value, _ ->
            val color = Color.HSVToColor(floatArrayOf(value, 1f, 1f))
            binding.karSuCfLoadersNoSrc.setColor(color)
        }
    }

    override fun onDestroy() {
        binding.editTextOverlay.removeTextChangedListener(textWatcher)
        textWatcher = null
        binding.switchShowProgress.setOnCheckedChangeListener(null)
        binding.seekBarTextSize.clearOnChangeListeners()
        binding.seekBarTextOffsetX.clearOnChangeListeners()
        binding.seekBarTextOffsetY.clearOnChangeListeners()
        binding.sliderTextColor.clearOnChangeListeners()
        binding.sliderWaveColor.clearOnChangeListeners()
        super.onDestroy()
    }
}
