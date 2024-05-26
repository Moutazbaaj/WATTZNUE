package de.syntax.androidabschluss.data.datamodels.weathermodels

import com.squareup.moshi.Json

data class Wind(

    @Json(name = "speed")
    val speed: Double,

    @Json(name = "deg")
    val deg: Int,

    @Json(name = "gust")
    val gust: Double?
)