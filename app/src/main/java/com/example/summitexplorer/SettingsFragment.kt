package com.example.summitexplorer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val buttonLogout = view.findViewById<Button>(R.id.buttonLogout)
        buttonLogout.setOnClickListener {
            clearSharedPreferences()
            navigateToLogin()
        }

        val buttonTheme = view.findViewById<Button>(R.id.buttonTheme)
        buttonTheme.setOnClickListener {
            toggleTheme()
        }

        return view
    }

    private fun clearSharedPreferences() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun toggleTheme() {
        val currentNightMode =
            resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            android.content.res.Configuration.UI_MODE_NIGHT_NO -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            android.content.res.Configuration.UI_MODE_NIGHT_YES -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}
