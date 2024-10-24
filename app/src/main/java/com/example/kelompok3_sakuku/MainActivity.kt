package com.example.kelompok3_sakuku

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.kelompok3_sakuku.ui.home.HomeFragment
import com.example.kelompok3_sakuku.ui.budget.BudgetFragment
import com.example.kelompok3_sakuku.ui.wallet.WalletFragment
import com.example.kelompok3_sakuku.ui.goals.GoalsFragment
import com.example.kelompok3_sakuku.ui.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Set default fragment saat aplikasi dibuka
        loadFragment(HomeFragment())

        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_budget -> loadFragment(BudgetFragment())
                R.id.nav_wallet -> loadFragment(WalletFragment())
                R.id.nav_goals -> loadFragment(GoalsFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
