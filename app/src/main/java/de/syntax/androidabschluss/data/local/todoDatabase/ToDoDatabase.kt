package de.syntax.androidabschluss.data.local.todoDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.syntax.androidabschluss.data.datamodels.todomodels.ToDo

// Database class representing the local SQLite database
@Database(entities = [ToDo::class], version = 12)
abstract class ToDoDatabase : RoomDatabase() {
    abstract val toDoDatabaseDao: ToDoDatabaseDao

}



// Singleton pattern to ensure only one instance of the database is created
private lateinit var INSTANCE: ToDoDatabase


// Function to get an instance of the database
fun getDatabaseToDo(context: Context): ToDoDatabase {
    synchronized(ToDoDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                ToDoDatabase::class.java,
                "todo_Database"
            ).fallbackToDestructiveMigration().build() //  .addMigrations(MIGRATION)  //.fallbackToDestructiveMigration()
        }
    }
    return INSTANCE
}
