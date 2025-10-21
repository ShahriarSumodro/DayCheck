package com.example.daycheck.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.daycheck.databinding.FragmentSettingsBinding
import com.example.daycheck.viewmodel.SettingsViewModel

// SettingsFragment provides options to toggle notifications, manage channel, and clear data.
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        binding.switchNotifications.isChecked = sharedPref.getBoolean("notifications_enabled", true)

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("notifications_enabled", isChecked).apply()
        }

        binding.manageChannel.setOnClickListener {
            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, "reminders_channel")
            }
            startActivity(intent)
        }

        binding.clearData.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Clear Data")
                .setMessage("Are you sure you want to clear all data?")
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.clearData()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}