package com.example.weatherappandroid.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherappandroid.Model.ForecastResponseApi
import com.example.weatherappandroid.R
import com.example.weatherappandroid.databinding.ForecastViewholderBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {
    private lateinit var binding: ForecastViewholderBinding
    private lateinit var context: Context

    // Định nghĩa ViewHolder => Quản lý các view con
    inner class ForecastViewHolder : RecyclerView.ViewHolder(binding.root)

    // Tạo viewholder mới
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastAdapter.ForecastViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ForecastViewholderBinding.inflate(inflater, parent, false)
        return ForecastViewHolder()
    }


    // Gán data vào viewholder
    override fun onBindViewHolder(holder: ForecastAdapter.ForecastViewHolder, position: Int) {
        val binding = ForecastViewholderBinding.bind(holder.itemView)
        context = binding.root.context

        val date = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(differ.currentList[position].dtTxt.toString())
        val calendar = Calendar.getInstance()
        calendar.time = date

        // Ngày
        val daysOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> context.getString(R.string.sun)
            Calendar.MONDAY -> context.getString(R.string.mon)
            Calendar.TUESDAY -> context.getString(R.string.tue)
            Calendar.WEDNESDAY -> context.getString(R.string.wed)
            Calendar.THURSDAY -> context.getString(R.string.thu)
            Calendar.FRIDAY -> context.getString(R.string.fri)
            Calendar.SATURDAY -> context.getString(R.string.sat)
            else -> "-"
        }

        binding.nameDayTxt.text = daysOfWeek

        // Giờ
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val amPm = if (hour < 12) " AM" else " PM"
        val hour12 = calendar.get(Calendar.HOUR).toString()
        binding.hourTxt.text = hour12 + amPm

        // temp
        val temp = Math.round(differ.currentList[position].main?.temp ?: 0.0).toInt()
        binding.tempTxt.text = holder.itemView.context.getString(R.string.temperature, temp)

        // icon
        val icon = when (differ.currentList[position].weather?.get(0)?.icon.toString()) {
            "01d", "01n" -> "sunny"
            "02d", "02n" -> "cloudy_sunny"
            "03d", "03n" -> "cloudy_sunny"
            "04d", "04n" -> "cloudy"
            "09d", "09n" -> "rainy"
            "10d", "10n" -> "rainy"
            "11d", "11n" -> "storm"
            "13d", "13n" -> "snowy"
            "50d", "50n" -> "windy"
            else -> "sunny"
        }
        val drawableResourceId: Int = binding.root.resources.getIdentifier(
            icon,
            "drawable", binding.root.context.packageName
        )
        Glide.with(binding.root.context)
            .load(drawableResourceId)
            .into(binding.pic)
    }


    // trả về tổng số item
    override fun getItemCount() = differ.currentList.size


    // Tạo differ => so sánh và cập nhật dữ liệu mới
    private val differCallback = object : DiffUtil.ItemCallback<ForecastResponseApi.data>() {
        override fun areItemsTheSame(
            oldItem: ForecastResponseApi.data,
            newItem: ForecastResponseApi.data
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ForecastResponseApi.data,
            newItem: ForecastResponseApi.data
        ): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

}