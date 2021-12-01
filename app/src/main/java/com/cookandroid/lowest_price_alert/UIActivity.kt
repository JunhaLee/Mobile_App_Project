package com.cookandroid.lowest_price_alert

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.cookandroid.lowest_price_alert.fragment.HomeFragment
import com.cookandroid.lowest_price_alert.fragment.SettingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class UIActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val settingFragment = SettingFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        replaceFragment(homeFragment)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation?.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.navigation_home -> replaceFragment(homeFragment)
                R.id.navigation_settings -> replaceFragment(settingFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }
}