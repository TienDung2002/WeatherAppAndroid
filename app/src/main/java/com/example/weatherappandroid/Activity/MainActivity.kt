package com.example.weatherappandroid.Activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherappandroid.Adapter.ForecastAdapter
import com.example.weatherappandroid.Model.CurrentResponseApi
import com.example.weatherappandroid.Model.ForecastResponseApi
import com.example.weatherappandroid.R
import com.example.weatherappandroid.Uitls.PreventDoubleClick
import com.example.weatherappandroid.ViewModel.WeatherViewModel
import com.example.weatherappandroid.databinding.ActivityMainBinding
import com.github.matteobattilana.weather.PrecipType
import eightbitlab.com.blurview.RenderScriptBlur
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import java.io.IOException
import java.util.Locale


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val calendar by lazy { Calendar.getInstance() }
    private val forecastAdapter by lazy { ForecastAdapter() }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_REQUEST_CODE = 1000
    private var useCurrentLocation = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Đọc trạng thái từ SharedPreferences
        val preferences = getSharedPreferences("WeatherAppPrefs", MODE_PRIVATE)
        val hasChosenCity = preferences.getBoolean("hasChosenCity", false)
        useCurrentLocation = !hasChosenCity


        binding.apply {
            var lat = intent.getDoubleExtra("lat", 0.0)
            var lon = intent.getDoubleExtra("lon", 0.0)
            var name = intent.getStringExtra("name")

            Log.d("lat", lat.toString())
            Log.d("lon", lon.toString())
            Log.d("name", name.toString())

            if (lat == 0.0) {
                lat = 51.50
                lon = -0.12
                name = "London"
//                lat = 21.02
//                lon = 105.85
//                name = "Hanoi"
            }

            addCity.setOnClickListener {
                if (PreventDoubleClick.checkClick()) {
                    startActivity(Intent(this@MainActivity, CityListActivity::class.java))
                }
            }

            checkLocationPermission()

//            if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // Nếu chưa cấp quyền, yêu cầu quyền
//                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
//            } else {
//                // Nếu đã cấp quyền, quyết định hành vi dựa trên biến trạng thái
//                if (useCurrentLocation) {
//                    getCurrentLocation()
//                } else {
//                    // Nếu không sử dụng vị trí hiện tại, sử dụng lat, lon, name từ Intent
//                    updateUI(lat, lon, name!!)
//                }
//            }
            if (useCurrentLocation) {
                checkLocationPermission()
            } else {
                // Nếu không sử dụng vị trí hiện tại, sử dụng lat, lon, name từ Intent
                updateUI(lat, lon, name!!)
            }

            // Setting blur view
            val radius = 10f
            val decorView = window.decorView
//            val rootView = (decorView.findViewById(android.R.id.content) as ViewGroup?)
            val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
            val windowBackground = decorView.background

            rootView?.let{
                blurView.setupWith(it, RenderScriptBlur(this@MainActivity))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(radius)
                blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
                blurView.clipToOutline = true
            }





        }


    }


    private fun isNight(): Boolean {
        return calendar.get(Calendar.HOUR_OF_DAY) >= 18
    }

    private fun setDinamicWallpaper(icon: String) :Int{
        return when(icon.dropLast(1)){
            "01", "02" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.cloudy_sunny
            }
            "03", "04" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.cloudy_bg
            }
            "09", "10", "11" -> {
                initWeatherView(PrecipType.RAIN)
                R.drawable.rainy_bg
            }
            "13" -> {
                initWeatherView(PrecipType.SNOW)
                R.drawable.snow_bg
            }
            "50" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.haze_bg
            }
            else -> 0
        }
    }

    private fun setEffectRainSnow(icon: String){
        when(icon.dropLast(1)){
            "01", "02" -> initWeatherView(PrecipType.CLEAR)
            "03", "04" -> initWeatherView(PrecipType.CLEAR)
            "09", "10", "11" -> initWeatherView(PrecipType.RAIN)
            "13" -> initWeatherView(PrecipType.SNOW)
            "50" -> initWeatherView(PrecipType.CLEAR)
        }
    }

    private fun initWeatherView(type: PrecipType) {
        binding.weatherView.apply {
            setWeatherData(type)
            angle = -20
            emissionRate = 100.0f
        }
    }

    private fun getCurrentLocation() {
        Log.d("Location", "getCurrentLocation called")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener(this, OnSuccessListener { location ->
                    if (location != null) {
                        val lat = location.latitude
                        val lon = location.longitude
                        Log.d("Location", "Lat: $lat, Lon: $lon")

                        try {
                            val geocoder = Geocoder(this, Locale.getDefault())
                            val addresses: List<Address>? = geocoder.getFromLocation(lat, lon, 1)

                            if (addresses != null && addresses.isNotEmpty()) {
                                val address = addresses[0]
                                val cityName = address.locality
                                    ?: address.subAdminArea
                                    ?: address.adminArea
                                    ?: "Unknown City"

                                // Lưu thông tin thành phố vào SharedPreferences
                                getSharedPreferences("WeatherAppPrefs", MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("hasChosenCity", true)
                                    .putString("cityName", cityName)
                                    .putFloat("lat", lat.toFloat())
                                    .putFloat("lon", lon.toFloat())
                                    .apply()

                                updateUI(lat, lon, cityName)
                            } else {
                                Toast.makeText(this, "No city found for the location", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(this, "Geocoder service not available", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        } else {
            // quyền không được cấp
            Log.d("Location", "Location is null")
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
        }
    }



    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
        } else {
            getCurrentLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation() // Nếu quyền được cấp, lấy vị trí
            } else {
                // Quyền bị từ chối, sử dụng giá trị mặc định
                useCurrentLocation = false
                // Lưu trạng thái vào SharedPreferences để sử dụng sau
                getSharedPreferences("WeatherAppPrefs", MODE_PRIVATE)
                    .edit()
                    .putBoolean("hasChosenCity", false)
                    .apply()
                val lat = 51.50
                val lon = -0.12
                val name = "London"
                updateUI(lat, lon, name)
            }
        }
    }

    private fun updateUI(lat: Double, lon: Double, name: String) {
        Log.d("LocationInfo", "Lat: $lat, Lon: $lon, City: $name")
        binding.apply {
            cityTxt.text = name
            progressBar.visibility = View.VISIBLE
            weatherViewModel.loadCurrentWeather(lat, lon, "metric")
                .enqueue(object : Callback<CurrentResponseApi> {
                    override fun onResponse(
                        call: Call<CurrentResponseApi>,
                        response: Response<CurrentResponseApi>
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()
                            progressBar.visibility = View.GONE
                            detailLayout.visibility = View.VISIBLE
                            data?.let {
                                statusTxt.text = it.weather?.get(0)?.main ?: "-"
                                windTxt.text = getString(R.string.wind_speed, Math.round(it.wind?.speed ?: 0.0), getString(R.string.Km))
                                humidityTxt.text = getString(R.string.humidity, it.main?.humidity ?: 0)
                                currentTempTxt.text = getString(R.string.temperature, Math.round(it.main?.temp ?: 0.0))
                                maxTempTxt.text = getString(R.string.temperature, Math.round(it.main?.tempMax ?: 0.0))
                                minTempTxt.text = getString(R.string.temperature, Math.round(it.main?.tempMin ?: 0.0))

                                val icon = it.weather?.get(0)?.icon ?: "-"
                                Log.d("WeatherIcon", "Icon: $icon")

                                val drawableId = if (isNight()) R.drawable.night_background
                                else {
                                    setDinamicWallpaper(it.weather?.get(0)?.icon ?: "-")
                                }

                                bgImage.setImageResource(drawableId)
                                setEffectRainSnow(it.weather?.get(0)?.icon ?: "-")
                            }
                        }
                    }

                    override fun onFailure(call: Call<CurrentResponseApi>, t: Throwable) {
                        Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_SHORT).show()
                    }
                })

            weatherViewModel.loadForecastWeather(lat, lon, "metric")
                .enqueue(object : Callback<ForecastResponseApi> {
                    override fun onResponse(
                        call: Call<ForecastResponseApi>,
                        response: Response<ForecastResponseApi>
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()
                            blurView.visibility = View.VISIBLE
                            data?.let {
                                forecastAdapter.differ.submitList(it.list)
                                forecastView.apply {
                                    layoutManager = LinearLayoutManager(
                                        this@MainActivity,
                                        LinearLayoutManager.HORIZONTAL,
                                        false
                                    )
                                    adapter = forecastAdapter
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ForecastResponseApi>, t: Throwable) {
                        Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

}