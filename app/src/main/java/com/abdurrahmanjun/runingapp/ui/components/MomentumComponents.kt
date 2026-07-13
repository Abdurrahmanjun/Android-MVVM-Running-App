package com.abdurrahmanjun.runingapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.abdurrahmanjun.runingapp.ui.theme.MomentumColors
import com.abdurrahmanjun.runingapp.ui.theme.MomentumShapes
import com.abdurrahmanjun.runingapp.ui.theme.MomentumType

/** Small pale-mint pill used for eyebrow labels ("Let's set you up"). */
@Composable
fun MintChip(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(MomentumShapes.pill)
            .background(MomentumColors.MintBright.copy(alpha = 0.22f))
            .padding(horizontal = 12.dp, vertical = 7.dp),
    ) {
        Text(text = text, style = MomentumType.label, color = MomentumColors.Teal)
    }
}

/** Full-width mint-gradient primary button (Continue) with a trailing arrow. */
@Composable
fun MintGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = Icons.AutoMirrored.Filled.ArrowForward,
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .shadow(18.dp, MomentumShapes.button, spotColor = MomentumColors.Teal, ambientColor = MomentumColors.Teal)
            .clip(MomentumShapes.button)
            .background(
                Brush.horizontalGradient(listOf(MomentumColors.Mint, MomentumColors.MintBright))
            )
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = text, style = MomentumType.button, color = MomentumColors.Ink)
            if (trailingIcon != null) {
                Icon(trailingIcon, contentDescription = null, tint = MomentumColors.Ink, modifier = Modifier.size(18.dp))
            }
        }
    }
}

/** Full-width lime CTA (Start a run). Lime is reserved for this + records only. */
@Composable
fun LimeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = null,
    height: Int = 60,
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(MomentumShapes.button)
            .background(MomentumColors.Lime)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = text, style = MomentumType.button, color = MomentumColors.Ink)
            if (trailingIcon != null) {
                Icon(trailingIcon, contentDescription = null, tint = MomentumColors.Ink, modifier = Modifier.size(16.dp))
            }
        }
    }
}

/** Two-segment pill toggle (Metric·km / Imperial·mi, Week / Month). Active = ink pill. */
@Composable
fun SegmentedToggle(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(MomentumShapes.pill)
            .background(MomentumColors.Paper)
            .border(BorderStroke(1.dp, MomentumColors.Line), MomentumShapes.pill)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            val interaction = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .clip(MomentumShapes.pill)
                    .background(if (selected) MomentumColors.Ink else Color.Transparent)
                    .clickable(interactionSource = interaction, indication = null) { onSelect(index) }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = MomentumType.label,
                    color = if (selected) MomentumColors.Card else MomentumColors.Muted,
                )
            }
        }
    }
}
