package com.abdurrahmanjun.runingapp.ui.tracking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdurrahmanjun.runingapp.ui.theme.MomentumColors
import com.abdurrahmanjun.runingapp.ui.theme.MomentumType

/**
 * Day / night palette for the live-run overlay. Night = dark neon sheet;
 * Day = light paper sheet. Chosen by the "Dark map at night" auto-switch.
 */
data class LiveRunColors(
    val surface: Color,
    val border: Color,
    val onSurface: Color,
    val muted: Color,
    val metric: Color,
    val controlBg: Color,
    val controlIcon: Color,
    val accent: Color,   // recenter icon + GPS dot
    val elevated: Boolean, // add a lift shadow (day, over a light map)
) {
    companion object {
        val Night = LiveRunColors(
            surface = Color(0xF20E1C19),
            border = Color(0x24FFFFFF),
            onSurface = Color.White,
            muted = Color(0xFF8AA39B),
            metric = MomentumColors.MintBright,
            controlBg = Color(0x1FFFFFFF),
            controlIcon = Color.White,
            accent = MomentumColors.MintBright,
            elevated = false,
        )
        val Day = LiveRunColors(
            surface = MomentumColors.Card,
            border = MomentumColors.Line,
            onSurface = MomentumColors.Ink,
            muted = MomentumColors.Muted,
            metric = MomentumColors.Teal,
            controlBg = MomentumColors.Paper,
            controlIcon = MomentumColors.Ink,
            accent = MomentumColors.Teal,
            elevated = true,
        )
    }
}

/** Top overlay: "GPS strong" chip (centered) + recenter button (end). */
@Composable
fun LiveRunTopBar(gpsText: String, colors: LiveRunColors, onRecenter: () -> Unit) {
    val chipShape = RoundedCornerShape(99.dp)
    val btnShape = RoundedCornerShape(12.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .then(if (colors.elevated) Modifier.shadow(8.dp, chipShape) else Modifier)
                .clip(chipShape)
                .background(colors.surface)
                .border(BorderStroke(1.dp, colors.border), chipShape)
                .padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(Modifier.size(7.dp).clip(CircleShape).background(colors.accent))
            Text(gpsText, style = MomentumType.label, color = colors.onSurface)
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .then(if (colors.elevated) Modifier.shadow(8.dp, btnShape) else Modifier)
                .size(38.dp)
                .clip(btnShape)
                .background(colors.surface)
                .border(BorderStroke(1.dp, colors.border), btnShape)
                .clickable(remember { MutableInteractionSource() }, null, onClick = onRecenter),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.MyLocation, "Recenter", tint = colors.accent, modifier = Modifier.size(18.dp))
        }
    }
}

/** Live-metric snapshot fed from TrackingService observers. */
data class LiveRunState(
    val elapsed: String,
    val distanceValue: String,
    val distanceUnit: String,
    val pace: String,
    val paceUnit: String,
    val calories: String,
    val isTracking: Boolean,
)

/** Bottom floating sheet: elapsed label, 58px timer, 3 metrics, pause/stop/lap controls. */
@Composable
fun LiveRunSheet(
    state: LiveRunState,
    colors: LiveRunColors,
    onPauseResume: () -> Unit,
    onStop: () -> Unit,
    onLap: () -> Unit,
) {
    val sheetShape = RoundedCornerShape(28.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding()
            .then(if (colors.elevated) Modifier.shadow(20.dp, sheetShape) else Modifier)
            .clip(sheetShape)
            .background(colors.surface)
            .border(BorderStroke(1.dp, colors.border), sheetShape)
            .padding(horizontal = 22.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Outlined.Timer, null, tint = colors.muted, modifier = Modifier.size(13.dp))
            Text("ELAPSED TIME", style = MomentumType.label, color = colors.muted)
        }
        Spacer(Modifier.size(4.dp))
        Text(state.elapsed, style = MomentumType.timer, color = colors.onSurface)
        Spacer(Modifier.size(18.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Metric(state.distanceValue, state.distanceUnit.uppercase(), colors)
            Metric(state.pace, "PACE ${state.paceUnit}", colors)
            Metric(state.calories, "KCAL", colors)
        }
        Spacer(Modifier.size(22.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleControl(
                icon = if (state.isTracking) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                desc = if (state.isTracking) "Pause" else "Resume",
                colors = colors,
                onClick = onPauseResume,
            )
            StopControl(onStop)
            CircleControl(icon = Icons.Outlined.Timer, desc = "Lap", colors = colors, onClick = onLap)
        }
    }
}

@Composable
private fun Metric(value: String, label: String, colors: LiveRunColors) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MomentumType.metricMedium, color = colors.metric)
        Spacer(Modifier.size(4.dp))
        Text(label, style = MomentumType.label.copy(fontSize = 10.sp), color = colors.muted, textAlign = TextAlign.Center)
    }
}

@Composable
private fun CircleControl(icon: ImageVector, desc: String, colors: LiveRunColors, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(62.dp)
            .clip(CircleShape)
            .background(colors.controlBg)
            .clickable(remember { MutableInteractionSource() }, null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, desc, tint = colors.controlIcon, modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun StopControl(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(82.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(MomentumColors.Lime)
            .clickable(remember { MutableInteractionSource() }, null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Filled.Stop, "Stop run", tint = MomentumColors.Ink, modifier = Modifier.size(30.dp))
    }
}
