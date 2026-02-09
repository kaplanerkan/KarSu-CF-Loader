/**
 * Data class representing a single loader entry in the RecyclerView demo.
 *
 * Each item defines the visual properties of a [com.karsu.cfl.KarSuCfLoaders]
 * widget along with descriptive title and description text shown beside it.
 *
 * @property title       Primary label displayed next to the loader.
 * @property description Secondary text providing additional context.
 * @property progress    Fill level of the loader (0–100).
 * @property waveColor   Wave fill color as an ARGB integer.
 * @property text        Optional overlay text; when `null` the loader falls back
 *                       to its own display logic (e.g. progress percentage).
 * @property showProgressText Whether the loader should auto-display the progress
 *                            percentage when no explicit [text] is set.
 *
 * @author Erkan Kaplan
 * @since 2026-02-08
 */
package com.karsu.cfl.sample

data class LoaderItem(
    val title: String,
    val description: String,
    val progress: Int,
    val waveColor: Int,
    val text: String? = null,
    val showProgressText: Boolean = true
)
