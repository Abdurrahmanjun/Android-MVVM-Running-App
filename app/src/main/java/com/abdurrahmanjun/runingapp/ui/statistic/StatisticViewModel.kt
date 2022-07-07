package com.abdurrahmanjun.runingapp.ui.statistic

import androidx.lifecycle.ViewModel
import com.abdurrahmanjun.runingapp.data.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(
    val mainRepository: MainRepository
): ViewModel() {
}