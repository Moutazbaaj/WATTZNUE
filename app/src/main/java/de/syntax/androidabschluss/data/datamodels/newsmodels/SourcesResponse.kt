package de.syntax.androidabschluss.data.datamodels.newsmodels

import com.squareup.moshi.Json

class SourcesResponse(
    @Json(name = "status")
    val status: String,

    @Json(name = "sources")
    val sources: List<Source>
)