package com.abdurrahmanjun.runingapp.ui.run

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.abdurrahmanjun.runingapp.R
import com.abdurrahmanjun.runingapp.data.local.UserPreferences
import com.abdurrahmanjun.runingapp.ui.MainViewModel
import com.abdurrahmanjun.runingapp.ui.home.HomeScreen
import com.abdurrahmanjun.runingapp.ui.theme.MomentumTheme
import com.abdurrahmanjun.runingapp.utils.TrackingUtility
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RunFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var userPreferences: UserPreferences

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            val allGranted = results.values.all { it }
            if (!allGranted && !shouldShowLocationRationale()) {
                showAppSettingsSnackbar()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            MomentumTheme {
                val runs by viewModel.mainRepository.getAllRunsSortedByDate().observeAsState(emptyList())
                HomeScreen(
                    userName = userPreferences.name,
                    isMetric = userPreferences.isMetric,
                    goalKm = userPreferences.weeklyGoalKm,
                    runs = runs,
                    now = System.currentTimeMillis(),
                    onStartRun = {
                        findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
                    },
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissionsIfNeeded()
    }

    private fun requestPermissionsIfNeeded() {
        if (TrackingUtility.hasLocationPermissions(requireContext())) {
            return
        }
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionLauncher.launch(permissions.toTypedArray())
    }

    private fun shouldShowLocationRationale() =
        ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    private fun showAppSettingsSnackbar() {
        Snackbar.make(
            requireView(),
            "Location permission is required to track your runs.",
            Snackbar.LENGTH_LONG
        ).setAction("Settings") {
            startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireContext().packageName, null)
                }
            )
        }.show()
    }
}
