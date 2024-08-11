package com.example.weatherappandroid.Repository

import com.example.weatherappandroid.Server.ApiServices

class WeatherRepository(val api: ApiServices) {
    fun getCurWeather(lat: Double, lon: Double, unit: String) =
        api.getCurrentWeather(lat, lon, unit, "38dcf46e6065634c2ed5dfc9fad11f29")

    fun getForecastWeather(lat: Double, lon: Double, unit: String) =
        api.getForecastWeather(lat, lon, unit, "38dcf46e6065634c2ed5dfc9fad11f29")
}