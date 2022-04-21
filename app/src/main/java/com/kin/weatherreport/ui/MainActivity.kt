package com.kin.weatherreport.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kin.weatherreport.*
import com.kin.weatherreport.databinding.ActivityMainBinding
import com.kin.weatherreport.model.City
import com.kin.weatherreport.model.Result
import com.kin.weatherreport.model.Weather
import com.kin.weatherreport.network.WeatherService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: WeatherViewModel
    private val service = WeatherService.getInstance()

    private var cityList: List<City> = listOf()
    private var weather: Weather? = null
    private var units = UNIT_CELSIUS // Default Units
    private var lastSearchedUnits = UNIT_CELSIUS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initInstances()
    }

    private fun initInstances() {
        initViewModel()
        setOnClickListener()
        setObserve()

        // Get default city
        getLocation("Bangkok")
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            WeatherViewModelFactory(
                WeatherRepository(service)
            )
        ).get(WeatherViewModel::class.java)
    }

    private fun setOnClickListener() {

        binding.apply {
            ivSearch.setOnClickListener {
                val city = binding.etCityName.text.toString()
                if (!city.isNullOrBlank()) {
                    getLocation(city)
                    this@MainActivity.hideKeyboard(etCityName)
                }
            }

            tvCelsius.setOnClickListener {
                units = UNIT_CELSIUS
                binding.tvCelsius.setTextColor(resources.getColor(R.color.black, null))
                binding.tvFahrenheit.setTextColor(resources.getColor(R.color.grey, null))

                if (weather != null) {
                    val temperature = weather?.main?.temp ?: 0.0

                    if (lastSearchedUnits != UNIT_CELSIUS) {
                        val celsius = convertFahrenheitToCelsius(temperature).round(2)
                        binding.tvTemperature.text = "${celsius} ํ"
                    } else {
                        binding.tvTemperature.text = "${temperature} ํ"
                    }
                }
            }

            tvFahrenheit.setOnClickListener {
                units = UNIT_FAHRENHEIT
                binding.tvCelsius.setTextColor(resources.getColor(R.color.grey, null))
                binding.tvFahrenheit.setTextColor(resources.getColor(R.color.black, null))

                if (weather != null) {
                    val temperature = weather?.main?.temp ?: 0.0

                    if (lastSearchedUnits != UNIT_FAHRENHEIT) {
                        val celsius = convertCelsiusToFahrenheit(temperature).round(2)
                        binding.tvTemperature.text = "${celsius} ํ"
                    } else {
                        binding.tvTemperature.text = "${temperature} ํ"
                    }
                }
            }
        }
    }

    private fun setObserve() {
        viewModel.cityList.observe(this, Observer {
            when (it) {
                is Result.Success -> {
                    showHideLoading(false)
                    hideError()
                    cityList = it.data

                    if (cityList.isNotEmpty()) {
                        val city = cityList.first()
                        getWeather(city.lat, city.lon, units)

                        lastSearchedUnits = units
                        binding.tvCityName.text = city.name
                    } else {
                        showError("Can't find city that you search")
                    }
                }

                is Result.Error -> {
                    showHideLoading(false)
                    showError("Can't find city right now. Please try again.")
                }

                is Result.Loading -> {
                    showHideLoading(true)
                    hideError()
                }
            }
        })

        viewModel.weather.observe(this, Observer {
            when (it) {
                is Result.Success -> {
                    showHideLoading(false)
                    hideError()
                    weather = it.data

                    weather?.let {
                        binding.tvTemperature.text = "${it.main?.temp} ํ"
                        binding.tvHumidity.text = "Humidity : ${it.main?.humidity}"
                    }
                }

                is Result.Error -> {
                    showHideLoading(false)
                    showError("Can't load weather right now. Please try again.")
                }

                is Result.Loading -> {
                    showHideLoading(true)
                    hideError()
                }
            }
        })
    }

    private fun getLocation(
        cityName: String
    ) {
        viewModel.getLocation(cityName, 1, AppConstants.API_KEY)
    }

    private fun getWeather(
        lat: Double,
        lon: Double,
        unit: String
    ) {
        viewModel.getWeather(lat, lon, unit, AppConstants.API_KEY)
    }

    private fun showHideLoading(status: Boolean) {
        binding.spinKit.visibility = if (status) View.VISIBLE else View.GONE
    }

    private fun showError(errorText: String) {
        binding.tvError.apply {
            visibility = View.VISIBLE
            text = errorText
        }
    }

    private fun hideError() {
        binding.tvError.apply {
            visibility = View.GONE
            text = ""
        }
    }

    private fun convertFahrenheitToCelsius(fahrenheit: Double): Double {
        return (fahrenheit - 32) * 5 / 9
    }

    private fun convertCelsiusToFahrenheit(celsius: Double): Double {
        return celsius * 9 / 5 + 32
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()

    companion object {
        const val UNIT_CELSIUS = "metric"
        const val UNIT_FAHRENHEIT = "imperial"
    }

}