package de.syntax.androidabschluss.data.datamodels.newsmodels

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

data class Source(
    @Json(name = "id")
    val id: String?,

    @Json(name = "name")
    val name: String?,

    @Json(name = "description")
    val description: String?,

    @Json(name = "url")
    val url: String?,

    @Json(name = "category")
    val category: String?,

    @Json(name = "language")
    val language: String?,

    @Json(name = "country")
    val country: String?,
)
