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
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdurrahmanjun.runingapp.ui.theme.MomentumColors
import com.abdurrahmanjun.runingapp.ui.theme.MomentumType

// Dark sheet surface tokens (design: #0E1C19 @ 95% + 1px white @ 14%).
private val SheetSurface = Color(0xF20E1C19)
private val SheetBorder = Color(0x24FFFFFF)
private val ControlSurface = Color(0x1FFFFFFF)
private val OnDarkMuted = Color(0xFF8AA39B)

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

/** Top overlay: "GPS strong" chip (centered) + recenter button (end). */
@Composable
fun LiveRunTopBar(gpsText: String, onRecenter: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .clip(RoundedCornerShape(99.dp))
                .background(SheetSurface)
                .border(BorderStroke(1.dp, SheetBorder), RoundedCornerShape(99.dp))
                .padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(Modifier.size(7.dp).clip(CircleShape).background(MomentumColors.MintBright))
            Text(gpsText, style = MomentumType.label, color = Color.White)
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SheetSurface)
                .border(BorderStroke(1.dp, SheetBorder), RoundedCornerShape(12.dp))
                .clickable(remember { MutableInteractionSource() }, null, onClick = onRecenter),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.MyLocation, "Recenter", tint = MomentumColors.MintBright, modifier = Modifier.size(18.dp))
        }
    }
}

/** Bottom floating sheet: elapsed label, 58px timer, 3 metrics, pause/stop/lap controls. */
@Composable
fun LiveRunSheet(
    state: LiveRunState,
    onPauseResume: () -> Unit,
    onStop: () -> Unit,
    onLap: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding()
            .clip(RoundedCornerShape(28.dp))
            .background(SheetSurface)
            .border(BorderStroke(1.dp, SheetBorder), RoundedCornerShape(28.dp))
            .padding(horizontal = 22.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Outlined.Timer, null, tint = OnDarkMuted, modifier = Modifier.size(13.dp))
            Text("ELAPSED TIME", style = MomentumType.label, color = OnDarkMuted)
        }
        Spacer(Modifier.height(4.dp))
        Text(state.elapsed, style = MomentumType.timer, color = Color.White)
        Spacer(Modifier.height(18.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Metric(state.distanceValue, state.distanceUnit.uppercase())
            Metric(state.pace, "PACE ${state.paceUnit}")
            Metric(state.calories, "KCAL")
        }
        Spacer(Modifier.height(22.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleControl(
                icon = if (state.isTracking) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                desc = if (state.isTracking) "Pause" else "Resume",
                onClick = onPauseResume,
            )
            StopControl(onStop)
            CircleControl(icon = Icons.Outlined.Timer, desc = "Lap", onClick = onLap)
        }
    }
}

@Composable
private fun Metric(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MomentumType.metricMedium, color = MomentumColors.MintBright)
        Spacer(Modifier.height(4.dp))
        Text(label, style = MomentumType.label.copy(fontSize = 10.sp), color = OnDarkMuted, textAlign = TextAlign.Center)
    }
}

@Composable
private fun CircleControl(icon: ImageVector, desc: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(62.dp)
            .clip(CircleShape)
            .background(ControlSurface)
            .clickable(remember { MutableInteractionSource() }, null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, desc, tint = Color.White, modifier = Modifier.size(24.dp))
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
