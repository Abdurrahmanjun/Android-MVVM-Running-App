package com.abdurrahmanjun.runingapp.ui.tracking

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.abdurrahmanjun.runingapp.R
import com.abdurrahmanjun.runingapp.data.local.UserPreferences
import com.abdurrahmanjun.runingapp.data.local.entity.RunEntity
import com.abdurrahmanjun.runingapp.databinding.FragmentTrackingBinding
import com.abdurrahmanjun.runingapp.ui.theme.MomentumColors
import com.abdurrahmanjun.runingapp.ui.theme.MomentumTheme
import com.abdurrahmanjun.runingapp.utils.Constants.ACTION_PAUSE_SERVICE
import com.abdurrahmanjun.runingapp.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.abdurrahmanjun.runingapp.utils.Constants.ACTION_STOP_SERVICE
import com.abdurrahmanjun.runingapp.utils.Constants.MAP_ZOOM
import com.abdurrahmanjun.runingapp.utils.Constants.POLYLINE_COLOR
import com.abdurrahmanjun.runingapp.utils.Constants.POLYLINE_GLOW_COLOR
import com.abdurrahmanjun.runingapp.utils.Constants.POLYLINE_GLOW_WIDTH
import com.abdurrahmanjun.runingapp.utils.Constants.POLYLINE_WIDTH
import com.abdurrahmanjun.runingapp.utils.TrackingUtility
import com.abdurrahmanjun.runingapp.utils.UnitFormatter
import com.abdurrahmanjun.runingapp.services.Polyline
import com.abdurrahmanjun.runingapp.services.TrackingService
import com.abdurrahmanjun.runingapp.ui.MainViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var userPreferences: UserPreferences

    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()

    private var map: GoogleMap? = null

    private var curTimeInMillis = 0L
    private var curDistanceMeters = 0
    private var lapCount = 0

    // Bridges the service observers into the Compose overlay.
    private val fmt get() = UnitFormatter(userPreferences.isMetric)
    private var liveState by mutableStateOf(
        LiveRunState(
            elapsed = "0:00", distanceValue = "0.0", distanceUnit = "km",
            pace = "--'--\"", paceUnit = "/km", calories = "0", isTracking = false,
        )
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTrackingBinding.bind(view)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync {
            map = it
            it.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style_dark))
            it.uiSettings.isMyLocationButtonEnabled = false
            it.uiSettings.isMapToolbarEnabled = false
            enableMyLocation()
            addAllPolyline()
        }

        binding.topOverlay.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent { MomentumTheme { LiveRunTopBar(gpsText = "GPS strong", onRecenter = ::recenter) } }
        }
        binding.sheetOverlay.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MomentumTheme {
                    LiveRunSheet(
                        state = liveState,
                        onPauseResume = ::toggleRun,
                        onStop = { zoomToWholeTrack(); endRunAndSaveToDb() },
                        onLap = ::recordLap,
                    )
                }
            }
        }

        subscribeToObservers()
        updateLiveState()
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (!TrackingUtility.hasLocationPermissions(requireContext())) return
        map?.isMyLocationEnabled = true
        if (pathPoints.all { it.isEmpty() }) {
            LocationServices.getFusedLocationProviderClient(requireContext()).lastLocation
                .addOnSuccessListener { location ->
                    if (location != null && pathPoints.all { it.isEmpty() }) {
                        map?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(location.latitude, location.longitude), MAP_ZOOM
                            )
                        )
                    }
                }
        }
    }

    private fun toggleRun() {
        if (isTracking) sendCommandToService(ACTION_PAUSE_SERVICE)
        else sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
    }

    private fun recordLap() {
        lapCount++
        Snackbar.make(binding.root, "Lap $lapCount · ${fmt.clock(curTimeInMillis)}", Snackbar.LENGTH_SHORT).show()
    }

    private fun recenter() {
        val last = pathPoints.lastOrNull()?.lastOrNull()
        if (last != null) {
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(last, MAP_ZOOM))
        } else {
            enableMyLocation()
        }
    }

    private fun subscribeToObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner) { updateTracking(it) }
        TrackingService.pathPoints.observe(viewLifecycleOwner) {
            pathPoints = it
            addLatestPolyline()
            moveCameraUser()
            recomputeDistance()
            updateLiveState()
        }
        TrackingService.timeRunInMillis.observe(viewLifecycleOwner) {
            curTimeInMillis = it
            updateLiveState()
        }
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        updateLiveState()
    }

    private fun recomputeDistance() {
        var meters = 0
        for (polyline in pathPoints) meters += TrackingUtility.calculatePolylineLength(polyline).toInt()
        curDistanceMeters = meters
    }

    private fun updateLiveState() {
        val f = fmt
        liveState = LiveRunState(
            elapsed = f.clock(curTimeInMillis),
            distanceValue = f.distanceValue(curDistanceMeters),
            distanceUnit = f.distanceUnit,
            pace = f.pace(curDistanceMeters, curTimeInMillis),
            paceUnit = f.paceUnit,
            calories = ((curDistanceMeters / 1000f) * userPreferences.weightKg).toInt().toString(),
            isTracking = isTracking,
        )
    }

    // Glow layer (wide translucent) drawn under the bright mint core.
    private fun mintGlow(polyline: Polyline) = PolylineOptions()
        .color(POLYLINE_GLOW_COLOR).width(POLYLINE_GLOW_WIDTH).addAll(polyline)

    private fun mintCore(polyline: Polyline) = PolylineOptions()
        .color(POLYLINE_COLOR).width(POLYLINE_WIDTH).addAll(polyline)

    private fun addAllPolyline() {
        for (polyline in pathPoints) {
            map?.addPolyline(mintGlow(polyline))
            map?.addPolyline(mintCore(polyline))
        }
        pathPoints.firstOrNull()?.firstOrNull()?.let { addStartDot(it) }
    }

    private fun addStartDot(point: LatLng) {
        map?.addCircle(
            CircleOptions().center(point).radius(6.0)
                .fillColor(MomentumColors.MintBright.value.toInt())
                .strokeColor(0x66FFFFFF).strokeWidth(4f)
        )
    }

    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLast = pathPoints.last()[pathPoints.last().size - 2]
            val last = pathPoints.last().last()
            map?.addPolyline(PolylineOptions().color(POLYLINE_GLOW_COLOR).width(POLYLINE_GLOW_WIDTH).add(preLast).add(last))
            map?.addPolyline(PolylineOptions().color(POLYLINE_COLOR).width(POLYLINE_WIDTH).add(preLast).add(last))
        }
    }

    private fun moveCameraUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(pathPoints.last().last(), MAP_ZOOM))
        }
    }

    private fun zoomToWholeTrack() {
        val bounds = LatLngBounds.Builder()
        var hasPoints = false
        for (polyline in pathPoints) for (pos in polyline) { bounds.include(pos); hasPoints = true }
        if (!hasPoints) return
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(), binding.mapView.width, binding.mapView.height,
                (binding.mapView.height * 0.05f).toInt()
            )
        )
    }

    private fun endRunAndSaveToDb() {
        map?.snapshot { bmp ->
            var distanceInMeters = 0
            for (polyline in pathPoints) distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            val avgSpeed = if (curTimeInMillis > 0) {
                round((distanceInMeters / 1000f) / (curTimeInMillis / 1000f / 60 / 60) * 10) / 10f
            } else 0f
            val timestamp = System.currentTimeMillis()
            val caloriesBurned = ((distanceInMeters / 1000f) * userPreferences.weightKg).toInt()
            val run = RunEntity(
                img = bmp,
                timestamp = timestamp,
                avgSpeedInKMH = avgSpeed,
                distanceInMeters = distanceInMeters,
                timeInMillis = curTimeInMillis,
                caloriesBurned = caloriesBurned,
                trace = TrackingService.timedTrace.toList()
            )
            viewModel.insertRun(run)
            Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                "Run saved successfully",
                Snackbar.LENGTH_LONG
            ).show()
            stopRun()
        }
    }

    private fun stopRun() {
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    override fun onResume() { super.onResume(); binding.mapView.onResume() }
    override fun onStart() { super.onStart(); binding.mapView.onStart() }
    override fun onStop() { super.onStop(); binding.mapView.onStop() }
    override fun onPause() { super.onPause(); binding.mapView.onPause() }
    override fun onLowMemory() { super.onLowMemory(); binding.mapView.onLowMemory() }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState); binding.mapView.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
