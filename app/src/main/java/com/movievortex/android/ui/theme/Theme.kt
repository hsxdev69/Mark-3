package com.movievortex.android.ui.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.preference.PreferenceManager
import com.lagradost.cloudstream3.R

// ─────────────────────────────────────────────────────────────────────────────
// ManropeFontFamily — Only regular & bold weights available in resources
// ─────────────────────────────────────────────────────────────────────────────
val ManropeFontFamily = FontFamily(
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_bold, FontWeight.Bold)
)

// ─────────────────────────────────────────────────────────────────────────────
// VortexColors — Dynamic Cinematic + Suspended Design System
// ─────────────────────────────────────────────────────────────────────────────
object VortexColors {

    var isDarkState = mutableStateOf(true)
    var currentAccent = mutableStateOf(Color(0xFFFFFFFF))

    fun updateTheme(themeName: String, accentName: String, systemInDark: Boolean) {
        val dark = when (themeName) {
            "Light" -> false
            "System Default" -> systemInDark
            else -> true // "Dark (OLED Pure Black)"
        }
        isDarkState.value = dark

        val accent = when {
            accentName.contains("Crimson", true) || accentName.contains("Red", true) -> Color(0xFFE50914)
            accentName.contains("Cyan", true) -> Color(0xFF00E5FF)
            accentName.contains("Blue", true) -> Color(0xFF0070F3)
            accentName.contains("Green", true) -> Color(0xFF10B981)
            accentName.contains("Gold", true) || accentName.contains("Amber", true) -> Color(0xFFF59E0B)
            accentName.contains("Purple", true) || accentName.contains("Violet", true) -> Color(0xFF8B5CF6)
            accentName.contains("Pink", true) -> Color(0xFFFF2A85)
            else -> if (dark) Color(0xFFFFFFFF) else Color(0xFF0F172A) // Pure White / Dark Slate
        }
        currentAccent.value = accent
    }

    fun getPlayerAccentColorInt(context: Context): Int {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val name = sp.getString("pref_accent_color", "Pure White (Cinematic)") ?: "Pure White (Cinematic)"
        return when {
            name.contains("Crimson", true) || name.contains("Red", true) -> android.graphics.Color.parseColor("#E50914")
            name.contains("Cyan", true) -> android.graphics.Color.parseColor("#00E5FF")
            name.contains("Blue", true) -> android.graphics.Color.parseColor("#0070F3")
            name.contains("Green", true) -> android.graphics.Color.parseColor("#10B981")
            name.contains("Gold", true) || name.contains("Amber", true) -> android.graphics.Color.parseColor("#F59E0B")
            name.contains("Purple", true) || name.contains("Violet", true) -> android.graphics.Color.parseColor("#8B5CF6")
            name.contains("Pink", true) -> android.graphics.Color.parseColor("#FF2A85")
            else -> android.graphics.Color.WHITE
        }
    }

    // ── Primary Requested Color System ───────────────────────────────────────
    val ObsidianBlack = Color(0xFF08090B)       // Primary Deep Black / Surface Background
    val PeriwinkleGrey = Color(0xFF8E95A3)      // Secondary Neutral Slate Gray (Clean Obsidian Dark Palette)
    val SoftOffWhite = Color(0xFFFFFFFF)        // Text / Highlights / Element Surface

    val DarkSlateDeep = Color(0xFF08090B)
    val DarkSlateCard = Color(0xFF181C24)
    val DarkSlateBorder = Color(0x334A5462)
    val MutedGrayDark = Color(0x664A5462)
    val MutedGraySoft = Color(0xFF8E95A3)
    val PureWhite = Color(0xFFFFFFFF)

    val PureBlack: Color
        get() = Color(0xFF08090B)
    val AppBackground: Color
        get() = Color(0xFF08090B)
    val SecondaryBackground: Color
        get() = Color(0xFF141820)
    val DarkGraphite: Color
        get() = Color(0xFF181C24)
    val CardSurface: Color
        get() = Color(0xFF181C24)
    val CardSurfaceElevated: Color
        get() = Color(0xFF222630)
    val BorderSubtle: Color
        get() = Color(0x334A5462)
    val Divider: Color
        get() = Color(0x224A5462)

    // ── Text Hierarchy ───────────────────────────────────────────────────────
    val PrimaryText: Color
        get() = Color(0xFFFFFFFF)
    val SecondaryText: Color
        get() = Color(0xFF8E95A3)
    val MutedText: Color
        get() = Color(0xFFA0A5AD)
    val TextPrimary: Color get() = PrimaryText
    val TextSecondary: Color get() = SecondaryText
    val TextMuted: Color get() = MutedText

