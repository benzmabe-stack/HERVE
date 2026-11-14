package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = ElectricBlue,
    onPrimary = MidnightBlue,
    primaryContainer = NavyCard,
    onPrimaryContainer = BrightWhite,
    secondary = ElectricBlueVariant,
    onSecondary = BrightWhite,
    background = MidnightBlue,
    onBackground = BrightWhite,
    surface = NavySurface,
    onSurface = BrightWhite,
    surfaceVariant = NavyCard,
    onSurfaceVariant = TextGray
  )

private val LightColorScheme =
  lightColorScheme(
    primary = ElectricBlueVariant,
    onPrimary = BrightWhite,
    primaryContainer = Color(0xFFE0F7FA),
    onPrimaryContainer = MidnightBlue,
    secondary = ElectricBlue,
    onSecondary = MidnightBlue,
    background = LightBackground,
    onBackground = MidnightBlue,
    surface = LightSurface,
    onSurface = MidnightBlue,
    surfaceVariant = Color(0xFFE9ECEF),
    onSurfaceVariant = Color(0xFF495057)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Disabled by default for HP Navigate branded dark theme
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
