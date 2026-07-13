package com.abdurrahmanjun.runingapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.abdurrahmanjun.runingapp.data.local.entity.RunEntity
import com.abdurrahmanjun.runingapp.databinding.ItemRunBinding
import com.abdurrahmanjun.runingapp.utils.TrackingUtility
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.round

class TrackingHistoryAdapter :
    ListAdapter<RunEntity, TrackingHistoryAdapter.RunViewHolder>(DIFF_CALLBACK) {

    inner class RunViewHolder(val binding: ItemRunBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val binding = ItemRunBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RunViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = getItem(position)
        with(holder.binding) {
            Glide.with(ivRunImage).load(run.img).into(ivRunImage)

            val calendar = Calendar.getInstance().apply { timeInMillis = run.timestamp }
            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            tvDate.text = dateFormat.format(calendar.time)

            val distanceKm = round(run.distanceInMeters / 1000f * 10) / 10f
            tvDistance.text = "${distanceKm}km"
            tvTime.text = TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)
            tvAvgSpeed.text = "${run.avgSpeedInKMH}km/h"
            tvCalories.text = "${run.caloriesBurned}kcal"
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RunEntity>() {
            override fun areItemsTheSame(oldItem: RunEntity, newItem: RunEntity) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: RunEntity, newItem: RunEntity) =
                oldItem == newItem
        }
    }
}
