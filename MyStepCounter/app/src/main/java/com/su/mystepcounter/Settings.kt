package com.su.mystepcounter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.su.mystepcounter.databinding.ActivitySettingsBinding

class Settings : AppCompatActivity() {

    private lateinit var settingsbinding : ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsbinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(settingsbinding.root)

        //settingsbinding.bottomNav2.setItemSelected(R.id.settings)

        settingsbinding.bottomNav2.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    // Navigate to AchievementsActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.achievement -> {
                    // Navigate to Settings Activity
                    startActivity(Intent(this, AchievementsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}