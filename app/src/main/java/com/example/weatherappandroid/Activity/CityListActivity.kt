package com.example.weatherappandroid.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherappandroid.Adapter.CityAdapter
import com.example.weatherappandroid.Model.CityResponseApi
import com.example.weatherappandroid.R
import com.example.weatherappandroid.ViewModel.CityViewmodel
import com.example.weatherappandroid.databinding.ActivityCityListBinding
import retrofit2.Call
import retrofit2.Response

class CityListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCityListBinding
    private val cityAdapter by lazy { CityAdapter() }
    private val cityViewModel: CityViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            // nút back
            backButton.setOnClickListener {
                val resultIntent = Intent()
                setResult(Activity.RESULT_CANCELED, resultIntent)
                finish()
            }

            cityEdt.addTextChangedListener(object: TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().isEmpty()) {
                        cityProgressBar.visibility = View.GONE
                    } else {
                        cityProgressBar.visibility = View.VISIBLE
                        cityViewModel.loadCity(s.toString(), 10).enqueue(object : retrofit2.Callback<CityResponseApi> {
                            override fun onResponse(
                                call: Call<CityResponseApi>,
                                response: Response<CityResponseApi>
                            ) {
                                if (response.isSuccessful) {
                                    val data = response.body()
                                    data?.let {
                                        cityProgressBar.visibility = View.GONE
                                        cityAdapter.differ.submitList(it)
                                        cityView.apply {
                                            layoutManager = LinearLayoutManager(
                                                this@CityListActivity,
                                                LinearLayoutManager.VERTICAL,
                                                false
                                            )
                                            adapter = cityAdapter
                                        }
                                    }
                                }
                            }

                            override fun onFailure(call: Call<CityResponseApi>, t: Throwable) {
                                Toast.makeText(this@CityListActivity, t.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        })
                    }
                }
            })


        }
    }
}