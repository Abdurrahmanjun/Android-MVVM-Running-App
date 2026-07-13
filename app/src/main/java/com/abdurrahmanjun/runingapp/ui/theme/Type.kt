package com.abdurrahmanjun.runingapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.abdurrahmanjun.runingapp.R

/** Display + all numerals. Tabular figures via the "tnum" feature. */
val SpaceGrotesk = FontFamily(
    Font(R.font.space_grotesk_medium, FontWeight.Medium),
    Font(R.font.space_grotesk_semibold, FontWeight.SemiBold),
    Font(R.font.space_grotesk_bold, FontWeight.Bold),
)

/** Body / secondary copy. */
val Manrope = FontFamily(
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_medium, FontWeight.Medium),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
    Font(R.font.manrope_bold, FontWeight.Bold),
)

/** Force tabular figures wherever numbers appear. */
private const val TNUM = "tnum"

/**
 * The Momentum type scale. Named styles used directly by screens; also folded
 * into a Material3 [Typography] so stray Material components inherit the fonts.
 */
object MomentumType {
    // Space Grotesk — display / headings / numerals
    val pageHeading = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold,
        fontSize = 30.sp, lineHeight = 34.sp, letterSpacing = (-0.02).em,
    )
    val pageHeadingLarge = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold,
        fontSize = 40.sp, lineHeight = 44.sp, letterSpacing = (-0.02).em,
    )
    val sectionHeading = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold,
        fontSize = 18.sp, lineHeight = 22.sp, letterSpacing = (-0.01).em,
    )
    val timer = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold,
        fontSize = 58.sp, lineHeight = 60.sp, letterSpacing = (-0.02).em,
        fontFeatureSettings = TNUM,
    )
    val metricLarge = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold,
        fontSize = 34.sp, lineHeight = 38.sp, letterSpacing = (-0.02).em,
        fontFeatureSettings = TNUM,
    )
    val metricMedium = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold,
        fontSize = 22.sp, lineHeight = 26.sp, letterSpacing = (-0.01).em,
        fontFeatureSettings = TNUM,
    )
    val numberField = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.Medium,
        fontSize = 20.sp, lineHeight = 24.sp, fontFeatureSettings = TNUM,
    )
    val label = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.12.em,
    )
    val button = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 20.sp, letterSpacing = 0.01.em,
    )

    // Manrope — body / secondary
    val body = TextStyle(
        fontFamily = Manrope, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp,
    )
    val bodyMedium = TextStyle(
        fontFamily = Manrope, fontWeight = FontWeight.Medium,
        fontSize = 14.sp, lineHeight = 20.sp,
    )
    val bodySmall = TextStyle(
        fontFamily = Manrope, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp,
    )
    val titleRow = TextStyle(
        fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp, lineHeight = 19.sp,
    )
}

val MomentumTypography = Typography(
    displayLarge = MomentumType.pageHeadingLarge,
    headlineMedium = MomentumType.pageHeading,
    titleLarge = MomentumType.sectionHeading,
    titleMedium = MomentumType.titleRow,
    labelLarge = MomentumType.button,
    labelSmall = MomentumType.label,
    bodyLarge = MomentumType.body,
    bodyMedium = MomentumType.bodyMedium,
    bodySmall = MomentumType.bodySmall,
)
