/**
 * KarSuCfLoaders - A circular fillable loader widget with wave animation and text overlay.
 *
 * Displays a circular image filled with an animated wave effect that represents progress.
 * Supports overlay text (primary + subtitle), auto progress percentage display,
 * text shadow, auto-sizing, and full programmatic control.
 *
 * ## XML Usage Example
 * ```xml
 * <com.karsu.cfl.KarSuCfLoaders
 *     android:layout_width="200dp"
 *     android:layout_height="200dp"
 *     android:src="@drawable/logo"
 *     app:cfl_progress="80"
 *     app:cfl_wave_color="#3f51b5"
 *     app:cfl_text="Loading"
 *     app:cfl_text_size="18sp"
 *     app:cfl_text_color="@android:color/white"
 *     app:cfl_show_progress_text="true"
 *     app:cfl_subtitle_text="Please wait..." />
 * ```
 *
 * ## XML Attributes
 * | Attribute | Format | Description |
 * |---|---|---|
 * | `cfl_progress` | integer | Fill level 0-100 |
 * | `cfl_border` | boolean | Show/hide border |
 * | `cfl_border_width` | dimension | Border stroke width |
 * | `cfl_wave_color` | color | Wave fill color |
 * | `cfl_wave_amplitude` | float | Wave height ratio (0.0-0.05) |
 * | `cfl_wave_enabled` | boolean | Enable/disable wave animation |
 * | `cfl_wave_speed` | integer | Wave cycle duration in ms |
 * | `cfl_text` | string | Primary overlay text |
 * | `cfl_text_size` | dimension | Primary text size |
 * | `cfl_text_color` | color | Primary text color |
 * | `cfl_text_font_family` | string | Font family name |
 * | `cfl_text_style` | enum | normal, bold, italic, bold_italic |
 * | `cfl_text_letter_spacing` | float | Letter spacing in ems |
 * | `cfl_text_offset_x` | dimension | Horizontal text offset from center |
 * | `cfl_text_offset_y` | dimension | Vertical text offset from center |
 * | `cfl_text_width_mode` | enum | wrap_content or match_parent |
 * | `cfl_text_shadow_color` | color | Text shadow color |
 * | `cfl_text_shadow_radius` | float | Text shadow blur radius |
 * | `cfl_text_shadow_dx` | float | Text shadow horizontal offset |
 * | `cfl_text_shadow_dy` | float | Text shadow vertical offset |
 * | `cfl_show_progress_text` | boolean | Auto-display progress percentage |
 * | `cfl_progress_text_format` | string | Format string for progress text |
 * | `cfl_subtitle_text` | string | Subtitle text below primary |
 * | `cfl_subtitle_text_size` | dimension | Subtitle text size |
 * | `cfl_subtitle_text_color` | color | Subtitle text color |
 * | `cfl_subtitle_font_family` | string | Subtitle font family |
 * | `cfl_subtitle_text_style` | enum | Subtitle text style |
 * | `cfl_subtitle_offset_y` | dimension | Extra gap between primary and subtitle |
 * | `cfl_auto_size_text` | boolean | Auto-shrink text to fit circle |
 * | `cfl_auto_size_min_text_size` | dimension | Minimum text size for auto-sizing |
 *
 * @author Erkan Kaplan
 * @since 2026-02-08
 */
package com.karsu.cfl

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.core.graphics.createBitmap
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

