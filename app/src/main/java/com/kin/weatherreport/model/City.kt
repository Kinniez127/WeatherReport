package com.kin.weatherreport.model

import com.google.gson.annotations.SerializedName

data class City(

    @SerializedName("name")
    val name: String = "",

    @SerializedName("lat")
    val lat: Double = 0.0,

    @SerializedName("lon")
    val lon: Double = 0.0,

    @SerializedName("country")
    val country: String = "",
)