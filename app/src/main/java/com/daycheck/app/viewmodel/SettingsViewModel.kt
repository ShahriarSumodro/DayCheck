package com.daycheck.app.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import kotlinx.coroutines.launch

/**
 * ViewModel for the Settings fragment.
 * Manages app settings and preferences.
 */
class SettingsViewModel(private val context: Context) : ViewModel() {
    
    private val _notificationsEnabled = MutableLiveData<Boolean>()
    val notificationsEnabled: LiveData<Boolean> = _notificationsEnabled
    
    private val _darkModeEnabled = MutableLiveData<Boolean>()
    val darkModeEnabled: LiveData<Boolean> = _darkModeEnabled
    
    private val _followSystemTheme = MutableLiveData<Boolean>()
    val followSystemTheme: LiveData<Boolean> = _followSystemTheme
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    
    init {
        loadSettings()
    }
    
    /**
     * Loads current settings from SharedPreferences.
     */
    private fun loadSettings() {
        _isLoading.value = true
        
        _notificationsEnabled.value = preferences.getBoolean("notifications_enabled", true)
        _darkModeEnabled.value = preferences.getBoolean("dark_mode_enabled", false)
        _followSystemTheme.value = preferences.getBoolean("follow_system_theme", true)
        
        _isLoading.value = false
    }
    
    /**
     * Updates the notifications setting.
     */
    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        preferences.edit()
            .putBoolean("notifications_enabled", enabled)
            .apply()
    }
    
    /**
     * Updates the dark mode setting.
     */
    fun setDarkModeEnabled(enabled: Boolean) {
        _darkModeEnabled.value = enabled
        preferences.edit()
            .putBoolean("dark_mode_enabled", enabled)
            .apply()
    }
    
    /**
     * Updates the follow system theme setting.
     */
    fun setFollowSystemTheme(follow: Boolean) {
        _followSystemTheme.value = follow
        preferences.edit()
            .putBoolean("follow_system_theme", follow)
            .apply()
    }
    
    /**
     * Opens the system notification settings.
     */
    fun openNotificationSettings() {
        // This would typically open the system notification settings
        // Implementation depends on the specific Android version
    }
    
    /**
     * Clears all app data (database, preferences, etc.).
     */
    fun clearAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                // Clear preferences
                preferences.edit().clear().apply()
                
                // Clear database
                // Note: In a real implementation, you'd inject the repository
                // and call a method to clear the database
                
                // Reset settings to defaults
                loadSettings()
                
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Gets the app version information.
     */
    fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: Exception) {
            "Unknown"
        }
    }
    
    /**
     * Refreshes the settings.
     */
    fun refresh() {
        loadSettings()
    }
}