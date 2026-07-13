package com.abdurrahmanjun.runingapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdurrahmanjun.runingapp.data.local.entity.RunEntity
import com.abdurrahmanjun.runingapp.ui.components.LimeButton
import com.abdurrahmanjun.runingapp.ui.theme.MomentumColors
import com.abdurrahmanjun.runingapp.ui.theme.MomentumDimens
import com.abdurrahmanjun.runingapp.ui.theme.MomentumShapes
import com.abdurrahmanjun.runingapp.ui.theme.MomentumType
import com.abdurrahmanjun.runingapp.utils.UnitFormatter

@Composable
fun HomeScreen(
    userName: String,
    isMetric: Boolean,
    goalKm: Float,
    runs: List<RunEntity>,
    now: Long,
    onStartRun: () -> Unit,
) {
    val fmt = remember(isMetric) { UnitFormatter(isMetric) }
    val summary = remember(runs, isMetric, goalKm) { HomeMapper.weeklySummary(runs, fmt, goalKm, now) }
    val items = remember(runs, isMetric) { runs.map { HomeMapper.toRunItem(it, fmt, now) } }
    val hour = remember(now) { java.util.Calendar.getInstance().apply { timeInMillis = now }.get(java.util.Calendar.HOUR_OF_DAY) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MomentumColors.Paper)
            .statusBarsPadding(),
        contentPadding = PaddingValues(
            start = MomentumDimens.screenPadding,
            end = MomentumDimens.screenPadding,
            top = 12.dp,
            bottom = 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { GreetingBar(HomeMapper.greeting(hour), userName.ifBlank { "Runner" }) }
        item { WeeklyHeroCard(summary) }
        item {
            LimeButton(
                text = "Start a run",
                onClick = onStartRun,
                trailingIcon = Icons.Filled.PlayArrow,
            )
        }
        item { RecentRunsHeader() }
        if (items.isEmpty()) {
            item { EmptyRunsCard() }
        } else {
            items(items, key = { it.id ?: it.hashCode() }) { RunRow(it) }
        }
    }
}

@Composable
private fun GreetingBar(greeting: String, name: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(greeting, style = MomentumType.bodySmall, color = MomentumColors.Muted)
            Spacer(Modifier.height(2.dp))
            Text(name, style = MomentumType.sectionHeading.copy(fontSize = 20.sp), color = MomentumColors.Ink)
        }
        Box(
            modifier = Modifier
                .size(MomentumDimens.avatar)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(listOf(MomentumColors.Mint, MomentumColors.Teal))),
            contentAlignment = Alignment.Center,
        ) {
            Text(HomeMapper.initials(name), style = MomentumType.label, color = MomentumColors.Card)
        }
    }
}

@Composable
private fun WeeklyHeroCard(summary: WeeklySummary) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MomentumShapes.card)
            .background(Brush.linearGradient(listOf(MomentumColors.Teal, MomentumColors.Ink)))
            .padding(MomentumDimens.cardPadding),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("THIS WEEK", style = MomentumType.label, color = MomentumColors.MintBright)
            Spacer(Modifier.weight(1f))
            summary.weekOverWeekPercent?.let { WowChip(it) }
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(summary.distanceValue, style = MomentumType.pageHeadingLarge, color = MomentumColors.Card)
            Spacer(Modifier.size(8.dp))
            Text(
                "${summary.distanceUnit} · ${summary.runCount} runs",
                style = MomentumType.bodyMedium,
                color = MomentumColors.Card.copy(alpha = 0.75f),
                modifier = Modifier.padding(bottom = 6.dp),
            )
        }
        Spacer(Modifier.height(12.dp))
        // Lime progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(MomentumColors.Card.copy(alpha = 0.15f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(summary.progress)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(MomentumColors.Lime),
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(summary.remainingText, style = MomentumType.bodySmall, color = MomentumColors.Card.copy(alpha = 0.7f))
    }
}

@Composable
private fun WowChip(percent: Int) {
    val up = percent >= 0
    Row(
        modifier = Modifier
            .clip(MomentumShapes.pill)
            .background(MomentumColors.Ink.copy(alpha = 0.45f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Text(
            "${if (up) "+" else ""}$percent%",
            style = MomentumType.label,
            color = MomentumColors.Lime,
        )
        Icon(
            if (up) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
            contentDescription = null,
            tint = MomentumColors.Lime,
            modifier = Modifier.size(11.dp),
        )
    }
}

@Composable
private fun RecentRunsHeader() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Recent runs", style = MomentumType.sectionHeading, color = MomentumColors.Ink)
        Spacer(Modifier.weight(1f))
        Text("See all", style = MomentumType.bodySmall, color = MomentumColors.Teal)
    }
}

@Composable
private fun RunRow(item: RunListItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MomentumShapes.card)
            .background(MomentumColors.Card)
            .border(1.dp, MomentumColors.Line, MomentumShapes.card)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(MomentumDimens.iconTileSize)
                .clip(MomentumShapes.iconTile)
                .background(MomentumColors.Teal.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.DirectionsRun, contentDescription = null, tint = MomentumColors.Teal, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, style = MomentumType.titleRow, color = MomentumColors.Ink)
            Spacer(Modifier.height(2.dp))
            Text(item.subtitle, style = MomentumType.bodySmall, color = MomentumColors.Muted)
        }
        Spacer(Modifier.size(8.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(item.distanceValue, style = MomentumType.metricMedium, color = MomentumColors.Ink)
            Text(" ${item.distanceUnit}", style = MomentumType.bodySmall, color = MomentumColors.Muted, modifier = Modifier.padding(bottom = 3.dp))
        }
    }
}

@Composable
private fun EmptyRunsCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MomentumShapes.card)
            .background(MomentumColors.Card)
            .border(1.dp, MomentumColors.Line, MomentumShapes.card)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(MomentumShapes.iconTile)
                .background(MomentumColors.Teal.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.DirectionsRun, contentDescription = null, tint = MomentumColors.Teal, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text("No runs yet", style = MomentumType.titleRow, color = MomentumColors.Ink)
        Spacer(Modifier.height(4.dp))
        Text(
            "Tap “Start a run” to log your first one and build your momentum.",
            style = MomentumType.bodySmall,
            color = MomentumColors.Muted,
            textAlign = TextAlign.Center,
        )
    }
}
