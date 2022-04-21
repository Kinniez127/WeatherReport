package com.kin.weatherreport.model

import com.google.gson.annotations.SerializedName

data class Weather(
    @SerializedName("coord")
    val coord: Coord? = null,

    @SerializedName("main")
    val main: Main? = null
)

data class Coord(
    @SerializedName("lat")
    val lat: Double = 0.0,

    @SerializedName("lon")
    val lon: Double = 0.0
)

data class Main(
    @SerializedName("temp")
    val temp: Double = 0.0,

    @SerializedName("humidity")
    val humidity: Int = 0
)