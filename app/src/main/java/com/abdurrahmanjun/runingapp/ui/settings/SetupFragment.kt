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
import com.abdurrahmanjun.runingapp.ui.onboarding.OnboardingScreen
import com.abdurrahmanjun.runingapp.ui.theme.MomentumTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment() {

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Skip setup once the profile has been entered before.
        if (!userPreferences.isFirstAppOpen) {
            navigateToRun()
            return View(requireContext())
        }
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MomentumTheme {
                    OnboardingScreen(
                        initialName = userPreferences.name,
                        initialUnits = userPreferences.units,
                        onContinue = { result ->
                            userPreferences.name = result.name
                            userPreferences.weightKg = result.weightKg
                            userPreferences.units = result.units
                            userPreferences.isFirstAppOpen = false
                            navigateToRun()
                        },
                    )
                }
            }
        }
    }

    private fun navigateToRun() {
        findNavController().navigate(
            R.id.action_setupFragment_to_runFragment,
            null,
            navOptions { popUpTo(R.id.setupFragment) { inclusive = true } },
        )
    }
}
