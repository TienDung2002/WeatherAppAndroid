package com.example.weatherappandroid.Repository

import com.example.weatherappandroid.Server.ApiServices

class CityRepository(val api: ApiServices) {
    fun getCities(q: String, limit: Int) =
        api.getCitiesList(q, limit, "38dcf46e6065634c2ed5dfc9fad11f29")
}