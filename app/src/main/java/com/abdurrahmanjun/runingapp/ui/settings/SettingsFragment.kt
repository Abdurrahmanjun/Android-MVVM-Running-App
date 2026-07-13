package com.abdurrahmanjun.runingapp.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.abdurrahmanjun.runingapp.R
import com.abdurrahmanjun.runingapp.data.local.UserPreferences
import com.abdurrahmanjun.runingapp.ui.theme.MomentumTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            MomentumTheme {
                SettingsScreen(
                    initial = SettingsValues(
                        name = userPreferences.name,
                        weightKg = userPreferences.weightKg,
                        units = userPreferences.units,
                        autoPause = userPreferences.autoPause,
                        voiceCoach = userPreferences.voiceCoach,
                        weeklyGoalKm = userPreferences.weeklyGoalKm,
                        darkMapAtNight = userPreferences.darkMapAtNight,
                    ),
                    onNameWeightChange = { n, w -> userPreferences.name = n; userPreferences.weightKg = w },
                    onUnitsChange = { userPreferences.units = it },
                    onAutoPauseChange = { userPreferences.autoPause = it },
                    onVoiceCoachChange = { userPreferences.voiceCoach = it },
                    onWeeklyGoalChange = { userPreferences.weeklyGoalKm = it },
                    onDarkMapChange = { userPreferences.darkMapAtNight = it },
                    onSignOut = ::signOut,
                )
            }
        }
    }

    private fun signOut() {
        userPreferences.isFirstAppOpen = true
        findNavController().navigate(
            R.id.setupFragment,
            null,
            navOptions { popUpTo(R.id.nav_graph) { inclusive = true } },
        )
    }
}
