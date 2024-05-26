package de.syntax.androidabschluss.data.datamodels.weathermodels

import com.squareup.moshi.Json

class Snow(
    @Json(name = "snow.1h")
    val snow1 : Int?,

    @Json(name = "snow.3h")
    val snow3 : Int?,

)