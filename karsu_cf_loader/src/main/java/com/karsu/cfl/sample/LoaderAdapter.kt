/**
 * RecyclerView adapter that displays a list of [LoaderItem] entries.
 *
 * Each item is rendered as a [com.karsu.cfl.KarSuCfLoaders] widget paired with
 * a title and description. The adapter uses [ListAdapter] with [DiffUtil] for
 * efficient partial updates and properly calls [com.karsu.cfl.KarSuCfLoaders.recycle]
 * when views are recycled to prevent memory leaks.
 *
 * Usage:
 * ```kotlin
 * val adapter = LoaderAdapter()
 * recyclerView.adapter = adapter
 * adapter.submitList(items)
 * ```
 *
 * @author Erkan Kaplan
 * @since 2026-02-08
 */
package com.karsu.cfl.sample

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.karsu.cfl.sample.databinding.ItemLoaderBinding

class LoaderAdapter : ListAdapter<LoaderItem, LoaderAdapter.LoaderViewHolder>(LoaderDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoaderViewHolder {
        val binding = ItemLoaderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LoaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoaderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: LoaderViewHolder) {
        super.onViewRecycled(holder)
        holder.binding.loader.recycle()
    }

    /**
     * ViewHolder that binds a [LoaderItem] to the item layout.
     *
     * @property binding View binding for `item_loader.xml`.
     */
    class LoaderViewHolder(
        val binding: ItemLoaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the given [item] data to the loader and text views.
         */
        fun bind(item: LoaderItem) {
            binding.loader.setProgress(item.progress)
            binding.loader.setColor(item.waveColor)
            binding.loader.setText(item.text)
            binding.loader.setShowProgressText(item.showProgressText)
            binding.textTitle.text = item.title
            binding.textDescription.text = item.description
        }
    }

    /**
     * DiffUtil callback for efficient list updates.
     */
    private object LoaderDiffCallback : DiffUtil.ItemCallback<LoaderItem>() {
        override fun areItemsTheSame(oldItem: LoaderItem, newItem: LoaderItem): Boolean =
            oldItem.title == newItem.title

        override fun areContentsTheSame(oldItem: LoaderItem, newItem: LoaderItem): Boolean =
            oldItem == newItem
    }
}
