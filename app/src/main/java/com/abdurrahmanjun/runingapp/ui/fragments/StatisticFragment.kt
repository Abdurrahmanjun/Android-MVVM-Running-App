package com.abdurrahmanjun.runingapp.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.abdurrahmanjun.runingapp.R
import com.abdurrahmanjun.runingapp.ui.viewmodels.MainViewModel
import com.abdurrahmanjun.runingapp.ui.viewmodels.StatisticViewModel

class StatisticFragment : Fragment(R.layout.fragment_statistics) {

    private val viewModel : StatisticViewModel by viewModels()

}