package com.example.weatherappandroid.ViewModel

import androidx.lifecycle.ViewModel
import com.example.weatherappandroid.Repository.CityRepository
import com.example.weatherappandroid.Server.ApiClient
import com.example.weatherappandroid.Server.ApiServices

class CityViewmodel(val repository: CityRepository): ViewModel() {
    constructor():this(CityRepository(ApiClient().getClient().create(ApiServices::class.java)))

    fun loadCity(q: String, limit: Int) =
        repository.getCities(q, limit)
}