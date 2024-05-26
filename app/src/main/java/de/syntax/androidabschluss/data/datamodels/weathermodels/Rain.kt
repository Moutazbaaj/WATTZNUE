package de.syntax.androidabschluss.data.datamodels.weathermodels

import com.squareup.moshi.Json

class Rain (
    @Json(name = "rain.1h")
    val rain1 : Int?,

    @Json(name = "rain.3h")
    val rain3 : Int?,

    )