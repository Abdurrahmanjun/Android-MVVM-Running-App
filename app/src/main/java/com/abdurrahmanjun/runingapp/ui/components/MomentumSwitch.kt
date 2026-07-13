package com.abdurrahmanjun.runingapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.abdurrahmanjun.runingapp.ui.theme.MomentumColors

/** Momentum toggle: 46×28 pill, teal on / grey off, 22px white knob. */
@Composable
fun MomentumSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val track by animateColorAsState(
        if (checked) MomentumColors.Teal else Color(0xFFCBD6D2), label = "track",
    )
    val knobOffset by animateDpAsState(if (checked) 21.dp else 3.dp, label = "knob")
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .width(46.dp)
            .height(28.dp)
            .clip(CircleShape)
            .background(track)
            .clickable(interaction, null) { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .padding(start = knobOffset)
                .size(22.dp)
                .clip(CircleShape)
                .background(Color.White),
        )
    }
}