class KarSuCfLoaders @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "KarSuCfLoaders"

        // Wave defaults
        private const val DEFAULT_AMPLITUDE_RATIO = 0.05f
        private const val DEFAULT_WATER_LEVEL_RATIO = 0.5f
        private const val DEFAULT_WAVE_LENGTH_RATIO = 1.0f
        private const val DEFAULT_WAVE_SHIFT_RATIO = 0.0f
        private const val DEFAULT_WAVE_SPEED_MS = 1000L

        /** Default wave color (black). */
        const val DEFAULT_WAVE_COLOR = Color.BLACK

        /** Default border width in dp. */
        const val DEFAULT_BORDER_WIDTH = 10

        // Text defaults
        private const val DEFAULT_TEXT_SIZE_SP = 14f
        private const val DEFAULT_TEXT_COLOR = Color.WHITE
        private const val DEFAULT_SUBTITLE_TEXT_SIZE_SP = 12f
        private const val DEFAULT_AUTO_SIZE_MIN_SP = 8f

        /** Text width mode: wraps to natural text width. */
        const val TEXT_WIDTH_WRAP_CONTENT = 0

        /** Text width mode: fills ~85% of the circle diameter. */
        const val TEXT_WIDTH_MATCH_PARENT = 1
    }

    // region Wave & Loader Properties

    private var canvasSize = 0
    private var amplitudeRatio = 0f
    private var waveColor = DEFAULT_WAVE_COLOR
    private var waterLevelRatio = 1f
    private var waveShiftRatio = DEFAULT_WAVE_SHIFT_RATIO
    private var defaultWaterLevel = 0f
    private var waveEnabled = true
    private var waveSpeed = DEFAULT_WAVE_SPEED_MS

    // endregion

    // region Drawing Objects (cached, never created in onDraw)

    private var image: Bitmap? = null
    private var currentDrawable: Drawable? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }
    private val wavePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var waveShader: BitmapShader? = null
    private val waveShaderMatrix = Matrix()

    // endregion

    // region Text Properties

    /** Primary overlay text, or null to hide. */
    private var _text: String? = null

    /** Primary text size in pixels. */
    private var _textSize: Float = spToPx(DEFAULT_TEXT_SIZE_SP)

    /** Primary text color. */
    private var _textColor: Int = DEFAULT_TEXT_COLOR

    /** Font family name for primary text (e.g., "sans-serif-medium"). */
    private var _textFontFamily: String? = null

    /** Text style: Typeface.NORMAL, BOLD, ITALIC, or BOLD_ITALIC. */
    private var _textStyle: Int = Typeface.NORMAL

    /** Letter spacing in ems for primary text. */
    private var _textLetterSpacing: Float = 0f

    /** Horizontal offset from circle center in pixels. */
    private var _textOffsetX: Float = 0f

    /** Vertical offset from circle center in pixels. */
    private var _textOffsetY: Float = 0f

    /** Text width mode: TEXT_WIDTH_WRAP_CONTENT or TEXT_WIDTH_MATCH_PARENT. */
    private var _textWidthMode: Int = TEXT_WIDTH_WRAP_CONTENT

    // endregion

    // region Text Shadow Properties

    private var _textShadowColor: Int = Color.TRANSPARENT
    private var _textShadowRadius: Float = 0f
    private var _textShadowDx: Float = 0f
    private var _textShadowDy: Float = 0f

    // endregion

    // region Progress Text Properties

    /** Whether to auto-display progress as formatted text. */
    private var _showProgressText: Boolean = false

    /** Format string for progress text (e.g., "%d%%"). */
    private var _progressTextFormat: String = "%d%%"

    /** Current progress value tracked for text display. */
    private var _currentProgress: Int = 0

    // endregion

    // region Subtitle Properties

    /** Subtitle text displayed below primary text, or null to hide. */
    private var _subtitleText: String? = null

    /** Subtitle text size in pixels. */
    private var _subtitleTextSize: Float = spToPx(DEFAULT_SUBTITLE_TEXT_SIZE_SP)

    /** Subtitle text color. */
    private var _subtitleTextColor: Int = DEFAULT_TEXT_COLOR

    /** Font family name for subtitle text. */
    private var _subtitleFontFamily: String? = null

    /** Subtitle text style. */
    private var _subtitleTextStyle: Int = Typeface.NORMAL

    /** Extra vertical gap between primary text and subtitle in pixels. */
    private var _subtitleOffsetY: Float = 0f

    // endregion

    // region Auto-Size Properties

    /** Whether to auto-shrink text to fit within the circle. */
    private var _autoSizeText: Boolean = false

    /** Minimum text size in pixels for auto-sizing. */
    private var _autoSizeMinTextSize: Float = spToPx(DEFAULT_AUTO_SIZE_MIN_SP)

    // endregion

    // region Text Paint & Layout (cached)

    /** Paint object for primary text rendering. */
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = DEFAULT_TEXT_COLOR
        textAlign = Paint.Align.CENTER
    }

    /** Paint object for subtitle text rendering. */
    private val subtitleTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = DEFAULT_TEXT_COLOR
        textAlign = Paint.Align.CENTER
    }

    /** Cached StaticLayout for primary text. Rebuilt when textLayoutDirty is true. */
    private var textLayout: StaticLayout? = null

    /** Cached StaticLayout for subtitle text. Rebuilt when subtitleLayoutDirty is true. */
    private var subtitleLayout: StaticLayout? = null

    /** Flag indicating the primary text layout needs to be rebuilt. */
    private var textLayoutDirty: Boolean = true

    /** Flag indicating the subtitle layout needs to be rebuilt. */
    private var subtitleLayoutDirty: Boolean = true

    // endregion

    // region Animation

    private var animatorSetWave: AnimatorSet? = null
    private var firstLoadBitmap = true

    // endregion

    init {
        initAnimation()

        context.obtainStyledAttributes(attrs, R.styleable.KarSuCfLoaders, defStyleAttr, 0).apply {

            // Core loader attributes
            waveColor = getColor(R.styleable.KarSuCfLoaders_cfl_wave_color, DEFAULT_WAVE_COLOR)
            val amplitudeAttr = getFloat(R.styleable.KarSuCfLoaders_cfl_wave_amplitude, DEFAULT_AMPLITUDE_RATIO)
            amplitudeRatio = if (amplitudeAttr > DEFAULT_AMPLITUDE_RATIO) DEFAULT_AMPLITUDE_RATIO else amplitudeAttr

            if (getBoolean(R.styleable.KarSuCfLoaders_cfl_border, true)) {
                val defaultBorderSize = DEFAULT_BORDER_WIDTH * context.resources.displayMetrics.density
                borderPaint.strokeWidth = getDimension(R.styleable.KarSuCfLoaders_cfl_border_width, defaultBorderSize)
            } else {
                borderPaint.strokeWidth = 0f
            }

            // Wave control
            waveEnabled = getBoolean(R.styleable.KarSuCfLoaders_cfl_wave_enabled, true)
            waveSpeed = getInteger(R.styleable.KarSuCfLoaders_cfl_wave_speed, DEFAULT_WAVE_SPEED_MS.toInt()).toLong()

            // Primary text
            _text = getString(R.styleable.KarSuCfLoaders_cfl_text)
            _textSize = getDimension(
                R.styleable.KarSuCfLoaders_cfl_text_size,
                spToPx(DEFAULT_TEXT_SIZE_SP)
            )
            _textColor = getColor(R.styleable.KarSuCfLoaders_cfl_text_color, DEFAULT_TEXT_COLOR)
            _textFontFamily = getString(R.styleable.KarSuCfLoaders_cfl_text_font_family)
            _textStyle = getInt(R.styleable.KarSuCfLoaders_cfl_text_style, Typeface.NORMAL)
            _textLetterSpacing = getFloat(R.styleable.KarSuCfLoaders_cfl_text_letter_spacing, 0f)
            _textOffsetX = getDimension(R.styleable.KarSuCfLoaders_cfl_text_offset_x, 0f)
            _textOffsetY = getDimension(R.styleable.KarSuCfLoaders_cfl_text_offset_y, 0f)
            _textWidthMode = getInt(R.styleable.KarSuCfLoaders_cfl_text_width_mode, TEXT_WIDTH_WRAP_CONTENT)

            // Text shadow
            _textShadowColor = getColor(R.styleable.KarSuCfLoaders_cfl_text_shadow_color, Color.TRANSPARENT)
            _textShadowRadius = getFloat(R.styleable.KarSuCfLoaders_cfl_text_shadow_radius, 0f)
            _textShadowDx = getFloat(R.styleable.KarSuCfLoaders_cfl_text_shadow_dx, 0f)
            _textShadowDy = getFloat(R.styleable.KarSuCfLoaders_cfl_text_shadow_dy, 0f)

            // Progress text
            _showProgressText = getBoolean(R.styleable.KarSuCfLoaders_cfl_show_progress_text, false)
            _progressTextFormat = getString(R.styleable.KarSuCfLoaders_cfl_progress_text_format) ?: "%d%%"

            // Subtitle
            _subtitleText = getString(R.styleable.KarSuCfLoaders_cfl_subtitle_text)
            _subtitleTextSize = getDimension(
                R.styleable.KarSuCfLoaders_cfl_subtitle_text_size,
                spToPx(DEFAULT_SUBTITLE_TEXT_SIZE_SP)
            )
            _subtitleTextColor = getColor(R.styleable.KarSuCfLoaders_cfl_subtitle_text_color, DEFAULT_TEXT_COLOR)
            _subtitleFontFamily = getString(R.styleable.KarSuCfLoaders_cfl_subtitle_font_family)
            _subtitleTextStyle = getInt(R.styleable.KarSuCfLoaders_cfl_subtitle_text_style, Typeface.NORMAL)
            _subtitleOffsetY = getDimension(R.styleable.KarSuCfLoaders_cfl_subtitle_offset_y, 0f)

            // Auto-size
            _autoSizeText = getBoolean(R.styleable.KarSuCfLoaders_cfl_auto_size_text, false)
            _autoSizeMinTextSize = getDimension(
                R.styleable.KarSuCfLoaders_cfl_auto_size_min_text_size,
                spToPx(DEFAULT_AUTO_SIZE_MIN_SP)
            )

            // Read progress last (triggers animation and uses _showProgressText)
            setProgress(getInteger(R.styleable.KarSuCfLoaders_cfl_progress, 0))

            recycle()
        }

        // Apply initial paint state from attributes
        applyTextPaintProperties()
        applySubtitlePaintProperties()
    }

    // region Draw Methods

    override fun onDraw(canvas: Canvas) {
        loadBitmap()

        if (image == null) return

        if (!isInEditMode) {
            canvasSize = min(width, height)
        }

        // Draw circular image
        val circleCenter = canvasSize / 2
        canvas.drawCircle(
            circleCenter.toFloat(),
            circleCenter.toFloat(),
            circleCenter.toFloat() - borderPaint.strokeWidth,
            paint
        )

        // Draw wave
        val shader = waveShader
        if (shader != null) {
            if (wavePaint.shader == null) {
                wavePaint.shader = shader
            }

            waveShaderMatrix.setScale(1f, amplitudeRatio / DEFAULT_AMPLITUDE_RATIO, 0f, defaultWaterLevel)

            val width = width
            val height = height
            waveShaderMatrix.postTranslate(
                waveShiftRatio * width,
                (DEFAULT_WATER_LEVEL_RATIO - waterLevelRatio) * height
            )

            shader.setLocalMatrix(waveShaderMatrix)

            // Draw border
            borderPaint.color = waveColor
            val borderWidth = borderPaint.strokeWidth
            if (borderWidth > 0) {
                canvas.drawCircle(width / 2f, height / 2f, (width - borderWidth) / 2f - 1f, borderPaint)
            }

            // Draw wave
            val radius = width / 2f - borderWidth
            canvas.drawCircle(width / 2f, height / 2f, radius, wavePaint)
        } else {
            wavePaint.shader = null
        }

        // Draw text overlay on top of everything
        drawTextOverlay(canvas)
    }

    /**
     * Draws the primary text and optional subtitle centered on the circle.
     * Called at the end of [onDraw] so text always appears on top of the wave.
     */
    private fun drawTextOverlay(canvas: Canvas) {
        val displayText = resolveDisplayText() ?: return

        val circleRadius = canvasSize / 2f - borderPaint.strokeWidth
        val centerX = width / 2f
        val centerY = height / 2f
        val maxTextWidth = (circleRadius * 2 * 0.85f).toInt()

        // Auto-size text if enabled (adjusts textPaint.textSize before layout)
        if (_autoSizeText) {
            autoFitTextSize(textPaint, displayText, maxTextWidth.toFloat())
        }

        // Rebuild primary text layout if dirty
        if (textLayoutDirty) {
            val layoutWidth = if (_textWidthMode == TEXT_WIDTH_MATCH_PARENT) {
                maxTextWidth
            } else {
                textPaint.measureText(displayText).toInt().coerceAtMost(maxTextWidth)
            }

            textLayout = if (layoutWidth > 0) {
                buildStaticLayout(displayText, textPaint, layoutWidth)
            } else {
                null
            }
            textLayoutDirty = false
        }

        val mainLayout = textLayout ?: return

        // Rebuild subtitle layout if dirty
        val subText = _subtitleText
        if (subtitleLayoutDirty) {
            subtitleLayout = if (subText != null && maxTextWidth > 0) {
                buildStaticLayout(subText, subtitleTextPaint, maxTextWidth)
            } else {
                null
            }
            subtitleLayoutDirty = false
        }

        val subLayout = subtitleLayout

        // Calculate total text block height for vertical centering
        val totalHeight = if (subLayout != null) {
            mainLayout.height + _subtitleOffsetY + subLayout.height
        } else {
            mainLayout.height.toFloat()
        }

        // Draw text block centered with offsets
        canvas.save()
        val startY = centerY - totalHeight / 2f + _textOffsetY
        canvas.translate(centerX + _textOffsetX, startY)
        mainLayout.draw(canvas)

        // Draw subtitle below primary text
        if (subLayout != null) {
            canvas.translate(0f, mainLayout.height.toFloat() + _subtitleOffsetY)
            subLayout.draw(canvas)
        }

        canvas.restore()
    }

    /**
     * Resolves which text to display.
     * Priority: explicit text > auto progress text > null (no text).
     */
    private fun resolveDisplayText(): String? {
        return _text ?: if (_showProgressText) {
            String.format(_progressTextFormat, _currentProgress)
        } else {
            null
        }
    }

    /**
     * Binary search for the largest text size that fits within [maxWidth].
     * Adjusts [paint]'s textSize in place without calling invalidate.
     */
    private fun autoFitTextSize(paint: TextPaint, text: String, maxWidth: Float) {
        var hi = _textSize
        var lo = _autoSizeMinTextSize

        while (hi - lo > 1f) {
            val mid = (hi + lo) / 2f
            paint.textSize = mid
            if (paint.measureText(text) <= maxWidth) {
                lo = mid
            } else {
                hi = mid
            }
        }
        paint.textSize = lo
    }

    /**
     * Creates a [StaticLayout] compatible with API 21+.
     * Uses [StaticLayout.Builder] on API 23+ and the deprecated constructor on older versions.
     */
    private fun buildStaticLayout(text: String, paint: TextPaint, width: Int): StaticLayout {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setIncludePad(false)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(text, paint, width, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false)
        }
    }

    private fun loadBitmap() {
        if (currentDrawable == drawable && !firstLoadBitmap) return

        currentDrawable = drawable
        image = drawableToBitmap(currentDrawable)
        firstLoadBitmap = false
        updateShader()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasSize = min(w, h)
        if (image != null) updateShader()

        // Text layouts depend on view size
        textLayoutDirty = true
        subtitleLayoutDirty = true
    }

    private fun updateShader() {
        val currentImage = image ?: return

        // Crop center image
        image = cropBitmap(currentImage)

        val croppedImage = image ?: return

        // Create shader
        val shader = BitmapShader(croppedImage, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        // Center image in shader
        val matrix = Matrix()
        matrix.setScale(
            canvasSize.toFloat() / croppedImage.width.toFloat(),
            canvasSize.toFloat() / croppedImage.height.toFloat()
        )
        shader.setLocalMatrix(matrix)

        paint.shader = shader

        updateWaveShader()
    }

    private fun updateWaveShader() {
        val width = width
        val height = height

        if (width <= 0 || height <= 0) return

        val defaultAngularFrequency = 2.0 * PI / DEFAULT_WAVE_LENGTH_RATIO / width
        val defaultAmplitude = height * DEFAULT_AMPLITUDE_RATIO
        defaultWaterLevel = height * DEFAULT_WATER_LEVEL_RATIO

        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)

        val localWavePaint = Paint().apply {
            strokeWidth = 2f
            isAntiAlias = true
        }

        val endX = width + 1
        val endY = height + 1

        val waveY = FloatArray(endX)

        // Draw first wave layer (semi-transparent, creates depth effect)
        localWavePaint.color = adjustAlpha(waveColor, 0.3f)
        for (beginX in 0 until endX) {
            val wx = beginX * defaultAngularFrequency
            val beginY = (defaultWaterLevel + defaultAmplitude * sin(wx)).toFloat()
            canvas.drawLine(beginX.toFloat(), beginY, beginX.toFloat(), endY.toFloat(), localWavePaint)
            waveY[beginX] = beginY
        }

        // Draw second wave layer (fully opaque, shifted by 1/4 wavelength)
        localWavePaint.color = waveColor
        val wave2Shift = width / 4
        for (beginX in 0 until endX) {
            canvas.drawLine(
                beginX.toFloat(),
                waveY[(beginX + wave2Shift) % endX],
                beginX.toFloat(),
                endY.toFloat(),
                localWavePaint
            )
        }

        waveShader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP)
        wavePaint.shader = waveShader
    }

    private fun cropBitmap(bitmap: Bitmap): Bitmap {
        return if (bitmap.width >= bitmap.height) {
            Bitmap.createBitmap(
                bitmap,
                bitmap.width / 2 - bitmap.height / 2,
                0,
                bitmap.height, bitmap.height
            )
        } else {
            Bitmap.createBitmap(
                bitmap,
                0,
                bitmap.height / 2 - bitmap.width / 2,
                bitmap.width, bitmap.width
            )
        }
    }

    private fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val intrinsicWidth: Int
        val intrinsicHeight: Int
        if (drawable == null) {
            intrinsicWidth = width
            intrinsicHeight = height
        } else {
            intrinsicWidth = drawable.intrinsicWidth
            intrinsicHeight = drawable.intrinsicHeight
        }

        if (intrinsicWidth <= 0 || intrinsicHeight <= 0) return null

        return try {
            val bitmap = createBitmap(intrinsicWidth, intrinsicHeight)
            if (drawable != null) {
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
            }
            bitmap
        } catch (e: OutOfMemoryError) {
            Log.e(TAG, "Encountered OutOfMemoryError while generating bitmap!")
            null
        }
    }

    // endregion

    // region Measure Methods

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var width = measureDimension(widthMeasureSpec)
        var height = measureDimension(heightMeasureSpec)

        // Inside a ScrollView the height spec is UNSPECIFIED, causing canvasSize (0) to
        // be returned. Since this is a square view, fall back to the resolved axis.
        if (heightMode == MeasureSpec.UNSPECIFIED && widthMode != MeasureSpec.UNSPECIFIED) {
            height = width
        } else if (widthMode == MeasureSpec.UNSPECIFIED && heightMode != MeasureSpec.UNSPECIFIED) {
            width = height
        }

        val imageSize = min(width, height)
        setMeasuredDimension(imageSize, imageSize)
    }

    private fun measureDimension(measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        return when (specMode) {
            MeasureSpec.EXACTLY, MeasureSpec.AT_MOST -> specSize
            else -> canvasSize
        }
    }

    // endregion

    // region Text Paint Helpers

    /**
     * Syncs [textPaint] properties from backing fields.
     * Called after any text property change.
     */
    private fun applyTextPaintProperties() {
        textPaint.textSize = _textSize
        textPaint.color = _textColor
        textPaint.letterSpacing = _textLetterSpacing
        textPaint.typeface = resolveTypeface(_textFontFamily, _textStyle)
        if (_textShadowRadius > 0f) {
            textPaint.setShadowLayer(_textShadowRadius, _textShadowDx, _textShadowDy, _textShadowColor)
        } else {
            textPaint.clearShadowLayer()
        }
        textLayoutDirty = true
    }

    /**
     * Syncs [subtitleTextPaint] properties from backing fields.
     * Called after any subtitle property change.
     */
    private fun applySubtitlePaintProperties() {
        subtitleTextPaint.textSize = _subtitleTextSize
        subtitleTextPaint.color = _subtitleTextColor
        subtitleTextPaint.typeface = resolveTypeface(_subtitleFontFamily, _subtitleTextStyle)
        subtitleLayoutDirty = true
    }

    /**
     * Creates a [Typeface] from font family name and style.
     *
     * @param fontFamily Font family name (e.g., "sans-serif-light"), or null for default.
     * @param textStyle Typeface style constant (e.g., [Typeface.BOLD]).
     * @return Resolved [Typeface] instance.
     */
    private fun resolveTypeface(fontFamily: String?, textStyle: Int): Typeface {
        val base = if (fontFamily != null) {
            Typeface.create(fontFamily, Typeface.NORMAL)
        } else {
            Typeface.DEFAULT
        }
        return Typeface.create(base, textStyle)
    }

    // endregion

    // region Public API - Wave & Loader

    /**
     * Sets the wave fill color.
     *
     * @param color Color integer (e.g., [Color.BLUE] or a resolved color resource).
     */
    fun setColor(color: Int) {
        waveColor = color
        updateWaveShader()
        invalidate()
    }

    /**
     * Sets the circular border stroke width.
     *
     * @param width Border width in pixels.
     */
    fun setBorderWidth(width: Float) {
        borderPaint.strokeWidth = width
        invalidate()
    }

    /**
     * Sets the wave amplitude ratio. Higher values create taller waves.
     *
     * @param amplitudeRatio Amplitude value between 0.0 and 0.05.
     */
    fun setAmplitudeRatio(amplitudeRatio: Float) {
        if (this.amplitudeRatio != amplitudeRatio) {
            this.amplitudeRatio = amplitudeRatio
            invalidate()
        }
    }

    /**
     * Animates the fill level to the given progress value.
     *
     * @param progress Target progress (0-100). 0 = empty, 100 = full.
     * @param milliseconds Animation duration in milliseconds. Default is 1000.
     */
    @JvmOverloads
    fun setProgress(progress: Int, milliseconds: Int = 1000) {
        _currentProgress = progress.coerceIn(0, 100)

        // Update text layout if showing progress text
        if (_showProgressText && _text == null) {
            textLayoutDirty = true
        }

        // Update accessibility description
        contentDescription = "Loading: $_currentProgress percent"

        val waterLevelAnim = ObjectAnimator.ofFloat(
            this, "waterLevelRatio", waterLevelRatio, 1f - (progress.toFloat() / 100)
        )
        waterLevelAnim.duration = milliseconds.toLong()
        waterLevelAnim.interpolator = DecelerateInterpolator()
        AnimatorSet().apply {
            play(waterLevelAnim)
            start()
        }
    }

    /**
     * Enables or disables the wave animation.
     * Disabling saves battery when the animation is not needed.
     *
     * @param enabled True to animate the wave, false to freeze it.
     */
    fun setWaveEnabled(enabled: Boolean) {
        waveEnabled = enabled
        if (enabled) {
            startAnimation()
        } else {
            cancel()
        }
    }

    /**
     * Sets the wave animation speed (one full cycle duration).
     *
     * @param durationMs Duration in milliseconds. Default is 1000.
     *                   Lower values = faster wave, higher values = slower wave.
     */
    fun setWaveSpeed(durationMs: Long) {
        waveSpeed = durationMs
        cancel()
        initAnimation()
        if (waveEnabled && visibility == VISIBLE) {
            startAnimation()
        }
    }

    // endregion

    // region Public API - Primary Text

    /**
     * Sets the primary overlay text displayed at the center of the loader.
     * When set, this takes priority over auto progress text.
     *
     * @param text The text to display, or null to hide (falls back to progress text if enabled).
     */
    fun setText(text: String?) {
        _text = text
        textLayoutDirty = true
        invalidate()
    }

    /**
     * Returns the current primary overlay text, or null if none is set.
     */
    fun getText(): String? = _text

    /**
     * Sets the primary text size in pixels.
     * Use [android.util.TypedValue.applyDimension] to convert from sp.
     *
     * @param size Text size in pixels.
     */
    fun setTextSize(size: Float) {
        _textSize = size
        applyTextPaintProperties()
        invalidate()
    }

    /**
     * Sets the primary text color.
     *
     * @param color Color integer (e.g., [Color.WHITE] or a resolved color resource).
     */
    fun setTextColor(color: Int) {
        _textColor = color
        applyTextPaintProperties()
        invalidate()
    }

    /**
     * Sets the font family for the primary text.
     *
     * @param fontFamily Font family name (e.g., "sans-serif-medium", "monospace"),
     *                   or null to use the default system font.
     */
    fun setTextFontFamily(fontFamily: String?) {
        _textFontFamily = fontFamily
        applyTextPaintProperties()
        invalidate()
    }

    /**
     * Sets the text style for the primary text.
     *
     * @param style One of [Typeface.NORMAL], [Typeface.BOLD], [Typeface.ITALIC],
     *              or [Typeface.BOLD_ITALIC].
     */
    fun setTextStyle(style: Int) {
        _textStyle = style
        applyTextPaintProperties()
        invalidate()
    }

    /**
     * Sets the letter spacing for the primary text.
     *
     * @param letterSpacing Letter spacing in ems. Default is 0.
     */
    fun setTextLetterSpacing(letterSpacing: Float) {
        _textLetterSpacing = letterSpacing
        applyTextPaintProperties()
        invalidate()
    }

    /**
     * Sets the horizontal offset of the text from the circle center.
     *
     * @param offsetX Offset in pixels. Positive moves right, negative moves left.
     */
    fun setTextOffsetX(offsetX: Float) {
        _textOffsetX = offsetX
        invalidate()
    }

    /**
     * Sets the vertical offset of the text from the circle center.
     *
     * @param offsetY Offset in pixels. Positive moves down, negative moves up.
     */
    fun setTextOffsetY(offsetY: Float) {
        _textOffsetY = offsetY
        invalidate()
    }

    /**
     * Sets how text width is measured within the circle.
     *
     * @param mode [TEXT_WIDTH_WRAP_CONTENT] to size text naturally,
     *             or [TEXT_WIDTH_MATCH_PARENT] to fill ~85% of circle diameter.
     */
    fun setTextWidthMode(mode: Int) {
        _textWidthMode = mode
        textLayoutDirty = true
        invalidate()
    }

    /**
     * Sets the text shadow layer for the primary text.
     * Pass radius 0 to clear the shadow.
     *
     * @param radius Blur radius of the shadow.
     * @param dx Horizontal offset of the shadow.
     * @param dy Vertical offset of the shadow.
     * @param color Shadow color.
     */
    fun setTextShadow(radius: Float, dx: Float, dy: Float, color: Int) {
        _textShadowRadius = radius
        _textShadowDx = dx
        _textShadowDy = dy
        _textShadowColor = color
        applyTextPaintProperties()
        invalidate()
    }

    // endregion

    // region Public API - Progress Text

    /**
     * Enables or disables automatic progress percentage text.
     * When enabled and no explicit text is set via [setText], the loader
     * displays the current progress as a formatted percentage string.
     *
     * @param show True to show progress text, false to hide it.
     */
    fun setShowProgressText(show: Boolean) {
        _showProgressText = show
        textLayoutDirty = true
        invalidate()
    }

    /**
     * Sets the format string for auto progress text.
     * Must contain one integer placeholder (e.g., "%d%%").
     *
     * @param format A [String.format]-compatible pattern. Default is "%d%%".
     */
    fun setProgressTextFormat(format: String) {
        _progressTextFormat = format
        textLayoutDirty = true
        invalidate()
    }

    // endregion

    // region Public API - Subtitle

    /**
     * Sets the subtitle text displayed below the primary text.
     *
     * @param text Subtitle text, or null to hide the subtitle.
     */
    fun setSubtitleText(text: String?) {
        _subtitleText = text
        subtitleLayoutDirty = true
        invalidate()
    }

    /**
     * Returns the current subtitle text, or null if none is set.
     */
    fun getSubtitleText(): String? = _subtitleText

    /**
     * Sets the subtitle text size in pixels.
     *
     * @param size Text size in pixels.
     */
    fun setSubtitleTextSize(size: Float) {
        _subtitleTextSize = size
        applySubtitlePaintProperties()
        invalidate()
    }

    /**
     * Sets the subtitle text color.
     *
     * @param color Color integer.
     */
    fun setSubtitleTextColor(color: Int) {
        _subtitleTextColor = color
        applySubtitlePaintProperties()
        invalidate()
    }

    /**
     * Sets the font family for the subtitle text.
     *
     * @param fontFamily Font family name, or null for default.
     */
    fun setSubtitleFontFamily(fontFamily: String?) {
        _subtitleFontFamily = fontFamily
        applySubtitlePaintProperties()
        invalidate()
    }

    /**
     * Sets the extra vertical gap between primary text and subtitle.
     *
     * @param offsetY Gap in pixels. Positive increases the gap.
     */
    fun setSubtitleOffsetY(offsetY: Float) {
        _subtitleOffsetY = offsetY
        invalidate()
    }

    // endregion

    // region Public API - RecyclerView Support

    /**
     * Releases cached resources for safe RecyclerView recycling.
     * Call this from [androidx.recyclerview.widget.RecyclerView.Adapter.onViewRecycled]
     * to prevent memory leaks and stale layout state.
     *
     * The widget will automatically reinitialize when reattached to a window.
     */
    fun recycle() {
        cancel()
        textLayout = null
        subtitleLayout = null
        textLayoutDirty = true
        subtitleLayoutDirty = true
    }

    // endregion

    // region Public API - Auto-Size

    /**
     * Enables or disables auto-sizing of text to fit within the circle.
     * When enabled, the text size is automatically reduced (down to [setAutoSizeMinTextSize])
     * to ensure the text fits within the circle's drawable area.
     *
     * @param enabled True to enable auto-sizing, false to use fixed text size.
     */
    fun setAutoSizeText(enabled: Boolean) {
        _autoSizeText = enabled
        textLayoutDirty = true
        invalidate()
    }

    /**
     * Sets the minimum text size for auto-sizing, in pixels.
     * Text will not be shrunk below this size even if it overflows.
     *
     * @param minSize Minimum size in pixels.
     */
    fun setAutoSizeMinTextSize(minSize: Float) {
        _autoSizeMinTextSize = minSize
        textLayoutDirty = true
        invalidate()
    }

    // endregion

    // region Animation

    private fun startAnimation() {
        if (waveEnabled) {
            animatorSetWave?.start()
        }
    }

    private fun initAnimation() {
        val waveShiftAnim = ObjectAnimator.ofFloat(this, "waveShiftRatio", 0f, 1f).apply {
            repeatCount = ValueAnimator.INFINITE
            duration = waveSpeed
            interpolator = LinearInterpolator()
        }

        animatorSetWave = AnimatorSet().apply {
            play(waveShiftAnim)
        }
    }

    /** Used by [ObjectAnimator] via reflection to animate wave horizontal shift. */
    @Suppress("unused")
    private fun setWaveShiftRatio(waveShiftRatio: Float) {
        if (this.waveShiftRatio != waveShiftRatio) {
            this.waveShiftRatio = waveShiftRatio
            invalidate()
        }
    }

    /** Used by [ObjectAnimator] via reflection to animate water fill level. */
    @Suppress("unused")
    private fun setWaterLevelRatio(waterLevelRatio: Float) {
        if (this.waterLevelRatio != waterLevelRatio) {
            this.waterLevelRatio = waterLevelRatio
            invalidate()
        }
    }

    private fun cancel() {
        animatorSetWave?.cancel()
        animatorSetWave = null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (animatorSetWave == null) {
            initAnimation()
        }
        startAnimation()
    }

    override fun onDetachedFromWindow() {
        cancel()
        super.onDetachedFromWindow()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == VISIBLE) {
            startAnimation()
        } else {
            cancel()
        }
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (getVisibility() != VISIBLE) return

        if (visibility == VISIBLE) {
            startAnimation()
        } else {
            cancel()
        }
    }

    // endregion

    // region Utilities

    /**
     * Adjusts the alpha channel of a color by a factor.
     *
     * @param color Original ARGB color.
     * @param factor Alpha multiplier (0.0 = transparent, 1.0 = original alpha).
     * @return Color with adjusted alpha.
     */
    private fun adjustAlpha(color: Int, factor: Float): Int {
        val alpha = (Color.alpha(color) * factor).roundToInt()
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }

    /**
     * Converts a value in SP (scaled pixels) to PX using [TypedValue.applyDimension].
     */
    private fun spToPx(sp: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)

    // endregion
}
