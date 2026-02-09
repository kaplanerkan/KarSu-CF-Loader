# KarSu CF Loaders

Circular Fillable Loader widget for Android with wave animation, text overlay, and extensive customization options.

A custom Android widget with rich text overlay, subtitle, auto-sizing, and shadow support.

<!-- Ekran goruntuleri screenshots/ dizininde bulunur -->

## Features

- Circular wave-fill animation with configurable speed and amplitude
- Image source support (with or without background image)
- Primary text overlay with font family, style, size, color, and positioning
- Subtitle text below primary text
- Auto progress percentage display with custom format
- Text shadow effects
- Auto-size text to fit within circle
- Border customization
- RecyclerView compatible with proper view recycling

## Modules

| Module | Description |
|--------|-------------|
| `karsu_cfl` | Library module - the `KarSuCfLoaders` custom widget |
| `karsu_cf_loader` | Sample app demonstrating all features |

## Sample App

The sample app contains three demo screens:

- **MainActivity** - Loader widget with interactive controls (progress, border width, wave amplitude, wave color)
- **TextControlsActivity** - Text overlay controls (text input, text size, position offsets, text color, progress toggle)
- **RecyclerViewActivity** - Demonstrates the widget inside a RecyclerView with 15 different configurations

## XML Attributes

### Core
| Attribute | Format | Description |
|-----------|--------|-------------|
| `cfl_progress` | integer | Fill level (0-100) |
| `cfl_border` | boolean | Show/hide border |
| `cfl_border_width` | dimension | Border stroke width |
| `cfl_wave_color` | color | Wave fill color |
| `cfl_wave_amplitude` | float | Wave height ratio |
| `cfl_wave_enabled` | boolean | Enable/disable wave animation |
| `cfl_wave_speed` | integer | Wave cycle duration (ms) |

### Text
| Attribute | Format | Description |
|-----------|--------|-------------|
| `cfl_text` | string | Custom overlay text |
| `cfl_text_size` | dimension | Text size |
| `cfl_text_color` | color | Text color |
| `cfl_text_font_family` | string | Font family |
| `cfl_text_style` | enum | normal, bold, italic, bold_italic |
| `cfl_text_letter_spacing` | float | Letter spacing (em) |
| `cfl_text_offset_x` | dimension | Horizontal text offset |
| `cfl_text_offset_y` | dimension | Vertical text offset |
| `cfl_text_width_mode` | enum | wrap_content, match_parent |
| `cfl_show_progress_text` | boolean | Auto-display progress % |
| `cfl_progress_text_format` | string | Progress format (default: "%d%%") |

### Subtitle
| Attribute | Format | Description |
|-----------|--------|-------------|
| `cfl_subtitle_text` | string | Subtitle text |
| `cfl_subtitle_text_size` | dimension | Subtitle size |
| `cfl_subtitle_text_color` | color | Subtitle color |
| `cfl_subtitle_font_family` | string | Subtitle font family |
| `cfl_subtitle_text_style` | enum | normal, bold, italic, bold_italic |
| `cfl_subtitle_offset_y` | dimension | Subtitle vertical offset |

### Text Shadow
| Attribute | Format | Description |
|-----------|--------|-------------|
| `cfl_text_shadow_color` | color | Shadow color |
| `cfl_text_shadow_radius` | float | Shadow blur radius |
| `cfl_text_shadow_dx` | float | Shadow horizontal offset |
| `cfl_text_shadow_dy` | float | Shadow vertical offset |

### Auto-Size
| Attribute | Format | Description |
|-----------|--------|-------------|
| `cfl_auto_size_text` | boolean | Enable auto-sizing |
| `cfl_auto_size_min_text_size` | dimension | Minimum auto-size |

## Usage

```xml
<com.karsu.cfl.KarSuCfLoaders
    android:layout_width="200dp"
    android:layout_height="wrap_content"
    android:src="@drawable/your_image"
    app:cfl_progress="75"
    app:cfl_show_progress_text="true"
    app:cfl_text_color="@android:color/white"
    app:cfl_text_size="24sp"
    app:cfl_text_style="bold"
    app:cfl_wave_color="#4CAF50" />
```

## Requirements

- Min SDK: 21 (Android 5.0)
- Target SDK: 36
- Kotlin / JVM 17

## License

MIT License - Copyright (c) 2026 Erkan Kaplan (KarSu)

See [LICENSE](LICENSE) for details.
