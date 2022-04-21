package com.kin.weatherreport

import com.kin.weatherreport.network.WeatherService

class WeatherRepository constructor(private val retrofitService: WeatherService) {

    fun getLocation(
        cityName: String,
        limit: Int,
        apiKey: String
    ) = retrofitService.getLocation(cityName, limit, apiKey)

    fun getWeather(
        lat: Double,
        lon: Double,
        unit: String,
        apiKey: String
    ) = retrofitService.getWeather(lat, lon, unit, apiKey)
}