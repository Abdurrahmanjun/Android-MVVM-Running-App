package com.abdurrahmanjun.runingapp.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Momentum design tokens (authoritative colours from the design handoff).
 * Centralised here so no screen hard-codes a hex value.
 */
object MomentumColors {
    val Ink = Color(0xFF08312C)        // primary text / dark surfaces
    val Teal = Color(0xFF0F8C7E)       // primary brand
    val Mint = Color(0xFF54D6BA)       // accent / gradient start
    val MintBright = Color(0xFF5FE0C0) // accent / gradient end, route glow
    val Lime = Color(0xFFD6F24E)       // energy pop — run CTA & records only
    val Paper = Color(0xFFF4F7F5)      // app background
    val Card = Color(0xFFFFFFFF)       // card surface
    val Muted = Color(0xFF6E827D)      // secondary text
    val Line = Color(0xFFE7EDEB)       // hairline / borders
    val Placeholder = Color(0xFFA9BBB6)
    val Destructive = Color(0xFFC0392B)
    val DarkMap = Color(0xFF0A1512)    // dark map background
    val OnLime = Ink                   // text sitting on a lime surface
}
