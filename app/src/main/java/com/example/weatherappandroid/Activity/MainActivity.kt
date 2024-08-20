package com.example.weatherappandroid.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
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
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.example.weatherappandroid.Services.NotifyBroadcastReiceiver
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.tasks.OnSuccessListener
import java.io.IOException
import java.util.Locale


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val calendar by lazy { Calendar.getInstance() }
    private val forecastAdapter by lazy { ForecastAdapter() }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var useCurrentLocation = true

    companion object {
        const val LOCATION_APP_REQUEST_CODE = 1000
        const val LOCATION_DEVICE_REQUEST_CODE = 2000
        const val REQUEST_CODE_CITY = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo LocationRequest
        locationRequest = LocationRequest.create().apply {
            interval = 10000 // Time nhận dc update vị trí
            fastestInterval = 5000 // Time ngắn nhất giữa các lần update
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        scheduleDailyNotification()

        binding.apply {
            var lat = intent.getDoubleExtra("lat", 0.0)
            var lon = intent.getDoubleExtra("lon", 0.0)
            var name = intent.getStringExtra("name")

            Log.d("lat_intent", lat.toString())
            Log.d("lon_intent", lon.toString())
            Log.d("name_intent", name.toString())

            addCity.setOnClickListener {
                if (PreventDoubleClick.checkClick()) {
                    val intent = Intent(this@MainActivity, CityListActivity::class.java)
                    startActivityForResult(intent, REQUEST_CODE_CITY)
                }
            }

            if (useCurrentLocation) {
                checkLocationPermission()
            } else {
                if (lat == 0.0 || lon == 0.0 || name.isNullOrEmpty()) {
                    useDefaultLocation()
                }
            }

            // swipe refresh
            swipeRefreshLayout.setOnRefreshListener {
                checkLocationPermission()
                if (useCurrentLocation) {
                    getCurrentLocation()
                } else {
                    useDefaultLocation()
                }
                swipeRefreshLayout.isRefreshing = false
            }

            // Setting blur view
            val radius = 10f
            val decorView = window.decorView
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


    private fun scheduleDailyNotification() {
        Log.d("scheduleDaily", "Notification scheduled_1")

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotifyBroadcastReiceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 6) // Tgian gửi noti hàng ngày lúc 6h sáng
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        // Nếu thời gian đã qua thì đặt cho ngày hôm sau
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        Log.d("scheduleDaily", "Notification scheduled")
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


    private fun useDefaultLocation() {
        val lat = 51.50
        val lon = -0.12
        val name = "London"
        updateUI(lat, lon, name)
    }


    private fun getCurrentLocation() {
        Log.d("getCurrentLocation_called", "getCurrentLocation_called")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        val lat = location.latitude
                        val lon = location.longitude

                        try {
                            val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                            val addresses: List<Address>? = geocoder.getFromLocation(lat, lon, 1)

                            if (addresses != null && addresses.isNotEmpty()) {
                                val address = addresses[0]
                                val cityName = address.locality
                                    ?: address.subAdminArea
                                    ?: address.adminArea
                                    ?: "Unknown City"
                                Log.d("CurLocation", "Lat: $lat, Lon: $lon, Name: $cityName")

                                updateUI(lat, lon, cityName)
                            } else {
                                Toast.makeText(this@MainActivity, "No city found for the location", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(this@MainActivity, "Geocoder service not available", Toast.LENGTH_SHORT).show()
                        }

                        // Ngừng cập nhật vị trí sau khi lấy được vị trí
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } else {
            // quyền không được cấp
            Log.d("CurLocation", "Location is null")
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
        }
    }


    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_APP_REQUEST_CODE)
        } else {
            checkLocationSettings()
        }
    }


    private fun checkLocationSettings() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        // Nếu tất cả cài đặt vị trí trên cả thiết bị và app đều được bật
        task.addOnSuccessListener { response ->
            Log.d("checkLocationSettings", "All settings are on")
            getCurrentLocation()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Vị trí không được bật nhưng có thể bật lên thông qua hộp thoại
                try {
                    exception.startResolutionForResult(this@MainActivity, LOCATION_DEVICE_REQUEST_CODE)
                } catch (_: IntentSender.SendIntentException) {
                    Toast.makeText(this, "Location error", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Không thể bật vị trí
                useCurrentLocation = false
                useDefaultLocation()
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_APP_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationSettings() // Nếu quyền được cấp, kiểm tra cài đặt vị trí của thiết bị
                Log.d("RequestPermissionsResult", "Request Permissions Accepted")
            } else {
                // Quyền bị từ chối, sử dụng giá trị mặc định
                Log.d("RequestPermissionsResult", "Request Permissions Denied")
                useCurrentLocation = false
                useDefaultLocation()
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CITY && resultCode == Activity.RESULT_OK && data != null) {
            val lat = data.getDoubleExtra("lat", 0.0)
            val lon = data.getDoubleExtra("lon", 0.0)
            val name = data.getStringExtra("name")
            useCurrentLocation = false
            updateUI(lat, lon, name!!)
        }
        else if (requestCode == LOCATION_DEVICE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                getCurrentLocation()
            } else {
                useCurrentLocation = false
                useDefaultLocation()
            }
        }
    }

}