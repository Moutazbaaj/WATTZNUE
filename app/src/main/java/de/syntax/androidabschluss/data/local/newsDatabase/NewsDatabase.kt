package de.syntax.androidabschluss.data.local.newsDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.syntax.androidabschluss.data.datamodels.newsmodels.NewsArticle


// Database class representing the local SQLite database
@Database(entities = [NewsArticle::class], version = 2)
abstract class NewsDatabase : RoomDatabase() {
    abstract val newsDatabaseDao: NewsDatabaseDao

}

// Singleton pattern to ensure only one instance of the database is created
private lateinit var INSTANCE: NewsDatabase


// Function to get an instance of the database
fun getDatabaseNews(context: Context): NewsDatabase {
    synchronized(NewsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                NewsDatabase::class.java,
                "article_Database"
            ).fallbackToDestructiveMigration().build() //.fallbackToDestructiveMigration()
        }
    }
    return INSTANCE
}
