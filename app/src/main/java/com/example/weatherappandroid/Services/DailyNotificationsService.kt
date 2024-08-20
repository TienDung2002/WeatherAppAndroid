package com.example.weatherappandroid.Services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.weatherappandroid.Model.CurrentResponseApi
import com.example.weatherappandroid.R
import com.example.weatherappandroid.ViewModel.WeatherViewModel
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.Locale


class DailyNotificationsService : Service() {
    private val weatherViewModel: WeatherViewModel = WeatherViewModel()


    // Gọi khi service khởi tạo
    override fun onCreate() {
        Log.d("Service_created", "Service created")
        super.onCreate()
    }

    // Gọi đến bằng startService() từ acti khác
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        getCurrentLocationOrDefault()
        Log.d("Service_started", "Service started")
        return START_NOT_STICKY
    }

    // gọi đến khi thành phần khác gọi service bằng câu lệnh bindService()
    override fun onBind(intent: Intent?): IBinder? = null


    // Gọi khi service bị hủy
    override fun onDestroy() {
        Log.d("Service_destroyed", "Service destroyed")
        super.onDestroy()
    }


    private fun useDefaultLocation() {
        val lat = 51.50
        val lon = -0.12
        fetchWeatherAndShowNotification(lat, lon, "London")
    }


    private fun fetchWeatherAndShowNotification(lat: Double, lon: Double, cityName: String) {
        weatherViewModel.loadCurrentWeather(lat, lon, "metric").enqueue(object : Callback<CurrentResponseApi> {
            override fun onResponse(call: Call<CurrentResponseApi>, response: Response<CurrentResponseApi>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    data?.let {
                        val temp = Math.round(it.main?.temp ?: 0.0)
                        val humidity = it.main?.humidity ?: 0
                        val tempMax = Math.round(it.main?.tempMax ?: 0.0)
                        val tempMin = Math.round(it.main?.tempMin ?: 0.0)
                        val weatherDescription = it.weather?.get(0)?.description ?: "-"

                        val notificationContent = "$weatherDescription, nhiệt độ cao nhất ${tempMax}°C, thấp nhất ${tempMin}°C, độ ẩm $humidity%"
                        showNotification(cityName, notificationContent)
                    }
                }
            }

            override fun onFailure(call: Call<CurrentResponseApi>, t: Throwable) {
                Log.d("FetchWeatherError", "Error fetching weather: ${t.message}")
            }
        })
    }


    private fun showNotification(cityName: String, weatherInfo: String) {
        Log.d("NotiSerCalled", "showNotification called")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "daily_notification_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(cityName)       // Tên thành phố here
            .setContentText(weatherInfo)     // Thông tin thời tiết
            .setSmallIcon(R.drawable.app_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1, notification)
    }


    private fun getCurrentLocationOrDefault() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude

                    try {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val addresses: List<Address>? = geocoder.getFromLocation(lat, lon, 1)

                        if (addresses != null && addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val cityName = address.locality
                                ?: address.subAdminArea
                                ?: address.adminArea
                                ?: "Unknown City"
                            Log.d("CurLocationSer", "Lat: $lat, Lon: $lon, Name: $cityName")

                            fetchWeatherAndShowNotification(lat, lon, cityName)

                        } else {
                            Log.d("CurLocationSer", "No city found for the location")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.d("CurLocationSer", "Geocoder service not available")
                    }

                } else {
                    useDefaultLocation()
                }
            }.addOnFailureListener {
                useDefaultLocation()
            }
        } else {
            useDefaultLocation()
        }
    }


}