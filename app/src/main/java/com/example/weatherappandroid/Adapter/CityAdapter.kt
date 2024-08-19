package com.example.weatherappandroid.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherappandroid.Activity.MainActivity
import com.example.weatherappandroid.Model.CityResponseApi
import com.example.weatherappandroid.Model.ForecastResponseApi
import com.example.weatherappandroid.R
import com.example.weatherappandroid.databinding.CityViewholderBinding
import com.example.weatherappandroid.databinding.ForecastViewholderBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class CityAdapter : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {
    private lateinit var binding: CityViewholderBinding
    private lateinit var context: Context


    // Định nghĩa ViewHolder => Quản lý các view con
    inner class CityViewHolder : RecyclerView.ViewHolder(binding.root)


    // Tạo viewholder mới
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityAdapter.CityViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = CityViewholderBinding.inflate(inflater, parent, false)
        return CityViewHolder()
    }


    // Lấy data và gửi về main activity
    override fun onBindViewHolder(holder: CityAdapter.CityViewHolder, position: Int) {
        val binding = CityViewholderBinding.bind(holder.itemView)
        binding.cityTxt.text = differ.currentList[position].name

        binding.root.setOnClickListener{
            val resultIntent = Intent()
            resultIntent.putExtra("lat", differ.currentList[position].lat)
            resultIntent.putExtra("lon", differ.currentList[position].lon)
            resultIntent.putExtra("name", differ.currentList[position].name)
            (binding.root.context as Activity).setResult(Activity.RESULT_OK, resultIntent)
            (binding.root.context as Activity).finish()
        }
    }


    // trả về tổng số item
    override fun getItemCount() = differ.currentList.size


    // Tạo differ => so sánh và cập nhật dữ liệu mới
    private val differCallback = object : DiffUtil.ItemCallback<CityResponseApi.CityResponseApiItem>() {
        override fun areItemsTheSame(
            oldItem: CityResponseApi.CityResponseApiItem,
            newItem: CityResponseApi.CityResponseApiItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CityResponseApi.CityResponseApiItem,
            newItem: CityResponseApi.CityResponseApiItem
        ): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

}