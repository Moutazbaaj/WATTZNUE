package de.syntax.androidabschluss.data.datamodels.newsmodels

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "news_table")
data class NewsArticle(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val titel: String,

    val description: String,

    val content: String,

    val author: String,

    val sourceName: String,

    val publishedAt: String,

    val url: String,

    val imgUrl: String
)