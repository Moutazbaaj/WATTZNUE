package de.syntax.androidabschluss.data.local.profileDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.syntax.androidabschluss.data.datamodels.profilemodels.Profile


// Database class representing the local SQLite database
@Database(entities = [Profile::class,], version = 1)
abstract class ProfileDatabase : RoomDatabase() {
    abstract val profileDatabaseDao: ProfileDatabaseDao

}



// Singleton pattern to ensure only one instance of the database is created
private lateinit var INSTANCE: ProfileDatabase


// Function to get an instance of the database
fun getDatabaseProfile(context: Context): ProfileDatabase {
    synchronized(ProfileDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                ProfileDatabase::class.java,
                "profile_Database"
            ).build() //  .addMigrations(MIGRATION)  //.fallbackToDestructiveMigration()
        }
    }
    return INSTANCE
}
