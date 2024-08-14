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

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val calendar by lazy { Calendar.getInstance() }
    private val forecastAdapter by lazy { ForecastAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.apply {
            var lat = intent.getDoubleExtra("lat", 0.0)
            var lon = intent.getDoubleExtra("lon", 0.0)
            var name = intent.getStringExtra("name")

            Log.d("lat", lat.toString())
            Log.d("lon", lon.toString())
            Log.d("name", name.toString())


            if (lat == 0.0) {
//                lat = 51.50
//                lon = -0.12
//                name = "London"
                lat = 21.02
                lon = 105.85
                name = "Hanoi"
            }

            addCity.setOnClickListener {
                if (PreventDoubleClick.checkClick()) {
                    startActivity(Intent(this@MainActivity, CityListActivity::class.java))
                }
            }


            // temp hiện tại
            cityTxt.text = name
            progressBar.visibility = View.VISIBLE
            weatherViewModel.loadCurrentWeather(lat, lon, "metric")
                // enqueue thực hiện yêu cầu bất đồng bộ, enqueue được cung cấp bởi retrofit2, tác dụng xử lí giống như coroutine
                .enqueue(object :Callback<CurrentResponseApi>{
                override fun onResponse(
                    call: Call<CurrentResponseApi>,
                    response: Response<CurrentResponseApi>
                ) {
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

                            // Check the icon value in log
                            val icon = it.weather?.get(0)?.icon ?: "-"
                            Log.d("WeatherIcon", "Icon: $icon")

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



            // dự báo temp
            weatherViewModel.loadForecastWeather(lat, lon, "metric")
                .enqueue(object: Callback<ForecastResponseApi>{
                    override fun onResponse(
                        call: Call<ForecastResponseApi>,
                        response: Response<ForecastResponseApi>
                    ) {
                        if (response.isSuccessful){
                            val data = response.body()
                            blurView.visibility = View.VISIBLE
                            data?.let {
                                forecastAdapter.differ.submitList(it.list)
                                forecastView.apply{
                                    layoutManager = LinearLayoutManager(
                                        this@MainActivity,
                                        LinearLayoutManager.HORIZONTAL,
                                        false)
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
}