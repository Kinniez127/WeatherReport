package com.kin.weatherreport.network

import com.kin.weatherreport.model.City
import com.kin.weatherreport.model.Weather
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherService {

    @GET("geo/1.0/direct")
    fun getLocation(
        @Query("q") cityName: String,
        @Query("limit") limit: Int,
        @Query("appid") apiKey: String
    ): Call<List<City>>

    @GET("data/2.5/weather")
    fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") unit: String,
        @Query("appid") apiKey: String
    ): Call<Weather>

    companion object {
        val baseUrl = "https://api.openweathermap.org/"
        var service: WeatherService? = null

        fun getInstance() : WeatherService {
            if (service == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                service = retrofit.create(WeatherService::class.java)
            }
            return service!!
        }
    }
}