package com.abdurrahmanjun.runingapp.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.abdurrahmanjun.runingapp.data.local.UserPreferences
import com.abdurrahmanjun.runingapp.ui.components.MomentumSwitch
import com.abdurrahmanjun.runingapp.ui.components.SegmentedToggle
import com.abdurrahmanjun.runingapp.ui.home.HomeMapper
import com.abdurrahmanjun.runingapp.ui.theme.MomentumColors
import com.abdurrahmanjun.runingapp.ui.theme.MomentumDimens
import com.abdurrahmanjun.runingapp.ui.theme.MomentumShapes
import com.abdurrahmanjun.runingapp.ui.theme.MomentumType

/** Snapshot of persisted settings the screen edits. */
data class SettingsValues(
    val name: String,
    val weightKg: Float,
    val units: String,
    val autoPause: Boolean,
    val voiceCoach: Boolean,
    val weeklyGoalKm: Float,
    val darkMapAtNight: Boolean,
)

@Composable
fun SettingsScreen(
    initial: SettingsValues,
    onNameWeightChange: (String, Float) -> Unit,
    onUnitsChange: (String) -> Unit,
    onAutoPauseChange: (Boolean) -> Unit,
    onVoiceCoachChange: (Boolean) -> Unit,
    onWeeklyGoalChange: (Float) -> Unit,
    onDarkMapChange: (Boolean) -> Unit,
    onSignOut: () -> Unit,
) {
    var name by remember { mutableStateOf(initial.name) }
    var weight by remember { mutableStateOf(initial.weightKg) }
    var metric by remember { mutableStateOf(initial.units != UserPreferences.UNITS_IMPERIAL) }
    var autoPause by remember { mutableStateOf(initial.autoPause) }
    var voiceCoach by remember { mutableStateOf(initial.voiceCoach) }
    var goal by remember { mutableStateOf(initial.weeklyGoalKm) }
    var darkMap by remember { mutableStateOf(initial.darkMapAtNight) }

    var showProfileDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }

    val unitsLabel = if (metric) "Metric units" else "Imperial units"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MomentumColors.Paper)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MomentumDimens.screenPadding, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Profile & settings", style = MomentumType.pageHeading, color = MomentumColors.Ink)

        // Profile card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MomentumShapes.card)
                .background(MomentumColors.Card)
                .border(1.dp, MomentumColors.Line, MomentumShapes.card)
                .padding(MomentumDimens.cardPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.linearGradient(listOf(MomentumColors.Mint, MomentumColors.Teal))),
                contentAlignment = Alignment.Center,
            ) {
                Text(HomeMapper.initials(name), style = MomentumType.sectionHeading, color = MomentumColors.Card)
            }
            Spacer(Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name.ifBlank { "Runner" }, style = MomentumType.sectionHeading, color = MomentumColors.Ink)
                Spacer(Modifier.height(3.dp))
                Text(
                    "${weight.toInt()} kg · $unitsLabel",
                    style = MomentumType.bodySmall,
                    color = MomentumColors.Muted,
                )
            }
            IconCircleButton(Icons.Outlined.Edit, "Edit profile") { showProfileDialog = true }
        }

        // Settings card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MomentumShapes.card)
                .background(MomentumColors.Card)
                .border(1.dp, MomentumColors.Line, MomentumShapes.card)
                .padding(vertical = 6.dp),
        ) {
            SettingRow(Icons.Outlined.Straighten, "Units", null) {
                SegmentedToggle(
                    options = listOf("km", "mi"),
                    selectedIndex = if (metric) 0 else 1,
                    onSelect = {
                        metric = it == 0
                        onUnitsChange(if (metric) UserPreferences.UNITS_METRIC else UserPreferences.UNITS_IMPERIAL)
                    },
                )
            }
            RowDivider()
            SettingRow(Icons.Outlined.Schedule, "Auto-pause", "Pause when you stop moving") {
                MomentumSwitch(autoPause) { autoPause = it; onAutoPauseChange(it) }
            }
            RowDivider()
            SettingRow(Icons.Outlined.Notifications, "Voice coach", "Splits every 1 km") {
                MomentumSwitch(voiceCoach) { voiceCoach = it; onVoiceCoachChange(it) }
            }
            RowDivider()
            SettingRow(Icons.Outlined.Flag, "Weekly goal", "${goal.toInt()} km per week") {
                Text(
                    "Edit",
                    style = MomentumType.titleRow,
                    color = MomentumColors.Teal,
                    modifier = Modifier
                        .clip(MomentumShapes.pill)
                        .clickable(remember { MutableInteractionSource() }, null) { showGoalDialog = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
            RowDivider()
            SettingRow(Icons.Outlined.DarkMode, "Dark map at night", "Auto theme for live runs") {
                MomentumSwitch(darkMap) { darkMap = it; onDarkMapChange(it) }
            }
        }

        // Ghost sign-out
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MomentumShapes.button)
                .background(MomentumColors.Card)
                .border(1.dp, MomentumColors.Line, MomentumShapes.button)
                .clickable(remember { MutableInteractionSource() }, null, onClick = onSignOut)
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("Sign out", style = MomentumType.button, color = MomentumColors.Destructive)
        }
    }

    if (showProfileDialog) {
        EditProfileDialog(
            initialName = name,
            initialWeight = weight,
            onDismiss = { showProfileDialog = false },
            onSave = { n, w -> name = n; weight = w; onNameWeightChange(n, w); showProfileDialog = false },
        )
    }
    if (showGoalDialog) {
        EditGoalDialog(
            initialGoal = goal,
            onDismiss = { showGoalDialog = false },
            onSave = { g -> goal = g; onWeeklyGoalChange(g); showGoalDialog = false },
        )
    }
}

