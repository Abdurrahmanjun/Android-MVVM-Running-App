package com.abdurrahmanjun.runingapp.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.abdurrahmanjun.runingapp.R
import com.abdurrahmanjun.runingapp.ui.viewmodels.MainViewModel

class RunFragment : Fragment(R.layout.fragment_run) {

    private val viewModel : MainViewModel by viewModels()
}