    // ── Dynamic Accent Tokens ────────────────────────────────────────────────
    val NeonCrimson: Color
        get() = currentAccent.value
    val NeonCrimsonPressed: Color
        get() = currentAccent.value.copy(alpha = 0.8f)
    val NeonCrimsonGlow: Color
        get() = currentAccent.value.copy(alpha = 0.25f)
    val BackgroundOled: Color
        get() = if (isDarkState.value) Color(0xFF000000) else Color(0xFFF8F9FA)
    val GlassSurfaceDark: Color
        get() = if (isDarkState.value) Color(0xDD0C0C0C) else Color(0xEEFFFFFF)
    val GlassSurfaceLight: Color
        get() = if (isDarkState.value) Color(0x1AFFFFFF) else Color(0x1A000000)
    val GlassBorder: Color
        get() = if (isDarkState.value) Color(0x22FFFFFF) else Color(0x22000000)
    val AccentGold: Color
        get() = currentAccent.value
    val VerifiedGreen: Color
        get() = Color(0xFF10B981)

    // ── Brand Accent Aliases ─────────────────────────────────────────────────
    val PrimaryBrand: Color get() = currentAccent.value
    val PrimaryBrandGlow: Color get() = currentAccent.value.copy(alpha = 0.25f)
    val GoldenAmber: Color get() = currentAccent.value
    val GoldenAmberDark: Color get() = currentAccent.value.copy(alpha = 0.8f)
    val GoldenAmberGlow: Color get() = currentAccent.value.copy(alpha = 0.25f)
    val AmberLight: Color get() = currentAccent.value

    // ── Secondary Accents ────────────────────────────────────────────────────
    val CinematicRed: Color get() = currentAccent.value
    val CinematicRedGlow: Color get() = currentAccent.value.copy(alpha = 0.25f)
    val SubtlePurple: Color get() = currentAccent.value
    val SubtlePurpleGlow: Color get() = currentAccent.value.copy(alpha = 0.25f)
    val CoolBlue: Color get() = currentAccent.value
    val CoolBlueGlow: Color get() = currentAccent.value.copy(alpha = 0.25f)
    val EmeraldGreen: Color get() = Color(0xFF10B981)
    val WarmAmber: Color get() = Color(0xFFF59E0B)

    // ── Skeleton / Shimmer ───────────────────────────────────────────────────
    val SkeletonDark: Color
        get() = if (isDarkState.value) Color(0xFF141414) else Color(0xFFE2E8F0)
    val SkeletonShimmer: Color
        get() = if (isDarkState.value) Color(0xFF222222) else Color(0xFFCBD5E1)

    // ── Cinematic Accent Colors ──────────────────────────────────────────────
    val CinematicAccent: Color
        get() = if (isDarkState.value) Color(0xFFE0E0E0) else Color(0xFF334155)

    val GlowWhite: Color
        get() = currentAccent.value.copy(alpha = 0.25f)

    val FrostedGlass: Color
        get() = if (isDarkState.value) Color(0xCC0A0A0A) else Color(0xEEFFFFFF)

    val FrostedGlassBorder: Color
        get() = if (isDarkState.value) Color(0x33FFFFFF) else Color(0x33000000)

    val HeroOverlay: Color
        get() = Color(0x99000000)

    val ChipActive: Color
        get() = currentAccent.value

    val ChipInactive: Color
        get() = if (isDarkState.value) Color(0xFF1A1A1A) else Color(0xFFF1F5F9)

    val ChipBorder: Color
        get() = if (isDarkState.value) Color(0x33FFFFFF) else Color(0x33000000)

    val ProgressTrack: Color
        get() = if (isDarkState.value) Color(0x33FFFFFF) else Color(0x33000000)

    val ProgressFill: Color
        get() = currentAccent.value

    val RatingGold: Color
        get() = Color(0xFFFFD700)

    // ── Gradients ────────────────────────────────────────────────────────────
    val CrimsonGradient: Brush
        get() = Brush.horizontalGradient(
            colors = listOf(
                currentAccent.value,
                currentAccent.value.copy(alpha = 0.75f)
            )
        )

    val GoldenGradient: Brush
        get() = CrimsonGradient

    val HeroGradient: Brush
        get() = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color(0x66000000),
                Color(0xCC000000),
                BackgroundOled
            )
        )

    val TopBarGradient: Brush
        get() = Brush.verticalGradient(
            colors = listOf(
                if (isDarkState.value) Color(0xEE000000) else Color(0xEEF8F9FA),
                if (isDarkState.value) Color(0x99000000) else Color(0x99F8F9FA),
                Color.Transparent
            )
        )

    val SplashAuraGradient: Brush
        get() = Brush.radialGradient(
            colors = listOf(
                currentAccent.value.copy(alpha = 0.3f),
                currentAccent.value.copy(alpha = 0.1f),
                Color.Transparent
            )
        )

    val CinematicHeroGradient: Brush
        get() = Brush.verticalGradient(
            colorStops = arrayOf(
                0.00f to Color.Transparent,
                0.40f to Color(0x66000000),
                0.70f to Color(0xCC000000),
                0.90f to if (isDarkState.value) Color(0xF2000000) else Color(0xF2F8F9FA),
                1.00f to PureBlack
            )
        )

    val DetailBackdropGradient: Brush
        get() = Brush.verticalGradient(
            colorStops = arrayOf(
                0.00f to Color.Transparent,
                0.30f to Color(0x66000000),
                0.60f to Color(0xCC000000),
                0.85f to if (isDarkState.value) Color(0xF2000000) else Color(0xF2F8F9FA),
                1.00f to PureBlack
            )
        )

    val CardHoverGlow: Brush
        get() = Brush.radialGradient(
            colors = listOf(
                GlowWhite,
                Color.Transparent
            )
        )
}

