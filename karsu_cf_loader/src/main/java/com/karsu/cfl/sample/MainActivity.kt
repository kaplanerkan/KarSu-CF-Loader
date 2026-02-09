/**
 * MainActivity - Sample application demonstrating the KarSuCfLoaders custom widget.
 *
 * Provides interactive controls to adjust the circular fillable loader's properties
 * in real time, including:
 * - **Loader controls**: progress, border width, wave amplitude, wave color
 *
 * Text controls are available in a separate [TextControlsActivity].
 *
 * @author Erkan Kaplan
 * @since 2026-02-08
 */
package com.karsu.cfl.sample

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.karsu.cfl.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "KarSuCFL"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRecyclerViewDemo.setOnClickListener {
            startActivity(Intent(this, RecyclerViewActivity::class.java))
        }

        binding.btnTextControlsDemo.setOnClickListener {
            startActivity(Intent(this, TextControlsActivity::class.java))
        }

        setupLoaderControls()
    }

    /**
     * Sets up slider and color picker listeners for the loader's core properties:
     * progress, border width, wave amplitude, and wave color.
     */
    private fun setupLoaderControls() {
        // Progress slider: updates fill level (0-100)
        binding.seekBarProgress.addOnChangeListener { _, value, _ ->
            val progress = value.toInt()
            binding.labelProgress.text = "Progress: $progress"
            Log.d(TAG, "Progress: $progress")
            binding.karSuCfLoaders.setProgress(progress)
        }

        // Border width slider: updates circle border thickness
        binding.seekBarBorderWidth.addOnChangeListener { _, value, _ ->
            val progress = value.toInt()
            val borderWidth = progress * resources.displayMetrics.density
            binding.labelBorderWidth.text = "Border Width: $progress"
            Log.d(TAG, "BorderWidth: $progress (px: $borderWidth)")
            binding.karSuCfLoaders.setBorderWidth(borderWidth)
        }

        // Amplitude slider: updates wave height ratio
        binding.seekBarAmplitude.addOnChangeListener { _, value, _ ->
            val progress = value.toInt()
            val amplitudeRatio = progress.toFloat() / 1000
            binding.labelAmplitude.text = "Amplitude: $progress"
            Log.d(TAG, "Amplitude: $progress (ratio: $amplitudeRatio)")
            binding.karSuCfLoaders.setAmplitudeRatio(amplitudeRatio)
        }

        // Wave color slider: hue 0-360
        binding.sliderWaveColor.addOnChangeListener { _, value, _ ->
            val color = Color.HSVToColor(floatArrayOf(value, 1f, 1f))
            binding.karSuCfLoaders.setColor(color)
        }
    }
}
