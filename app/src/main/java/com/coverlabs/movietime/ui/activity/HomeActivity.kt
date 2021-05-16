package com.coverlabs.movietime.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.coverlabs.movietime.R
import com.coverlabs.movietime.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)

        with(binding) {
            setContentView(root)

            val navHostFragment = supportFragmentManager.findFragmentById(
                R.id.nav_host_fragment_activity_home
            ) as NavHostFragment
            navView.setupWithNavController(navHostFragment.navController)
        }
    }
}