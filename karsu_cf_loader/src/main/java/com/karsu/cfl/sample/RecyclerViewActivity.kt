/**
 * RecyclerViewActivity - Demonstrates KarSuCfLoaders inside a RecyclerView.
 *
 * Displays a scrollable list of loader items, each showing a circular fillable
 * loader with varying progress, colors, and text alongside descriptive labels.
 * This activity validates that the widget handles view recycling correctly
 * (animation restart, resource cleanup).
 *
 * @author Erkan Kaplan
 * @since 2026-02-08
 */
package com.karsu.cfl.sample

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.karsu.cfl.sample.databinding.ActivityRecyclerviewBinding

class RecyclerViewActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "KarSuCFL"
    }

    private lateinit var binding: ActivityRecyclerviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        val adapter = LoaderAdapter { item -> onItemClicked(item) }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        adapter.submitList(createSampleItems())
    }

    private fun onItemClicked(item: LoaderItem) {
        val colorHex = "#%06X".format(0xFFFFFF and item.waveColor)
        val info = "Title: ${item.title} | Progress: ${item.progress}% | Color: $colorHex" +
            (item.text?.let { " | Text: $it" } ?: "") +
            " | ShowProgress: ${item.showProgressText}"

        Log.d(TAG, "Item clicked → $info")
        Log.d(TAG, "  Description: ${item.description}")

        binding.infoCard.visibility = View.VISIBLE
        binding.textItemInfo.text = info
    }

    /**
     * Creates a diverse set of sample loader items to demonstrate various
     * widget configurations inside a RecyclerView.
     */
    private fun createSampleItems(): List<LoaderItem> = listOf(
        LoaderItem(
            title = "File Upload",
            description = "Uploading document to cloud storage...",
            progress = 75,
            waveColor = Color.parseColor("#4CAF50")
        ),
        LoaderItem(
            title = "Image Processing",
            description = "Applying filters and resizing images",
            progress = 45,
            waveColor = Color.parseColor("#2196F3")
        ),
        LoaderItem(
            title = "Data Sync",
            description = "Synchronizing local database with server",
            progress = 90,
            waveColor = Color.parseColor("#FF9800")
        ),
        LoaderItem(
            title = "Video Encoding",
            description = "Converting video to MP4 format",
            progress = 30,
            waveColor = Color.parseColor("#E91E63"),
            text = "30%"
        ),
        LoaderItem(
            title = "Download Complete",
            description = "All files have been downloaded successfully",
            progress = 100,
            waveColor = Color.parseColor("#4CAF50"),
            text = "Done"
        ),
        LoaderItem(
            title = "Backup in Progress",
            description = "Creating incremental backup of app data",
            progress = 60,
            waveColor = Color.parseColor("#9C27B0")
        ),
        LoaderItem(
            title = "Cache Cleanup",
            description = "Removing temporary files and expired cache",
            progress = 15,
            waveColor = Color.parseColor("#607D8B")
        ),
        LoaderItem(
            title = "Firmware Update",
            description = "Installing system firmware v2.4.1",
            progress = 55,
            waveColor = Color.parseColor("#FF5722"),
            text = "55%"
        ),
        LoaderItem(
            title = "Music Streaming",
            description = "Buffering audio stream at 320kbps",
            progress = 80,
            waveColor = Color.parseColor("#00BCD4")
        ),
        LoaderItem(
            title = "Photo Backup",
            description = "Uploading 247 photos to gallery",
            progress = 35,
            waveColor = Color.parseColor("#8BC34A")
        ),
        LoaderItem(
            title = "App Installation",
            description = "Installing dependencies and configuring app",
            progress = 20,
            waveColor = Color.parseColor("#3F51B5")
        ),
        LoaderItem(
            title = "Email Sync",
            description = "Fetching new messages from mail server",
            progress = 95,
            waveColor = Color.parseColor("#009688"),
            text = "Almost done"
        ),
        LoaderItem(
            title = "Map Download",
            description = "Downloading offline map tiles for region",
            progress = 50,
            waveColor = Color.parseColor("#795548")
        ),
        LoaderItem(
            title = "Security Scan",
            description = "Scanning files for potential threats",
            progress = 70,
            waveColor = Color.parseColor("#F44336")
        ),
        LoaderItem(
            title = "Waiting",
            description = "Queued for processing, please wait...",
            progress = 5,
            waveColor = Color.parseColor("#9E9E9E"),
            showProgressText = false
        )
    )
}
