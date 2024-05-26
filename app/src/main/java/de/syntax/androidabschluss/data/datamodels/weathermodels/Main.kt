package de.syntax.androidabschluss.data.datamodels.weathermodels

import com.squareup.moshi.Json

data class Main(
    @Json(name = "temp")
    val temp: Double,

    @Json(name = "humidity")
    val humidity: Int,

    @Json(name = "temp_min")
    val temp_min: Double,

    @Json(name = "temp_max")
    val temp_max: Double,

    @Json(name = "pressure")
    val pressure: Int,
)