package com.example.weatherappandroid.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherappandroid.R
import com.example.weatherappandroid.databinding.ActivityCityListBinding

class CityListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCityListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCityListBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}