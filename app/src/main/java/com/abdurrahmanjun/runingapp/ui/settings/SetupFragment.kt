package com.abdurrahmanjun.runingapp.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.abdurrahmanjun.runingapp.R
import com.abdurrahmanjun.runingapp.data.local.UserPreferences
import com.abdurrahmanjun.runingapp.databinding.FragmentSetupBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup) {

    @Inject
    lateinit var userPreferences: UserPreferences

    private var _binding: FragmentSetupBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSetupBinding.bind(view)

        // Skip setup once the profile has been entered before.
        if (!userPreferences.isFirstAppOpen) {
            findNavController().navigate(
                R.id.action_setupFragment_to_runFragment,
                null,
                androidx.navigation.navOptions {
                    popUpTo(R.id.setupFragment) { inclusive = true }
                }
            )
            return
        }

        binding.btnContinue.setOnClickListener {
            if (saveProfile()) {
                findNavController().navigate(
                    R.id.action_setupFragment_to_runFragment,
                    null,
                    androidx.navigation.navOptions {
                        popUpTo(R.id.setupFragment) { inclusive = true }
                    }
                )
            }
        }
    }

    private fun saveProfile(): Boolean {
        val name = binding.etName.text.toString().trim()
        val weight = binding.etWeight.text.toString().trim()
        if (name.isEmpty() || weight.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a name and weight", Toast.LENGTH_SHORT).show()
            return false
        }
        userPreferences.name = name
        userPreferences.weightKg = weight.toFloatOrNull() ?: 70f
        userPreferences.isFirstAppOpen = false
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
