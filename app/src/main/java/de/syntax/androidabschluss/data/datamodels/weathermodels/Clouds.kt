package de.syntax.androidabschluss.data.datamodels.weathermodels

import com.squareup.moshi.Json

data class Clouds(
    @Json(name = "all")
    val all: Int
)
