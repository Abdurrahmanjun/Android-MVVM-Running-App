package com.abdurrahmanjun.runingapp.ui

import androidx.lifecycle.ViewModel
import com.abdurrahmanjun.runingapp.domain.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepository: MainRepository
): ViewModel() {

}