package de.syntax.androidabschluss.data.local.newsDatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import de.syntax.androidabschluss.data.datamodels.newsmodels.NewsArticle

@Dao
interface NewsDatabaseDao {
    // Insert article into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(newsArticle: NewsArticle)

    @Update
    suspend fun update(newsArticle: NewsArticle)

    // Get all articles from the database
    @Query("SELECT * FROM news_table")
    fun getAll(): LiveData<List<NewsArticle>>

    // Delete article from the database by its ID
    @Query("DELETE FROM news_table WHERE id = :id")
    suspend fun deleteById(id: Int)



    // Delete all articles from the database
    @Query("DELETE FROM news_table")
    suspend fun deleteAll()
}





