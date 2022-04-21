package com.kin.weatherreport

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kin.weatherreport.model.City
import com.kin.weatherreport.model.Result
import com.kin.weatherreport.model.Weather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class WeatherViewModel constructor(private val repository: WeatherRepository)  : ViewModel() {

    val cityList = MutableLiveData<Result<List<City>>>()
    val weather = MutableLiveData<Result<Weather>>()

    fun getLocation(
        cityName: String,
        limit: Int,
        apiKey: String
    ) {
        val response = repository.getLocation(cityName, limit, apiKey)
        weather.postValue(Result.Loading)
        response.enqueue(object : Callback<List<City>> {
            override fun onResponse(call: Call<List<City>>, response: Response<List<City>>) {
                val res = response.body() ?: listOf()
                if (res.isNotEmpty()) {
                    cityList.postValue(Result.Success(res))
                } else {
                    cityList.postValue(Result.Error(Exception()))
                }

            }
            override fun onFailure(call: Call<List<City>>, t: Throwable) {
                cityList.postValue(Result.Error(Exception()))
            }
        })
    }

    fun getWeather(
        lat: Double,
        lon: Double,
        unit: String,
        apiKey: String
    ) {
        val response = repository.getWeather(lat, lon, unit, apiKey)
        weather.postValue(Result.Loading)
        response.enqueue(object : Callback<Weather> {
            override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                val res = response.body()
                if (res != null) {
                    res?.let {
                        weather.postValue(Result.Success(it))
                    }
                } else {
                    weather.postValue(Result.Error(Exception()))
                }
            }
            override fun onFailure(call: Call<Weather>, t: Throwable) {
                weather.postValue(Result.Error(Exception()))
            }
        })
    }
}