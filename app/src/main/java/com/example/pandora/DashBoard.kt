package com.example.pandora

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DashBoard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setLogo(R.drawable.pandroid)
            setDisplayUseLogoEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
    }
}