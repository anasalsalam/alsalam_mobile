package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val PolishPrimary = Color(0xFF6750A4)
private val PolishOnPrimary = Color(0xFFFFFFFF)
private val PolishPrimaryContainer = Color(0xFFEADDFF)
private val PolishOnPrimaryContainer = Color(0xFF21005D)
private val PolishBackground = Color(0xFFFEF7FF)
private val PolishOnBackground = Color(0xFF1D1B20)
private val PolishSurface = Color(0xFFF3EDF7)
private val PolishOnSurface = Color(0xFF1D1B20)
private val PolishOutline = Color(0xFFCAC4D0)

private val DarkColorScheme =
  darkColorScheme(
    primary = PolishPrimary,
    onPrimary = PolishOnPrimary,
    primaryContainer = PolishPrimaryContainer,
    onPrimaryContainer = PolishOnPrimaryContainer,
    background = Color(0xFF141218), // Dark version of Polish surface
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF211F24),
    onSurface = Color(0xFFE6E1E5),
    outline = PolishOutline
  )

private val LightColorScheme =
  lightColorScheme(
    primary = PolishPrimary,
    onPrimary = PolishOnPrimary,
    primaryContainer = PolishPrimaryContainer,
    onPrimaryContainer = PolishOnPrimaryContainer,
    background = PolishBackground,
    onBackground = PolishOnBackground,
    surface = PolishSurface,
    onSurface = PolishOnSurface,
    outline = PolishOutline
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic colors by default so our professional theme always renders
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
