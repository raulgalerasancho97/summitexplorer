package com.example.summitexplorer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isLogged = sharedPreferences.getBoolean("isLogged", false)
        if (isLogged) {
            showHomePage()
        } else {
            showLoginPage()
        }
    }

    private fun showHomePage() {
        setContentView(R.layout.activity_main)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home_button -> {
                    loadFragment(HomeFragment())
                    true
                }

                R.id.map_button -> {
                    loadFragment(MapFragment())
                    true
                }

                R.id.list_button -> {
                    loadFragment(ListFragment())
                    true
                }

                R.id.settings_button -> {
                    loadFragment(SettingsFragment())
                    true
                }

                else -> false
            }
        }
        bottomNavigationView.selectedItemId = R.id.home_button
    }

    private fun showLoginPage() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }
}
