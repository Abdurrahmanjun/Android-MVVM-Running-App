package com.abdurrahmanjun.runingapp.ui.statistic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.abdurrahmanjun.runingapp.data.local.entity.RunEntity
import com.abdurrahmanjun.runingapp.ui.components.SegmentedToggle
import com.abdurrahmanjun.runingapp.ui.theme.MomentumColors
import com.abdurrahmanjun.runingapp.ui.theme.MomentumDimens
import com.abdurrahmanjun.runingapp.ui.theme.MomentumShapes
import com.abdurrahmanjun.runingapp.ui.theme.MomentumType
import com.abdurrahmanjun.runingapp.utils.UnitFormatter

@Composable
fun StatsScreen(
    isMetric: Boolean,
    runs: List<RunEntity>,
    now: Long,
) {
    var period by remember { mutableStateOf(StatsPeriod.WEEK) }
    val fmt = remember(isMetric) { UnitFormatter(isMetric) }
    val ui = remember(runs, isMetric, period) { StatsMapper.build(runs, fmt, period, now) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MomentumColors.Paper)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MomentumDimens.screenPadding, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Your stats", style = MomentumType.pageHeading, color = MomentumColors.Ink)
            Spacer(Modifier.weight(1f))
            SegmentedToggle(
                options = listOf("Week", "Month"),
                selectedIndex = if (period == StatsPeriod.WEEK) 0 else 1,
                onSelect = { period = if (it == 0) StatsPeriod.WEEK else StatsPeriod.MONTH },
            )
        }

        // 2x2 metric grid
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            MetricCard(ui.cards[0], Modifier.weight(1f))
            MetricCard(ui.cards[1], Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            MetricCard(ui.cards[2], Modifier.weight(1f))
            MetricCard(ui.cards[3], Modifier.weight(1f))
        }

        WeeklyChartCard(ui)

        ui.personalBest?.let { PersonalBestCard(it) }
    }
}

@Composable
private fun MetricCard(card: StatCard, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(MomentumShapes.card)
            .background(MomentumColors.Card)
            .border(1.dp, MomentumColors.Line, MomentumShapes.card)
            .padding(MomentumDimens.cardPadding),
    ) {
        Text(card.label.uppercase(), style = MomentumType.label, color = MomentumColors.Muted)
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(card.value, style = MomentumType.metricLarge, color = MomentumColors.Ink)
            Text(" ${card.unit}", style = MomentumType.bodySmall, color = MomentumColors.Muted, modifier = Modifier.padding(bottom = 4.dp))
        }
    }
}

@Composable
private fun WeeklyChartCard(ui: StatsUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MomentumShapes.card)
            .background(MomentumColors.Card)
            .border(1.dp, MomentumColors.Line, MomentumShapes.card)
            .padding(MomentumDimens.cardPadding),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Distance this week", style = MomentumType.sectionHeading, color = MomentumColors.Ink)
            Spacer(Modifier.weight(1f))
            Text(ui.weekTotalLabel, style = MomentumType.titleRow, color = MomentumColors.Teal)
        }
        Spacer(Modifier.height(18.dp))
        if (!ui.hasChartData) {
            ChartEmptyState()
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                ui.bars.forEach { bar -> Bar(bar, Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun Bar(bar: WeeklyBar, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Box(modifier = Modifier.height(120.dp), contentAlignment = Alignment.BottomCenter) {
            // Minimum stub height so empty days still read as a bar.
            val frac = (bar.fraction).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeightFraction(if (frac == 0f) 0.04f else frac)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .background(
                        if (bar.isPeak) Brush.verticalGradient(listOf(MomentumColors.Lime, MomentumColors.Lime))
                        else Brush.verticalGradient(listOf(MomentumColors.Mint, MomentumColors.Teal))
                    ),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(bar.dayLabel, style = MomentumType.label, color = MomentumColors.Muted)
    }
}

// Applies a height fraction inside a fixed-height parent Box.
private fun Modifier.fillMaxHeightFraction(fraction: Float): Modifier =
    this.then(Modifier.fillMaxHeight(fraction))

@Composable
private fun ChartEmptyState() {
    Column(
        modifier = Modifier.fillMaxWidth().height(150.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(Icons.Outlined.BarChart, null, tint = MomentumColors.Placeholder, modifier = Modifier.size(30.dp))
        Spacer(Modifier.height(8.dp))
        Text("No runs in this period yet", style = MomentumType.bodyMedium, color = MomentumColors.Muted)
        Spacer(Modifier.height(2.dp))
        Text("Your weekly distance will appear here.", style = MomentumType.bodySmall, color = MomentumColors.Placeholder, textAlign = TextAlign.Center)
    }
}

@Composable
private fun PersonalBestCard(pb: PersonalBest) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MomentumShapes.card)
            .background(MomentumColors.Lime.copy(alpha = 0.16f))
            .border(1.dp, MomentumColors.Lime.copy(alpha = 0.45f), MomentumShapes.card)
            .padding(MomentumDimens.cardPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(MomentumDimens.iconTileSize)
                .clip(MomentumShapes.iconTile)
                .background(MomentumColors.Lime),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.EmojiEvents, null, tint = MomentumColors.Ink, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.size(12.dp))
        Column {
            Text(pb.title, style = MomentumType.titleRow, color = MomentumColors.Ink)
            Spacer(Modifier.height(2.dp))
            Text(pb.detail, style = MomentumType.bodySmall, color = MomentumColors.Muted)
        }
    }
}
