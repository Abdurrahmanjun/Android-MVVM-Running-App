package com.abdurrahmanjun.runingapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.abdurrahmanjun.runingapp.R
import com.abdurrahmanjun.runingapp.data.local.dao.RunDAO
import com.abdurrahmanjun.runingapp.databinding.ActivityMainBinding
import com.abdurrahmanjun.runingapp.ui.components.MomentumBottomBar
import com.abdurrahmanjun.runingapp.ui.theme.MomentumTheme
import com.abdurrahmanjun.runingapp.utils.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var runDAO: RunDAO

    private lateinit var binding: ActivityMainBinding

    // Destinations that show the bottom navigation bar.
    private val topLevelDestinations = setOf(
        R.id.runFragment, R.id.statisticFragment, R.id.settingsFragment,
    )

    private val navController: NavController
        get() = (supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment)
            .navController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigateToTrackingFragmentIfNeeded(intent)

        // Compose bottom bar driving the existing NavController (graph unchanged).
        var selectedDestId by mutableIntStateOf(R.id.runFragment)
        var barVisible by mutableStateOf(true)
        binding.bottomBar.setContent {
            MomentumTheme {
                if (barVisible) {
                    MomentumBottomBar(
                        selectedDestId = selectedDestId,
                        onSelect = { destId -> if (destId != selectedDestId) navigateTab(destId) },
                    )
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in topLevelDestinations) {
                selectedDestId = destination.id
                barVisible = true
            } else {
                barVisible = false
            }
        }
    }

    /** Switch top-level tabs, mirroring NavigationUI single-top / restore behaviour. */
    private fun navigateTab(destId: Int) {
        val options = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setRestoreState(true)
            .setPopUpTo(navController.graph.startDestinationId, inclusive = false, saveState = true)
            .build()
        navController.navigate(destId, null, options)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            navController.navigate(R.id.action_global_to_trackingFragment)
        }
    }
}
