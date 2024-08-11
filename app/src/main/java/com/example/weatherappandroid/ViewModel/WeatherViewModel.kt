package com.example.weatherappandroid.ViewModel

import androidx.lifecycle.ViewModel
import com.example.weatherappandroid.Repository.WeatherRepository
import com.example.weatherappandroid.Server.ApiClient
import com.example.weatherappandroid.Server.ApiServices
import retrofit2.create

class WeatherViewModel(val repository: WeatherRepository) : ViewModel() {

    // tự động tạo repository bằng gọi ApiClient
    constructor() : this(WeatherRepository(ApiClient().getClient().create(ApiServices::class.java)))

    fun loadCurrentWeather(lat: Double, lon: Double, unit: String) =
        repository.getCurWeather(lat, lon, unit)

    fun loadForecastWeather(lat: Double, lon: Double, unit: String) =
        repository.getForecastWeather(lat, lon, unit)
}