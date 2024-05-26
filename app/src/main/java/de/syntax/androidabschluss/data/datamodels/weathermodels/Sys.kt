package de.syntax.androidabschluss.data.datamodels.weathermodels

import com.squareup.moshi.Json

data class Sys(
    @Json(name = "type")
    val type: Int,

    @Json(name = "id")
    val id: Int,

    @Json(name = "message")
    val message: Double?,

    @Json(name = "country")
    val country: String,

    @Json(name = "sunrise")
    val sunrise: Int,

    @Json(name = "sunset")
    val sunset: Int
)
