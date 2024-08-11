package com.example.weatherappandroid.Activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherappandroid.Model.CurrentResponseApi
import com.example.weatherappandroid.R
import com.example.weatherappandroid.ViewModel.WeatherViewModel
import com.example.weatherappandroid.databinding.ActivityMainBinding
import com.github.matteobattilana.weather.PrecipType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val calendar by lazy { Calendar.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        binding.apply {
            var lat = 51.50
            var lon = -0.12
            var name = "London"
            cityTxt.text = name
            progressBar.visibility = View.VISIBLE
            weatherViewModel.loadCurrentWeather(lat, lon, "metric").enqueue(object :Callback<CurrentResponseApi>{
                override fun onResponse(call: Call<CurrentResponseApi>, response: Response<CurrentResponseApi>) {
                    if (response.isSuccessful){
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

                            val drawableId = if (isNight()) R.drawable.night_background
                            else {
                                setDinamicWallpaper(it.weather?.get(0)?.icon?: "-")
                            }

                            bgImage.setImageResource(drawableId)
                            setEffectRainSnow(it.weather?.get(0)?.icon?: "-")
                        }
                    }
                }

                override fun onFailure(call: Call<CurrentResponseApi>, t: Throwable) {
                    Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_SHORT).show()
                }

            })
        }
    }


    private fun isNight(): Boolean {
        return calendar.get(Calendar.HOUR_OF_DAY) >= 18
    }

    private fun setDinamicWallpaper(icon: String) :Int{
        return when(icon.dropLast(1)){
            "01" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.snow_bg
            }
            "02", "03", "04" -> {
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
            "01" -> {
                initWeatherView(PrecipType.CLEAR)
            }
            "02", "03", "04" -> {
                initWeatherView(PrecipType.CLEAR)
            }
            "09", "10", "11" -> {
                initWeatherView(PrecipType.RAIN)
            }
            "13" -> {
                initWeatherView(PrecipType.SNOW)
            }
            "50" -> {
                initWeatherView(PrecipType.CLEAR)
            }
        }
    }

    private fun initWeatherView(type: PrecipType) {
        binding.weatherView.apply {
            setWeatherData(type)
            angle = -20
            emissionRate = 100.0f
        }
    }
}