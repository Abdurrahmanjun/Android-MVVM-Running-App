package com.abdurrahmanjun.runingapp.ui.statistic

import androidx.lifecycle.ViewModel
import com.abdurrahmanjun.runingapp.domain.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(
    val mainRepository: MainRepository
): ViewModel() {
}