@Composable
private fun SettingRow(icon: ImageVector, title: String, subtitle: String?, trailing: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(MomentumDimens.iconTileSize)
                .clip(MomentumShapes.iconTile)
                .background(MomentumColors.Teal.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = MomentumColors.Teal, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MomentumType.titleRow, color = MomentumColors.Ink)
            if (subtitle != null) {
                Spacer(Modifier.height(2.dp))
                Text(subtitle, style = MomentumType.bodySmall, color = MomentumColors.Muted)
            }
        }
        Spacer(Modifier.size(10.dp))
        trailing()
    }
}

@Composable
private fun RowDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 66.dp, end = 14.dp)
            .height(1.dp)
            .background(MomentumColors.Line),
    )
}

@Composable
private fun IconCircleButton(icon: ImageVector, desc: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(MomentumDimens.iconButton)
            .clip(RoundedCornerShape(12.dp))
            .background(MomentumColors.Paper)
            .border(1.dp, MomentumColors.Line, RoundedCornerShape(12.dp))
            .clickable(remember { MutableInteractionSource() }, null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, desc, tint = MomentumColors.Ink, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun EditProfileDialog(
    initialName: String,
    initialWeight: Float,
    onDismiss: () -> Unit,
    onSave: (String, Float) -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }
    var weight by remember { mutableStateOf(if (initialWeight > 0) initialWeight.toInt().toString() else "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit profile", style = MomentumType.sectionHeading) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("Name") }, singleLine = true)
                OutlinedTextField(
                    weight, { weight = it }, label = { Text("Weight (kg)") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val w = weight.trim().toFloatOrNull()
                if (name.isNotBlank() && w != null && w > 0) onSave(name.trim(), w)
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun EditGoalDialog(
    initialGoal: Float,
    onDismiss: () -> Unit,
    onSave: (Float) -> Unit,
) {
    var goal by remember { mutableStateOf(initialGoal.toInt().toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Weekly goal", style = MomentumType.sectionHeading) },
        text = {
            OutlinedTextField(
                goal, { goal = it }, label = { Text("Kilometres per week") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        },
        confirmButton = {
            TextButton(onClick = {
                val g = goal.trim().toFloatOrNull()
                if (g != null && g > 0) onSave(g)
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
