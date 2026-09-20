package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = AccentViolet,
  onPrimary = Color.White,
  primaryContainer = SurfaceElevated,
  onPrimaryContainer = AccentMint,
  secondary = AccentMint,
  onSecondary = Color.Black,
  tertiary = AccentBlue,
  background = BackgroundDark,
  onBackground = TextPrimary,
  surface = SurfaceDark,
  onSurface = TextPrimary,
  surfaceVariant = SurfaceElevated,
  onSurfaceVariant = TextSecondary,
  outline = SurfaceBorder,
  error = StatusDanger,
  onError = Color.White
)

private val LightColorScheme = lightColorScheme(
  primary = AccentViolet,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFE8E5FF),
  onPrimaryContainer = Color(0xFF281077),
  secondary = Color(0xFF00897B),
  onSecondary = Color.White,
  tertiary = Color(0xFF1976D2),
  background = Color(0xFFF4F6FB),
  onBackground = Color(0xFF10141D),
  surface = Color.White,
  onSurface = Color(0xFF10141D),
  surfaceVariant = Color(0xFFE9EDF5),
  onSurfaceVariant = Color(0xFF4C566A),
  outline = Color(0xFFD0D7E5),
  error = StatusDanger,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to sleek executive dark theme
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