// ─────────────────────────────────────────────────────────────────────────────
// Dynamic Color Schemes
// ─────────────────────────────────────────────────────────────────────────────
fun getVortexColorScheme(isDark: Boolean, accent: Color) = if (isDark) {
    darkColorScheme(
        primary = accent,
        onPrimary = if (accent == Color.White) Color.Black else Color.White,
        primaryContainer = Color(0xFF1C1C1C),
        onPrimaryContainer = Color.White,
        secondary = accent,
        onSecondary = Color.Black,
        secondaryContainer = Color(0xFF141414),
        onSecondaryContainer = Color.White,
        tertiary = Color(0xFF10B981),
        onTertiary = Color.Black,
        background = Color(0xFF000000),
        onBackground = Color.White,
        surface = Color(0xFF101010),
        onSurface = Color.White,
        surfaceVariant = Color(0xFF141414),
        onSurfaceVariant = Color(0xFFCCCCCC),
        outline = Color(0x22FFFFFF)
    )
} else {
    lightColorScheme(
        primary = accent,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFF1F5F9),
        onPrimaryContainer = Color(0xFF0F172A),
        secondary = accent,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFF1F5F9),
        onSecondaryContainer = Color(0xFF0F172A),
        tertiary = Color(0xFF10B981),
        onTertiary = Color.White,
        background = Color(0xFFF8F9FA),
        onBackground = Color(0xFF0F172A),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF0F172A),
        surfaceVariant = Color(0xFFF1F5F9),
        onSurfaceVariant = Color(0xFF475569),
        outline = Color(0x22000000)
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// ModernTypography — Manrope type scale
// ─────────────────────────────────────────────────────────────────────────────
val ModernTypography = Typography(
    displayLarge = TextStyle(fontFamily = ManropeFontFamily, fontWeight = FontWeight.Normal, fontSize = 57.sp),
    displayMedium = TextStyle(fontFamily = ManropeFontFamily, fontWeight = FontWeight.Normal, fontSize = 45.sp),
    displaySmall = TextStyle(fontFamily = ManropeFontFamily, fontWeight = FontWeight.Normal, fontSize = 36.sp),
    headlineLarge = TextStyle(fontFamily = ManropeFontFamily, fontWeight = FontWeight.Bold, fontSize = 32.sp),
    headlineMedium = TextStyle(fontFamily = ManropeFontFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp),
    headlineSmall = TextStyle(fontFamily = ManropeFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 24.sp),
    titleLarge = TextStyle(fontFamily = ManropeFontFamily, fontWeight = FontWeight.Bold, fontSize = 22.sp),
    titleMedium = TextStyle(fontFamily = ManropeFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    titleSmall = TextStyle(fontFamily = ManropeFontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp),
    bodyLarge = TextStyle(fontFamily = ManropeFontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = ManropeFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = ManropeFontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = ManropeFontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = ManropeFontFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp),
    labelSmall = TextStyle(fontFamily = ManropeFontFamily, fontWeight = FontWeight.Medium, fontSize = 11.sp)
)

// ─────────────────────────────────────────────────────────────────────────────
// MovieVortexTheme — App-level Compose theme
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun MovieVortexTheme(
    appTheme: String = "Dark (OLED Pure Black)",
    accentColor: String = "Pure White (Cinematic)",
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    SideEffect {
        VortexColors.updateTheme(themeName = appTheme, accentName = accentColor, systemInDark = systemInDark)
    }

    val isDark = VortexColors.isDarkState.value
    val accent = VortexColors.currentAccent.value

    MaterialTheme(
        colorScheme = getVortexColorScheme(isDark, accent),
        typography = ModernTypography,
        content = content
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Dynamic Top-level convenience aliases
// ─────────────────────────────────────────────────────────────────────────────
val TextPrimary: Color get() = VortexColors.PrimaryText
val TextSecondary: Color get() = VortexColors.SecondaryText
val TextMuted: Color get() = VortexColors.MutedText
val PrimaryText: Color get() = VortexColors.PrimaryText
val SecondaryText: Color get() = VortexColors.SecondaryText
val TextMutedAlias: Color get() = VortexColors.MutedText
val NeonCrimson: Color get() = VortexColors.NeonCrimson
val BackgroundOled: Color get() = VortexColors.BackgroundOled
val GlassSurfaceDark: Color get() = VortexColors.GlassSurfaceDark
val GlassSurfaceLight: Color get() = VortexColors.GlassSurfaceLight
val GlassBorder: Color get() = VortexColors.GlassBorder
val AccentGold: Color get() = VortexColors.AccentGold
val VerifiedGreen: Color get() = VortexColors.VerifiedGreen
val PureBlack: Color get() = VortexColors.PureBlack
