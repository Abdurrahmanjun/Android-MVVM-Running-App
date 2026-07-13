package com.abdurrahmanjun.runingapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.abdurrahmanjun.runingapp.R
import com.abdurrahmanjun.runingapp.ui.theme.MomentumColors
import com.abdurrahmanjun.runingapp.ui.theme.MomentumType

private data class NavTab(val destId: Int, val label: String, val icon: ImageVector)

private val tabs = listOf(
    NavTab(R.id.runFragment, "Run", Icons.Outlined.DirectionsRun),
    NavTab(R.id.statisticFragment, "Stats", Icons.Outlined.ShowChart),
    NavTab(R.id.settingsFragment, "Settings", Icons.Outlined.Settings),
)

/**
 * Momentum bottom navigation. Drives the existing [androidx.navigation.NavController]
 * (routes/graph unchanged) — only the widget is Compose. Active tab = teal icon +
 * label with a teal dot indicator; inactive = muted.
 */
@Composable
fun MomentumBottomBar(
    selectedDestId: Int,
    onSelect: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MomentumColors.Card),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MomentumColors.Line),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(78.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tabs.forEach { tab ->
                BottomBarItem(
                    tab = tab,
                    selected = tab.destId == selectedDestId,
                    onClick = { onSelect(tab.destId) },
                )
            }
        }
    }
}

@Composable
private fun BottomBarItem(tab: NavTab, selected: Boolean, onClick: () -> Unit) {
    val tint = if (selected) MomentumColors.Teal else MomentumColors.Muted
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = Modifier
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = tab.icon,
            contentDescription = tab.label,
            tint = tint,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = tab.label,
            style = MomentumType.label.copy(fontSize = 10.sp, letterSpacing = 0.04.em),
            color = tint,
            textAlign = TextAlign.Center,
        )
        Box(
            modifier = Modifier
                .size(4.dp)
                .background(
                    color = if (selected) MomentumColors.Teal else Color.Transparent,
                    shape = CircleShape,
                ),
        )
    }
}
