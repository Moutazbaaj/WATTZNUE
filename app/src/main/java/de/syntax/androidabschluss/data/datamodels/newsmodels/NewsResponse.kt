package de.syntax.androidabschluss.data.datamodels.newsmodels

import com.squareup.moshi.Json

data class NewsResponse(
    @Json(name = "status")
    val status: String,

    @Json(name = "totalResults")
    val totalResults: Int,

    @Json(name = "articles")
    val articles: List<Article>,

    @Json(name = "sources")
    val sources: List<Source>?
)
