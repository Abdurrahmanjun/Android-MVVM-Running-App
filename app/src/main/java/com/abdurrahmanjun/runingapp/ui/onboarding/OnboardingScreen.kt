package com.abdurrahmanjun.runingapp.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.abdurrahmanjun.runingapp.data.local.UserPreferences
import com.abdurrahmanjun.runingapp.ui.components.MintChip
import com.abdurrahmanjun.runingapp.ui.components.MintGradientButton
import com.abdurrahmanjun.runingapp.ui.components.SegmentedToggle
import com.abdurrahmanjun.runingapp.ui.theme.MomentumColors
import com.abdurrahmanjun.runingapp.ui.theme.MomentumDimens
import com.abdurrahmanjun.runingapp.ui.theme.MomentumShapes
import com.abdurrahmanjun.runingapp.ui.theme.MomentumType

/** Result of a successful onboarding submit. */
data class OnboardingResult(val name: String, val weightKg: Float, val units: String)

@Composable
fun OnboardingScreen(
    initialName: String = "",
    initialWeight: String = "",
    initialUnits: String = UserPreferences.UNITS_METRIC,
    onContinue: (OnboardingResult) -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }
    var weight by remember { mutableStateOf(initialWeight) }
    var metric by remember { mutableStateOf(initialUnits != UserPreferences.UNITS_IMPERIAL) }
    var nameError by remember { mutableStateOf(false) }
    var weightError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MomentumColors.Paper)
            // Soft mint blob decoration, top-right (radial gradient reads soft without a blur pass).
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(MomentumColors.Mint.copy(alpha = 0.55f), MomentumColors.Mint.copy(alpha = 0f)),
                        center = Offset(size.width * 0.92f, size.height * 0.12f),
                        radius = size.width * 0.42f,
                    ),
                    radius = size.width * 0.42f,
                    center = Offset(size.width * 0.92f, size.height * 0.12f),
                )
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(MomentumDimens.screenPadding),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Top block ---------------------------------------------------------
            Column(modifier = Modifier.padding(top = 8.dp)) {
                MintChip("Let's set you up")
                Spacer(Modifier.height(18.dp))
                androidx.compose.material3.Text(
                    text = "Welcome.\nLet's build your\nmomentum.",
                    style = MomentumType.pageHeadingLarge,
                    color = MomentumColors.Ink,
                )
                Spacer(Modifier.height(14.dp))
                androidx.compose.material3.Text(
                    text = "Your name and weight help us track calories and pace accurately from your very first run.",
                    style = MomentumType.body,
                    color = MomentumColors.Muted,
                    modifier = Modifier.fillMaxWidth(0.9f),
                )
            }

            // Bottom block ------------------------------------------------------
            Column {
                OnboardingField(
                    label = "YOUR NAME",
                    value = name,
                    onValueChange = { name = it; nameError = false },
                    placeholder = "e.g. Alex Rivera",
                    keyboardType = KeyboardType.Text,
                    isError = nameError,
                )
                Spacer(Modifier.height(12.dp))
                OnboardingField(
                    label = "YOUR WEIGHT",
                    value = weight,
                    onValueChange = { weight = it; weightError = false },
                    placeholder = "68",
                    keyboardType = KeyboardType.Decimal,
                    suffix = if (metric) "kg" else "lb",
                    isError = weightError,
                )
                Spacer(Modifier.height(14.dp))
                SegmentedToggle(
                    options = listOf("Metric · km", "Imperial · mi"),
                    selectedIndex = if (metric) 0 else 1,
                    onSelect = { metric = it == 0 },
                )
                Spacer(Modifier.height(18.dp))
                MintGradientButton(
                    text = "Continue",
                    onClick = {
                        val w = weight.trim().toFloatOrNull()
                        nameError = name.isBlank()
                        weightError = w == null || w <= 0f
                        if (!nameError && !weightError) {
                            onContinue(
                                OnboardingResult(
                                    name = name.trim(),
                                    weightKg = w!!,
                                    units = if (metric) UserPreferences.UNITS_METRIC else UserPreferences.UNITS_IMPERIAL,
                                )
                            )
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun OnboardingField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
    suffix: String? = null,
    isError: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MomentumShapes.field)
            .background(MomentumColors.Card)
            .border(
                width = if (isError) 1.5.dp else 1.dp,
                color = if (isError) MomentumColors.Destructive else MomentumColors.Line,
                shape = MomentumShapes.field,
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        androidx.compose.material3.Text(label, style = MomentumType.label, color = MomentumColors.Muted)
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    androidx.compose.material3.Text(
                        placeholder,
                        style = MomentumType.numberField,
                        color = MomentumColors.Placeholder,
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = MomentumType.numberField.copy(color = MomentumColors.Ink),
                    cursorBrush = SolidColor(MomentumColors.Teal),
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (suffix != null) {
                Spacer(Modifier.width(8.dp))
                androidx.compose.material3.Text(suffix, style = MomentumType.numberField, color = MomentumColors.Muted)
            }
        }
    }
}
