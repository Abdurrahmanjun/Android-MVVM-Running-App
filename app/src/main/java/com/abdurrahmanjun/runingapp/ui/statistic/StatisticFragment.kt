package com.abdurrahmanjun.runingapp.ui.statistic

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.abdurrahmanjun.runingapp.R
import com.abdurrahmanjun.runingapp.databinding.FragmentStatisticsBinding
import com.abdurrahmanjun.runingapp.utils.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class StatisticFragment : Fragment(R.layout.fragment_statistics) {

    private val viewModel: StatisticViewModel by viewModels()

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStatisticsBinding.bind(view)
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        viewModel.totalTimeRun.observe(viewLifecycleOwner) { time ->
            binding.tvTotalTime.text = TrackingUtility.getFormattedStopWatchTime(time ?: 0L)
        }
        viewModel.totalDistance.observe(viewLifecycleOwner) { distance ->
            val km = round((distance ?: 0) / 1000f * 10) / 10f
            binding.tvTotalDistance.text = "${km}km"
        }
        viewModel.totalAvgSpeed.observe(viewLifecycleOwner) { speed ->
            val rounded = round((speed ?: 0f) * 10) / 10f
            binding.tvAverageSpeed.text = "${rounded}km/h"
        }
        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner) { calories ->
            binding.tvTotalCalories.text = "${calories ?: 0}kcal"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
