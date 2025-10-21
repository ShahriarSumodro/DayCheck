package com.daycheck.app.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.daycheck.app.R
import com.daycheck.app.databinding.FragmentSettingsBinding
import com.daycheck.app.viewmodel.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Fragment that displays app settings and preferences.
 * Allows users to configure notifications, theme, and manage app data.
 */
class SettingsFragment : Fragment() {
    
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: SettingsViewModel by activityViewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
        observeViewModel()
    }
    
    /**
     * Sets up click listeners for all interactive elements.
     */
    private fun setupClickListeners() {
        // Notifications switch
        binding.notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setNotificationsEnabled(isChecked)
        }
        
        // Notification settings button
        binding.btnNotificationSettings.setOnClickListener {
            viewModel.openNotificationSettings()
        }
        
        // Follow system theme switch
        binding.followSystemSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setFollowSystemTheme(isChecked)
        }
        
        // Clear data button
        binding.btnClearData.setOnClickListener {
            showClearDataConfirmation()
        }
    }
    
    /**
     * Observes the ViewModel for data changes.
     */
    private fun observeViewModel() {
        viewModel.notificationsEnabled.observe(viewLifecycleOwner, Observer { enabled ->
            binding.notificationsSwitch.isChecked = enabled
        })
        
        viewModel.darkModeEnabled.observe(viewLifecycleOwner, Observer { enabled ->
            // Handle dark mode setting if needed
        })
        
        viewModel.followSystemTheme.observe(viewLifecycleOwner, Observer { follow ->
            binding.followSystemSwitch.isChecked = follow
        })
        
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.root.isEnabled = !isLoading
        })
        
        // Set app version
        binding.appVersionText.text = viewModel.getAppVersion()
    }
    
    /**
     * Shows a confirmation dialog for clearing all app data.
     */
    private fun showClearDataConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Clear All Data")
            .setMessage(getString(R.string.confirm_clear_data))
            .setPositiveButton("Clear") { _, _ ->
                viewModel.clearAllData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    /**
     * Opens the system notification settings.
     */
    private fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            data = Uri.parse("package:${requireContext().packageName}")
        }
        startActivity(intent)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}