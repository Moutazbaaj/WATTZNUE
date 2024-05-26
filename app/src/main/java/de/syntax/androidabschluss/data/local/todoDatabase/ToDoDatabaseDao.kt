package de.syntax.androidabschluss.data.local.todoDatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import de.syntax.androidabschluss.data.datamodels.todomodels.ToDo

@Dao
interface ToDoDatabaseDao {

    // Insert a note into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(toDo: ToDo)

    @Update
    suspend fun update(toDo: ToDo)

    // Get all notes from the database
    @Query("SELECT * FROM todo_table ORDER BY creationDateTime DESC")
    fun getAll(): LiveData<List<ToDo>>



    // Update the isSelected state of a To.Do item in the database
    @Query("UPDATE todo_table SET isSelected = :isSelected WHERE id = :id")
    suspend fun updateSelection(id: Int, isSelected: Boolean)


    // Update the isSelected state of a To.Do item in the database
    @Query("UPDATE todo_table SET favorite = :favorite WHERE id = :id")
    suspend fun updateFavorite(id: Int, favorite: Boolean)


    // Update the isSelected state of a To.Do item in the database
    @Query("UPDATE todo_table SET isImportant = :isImportant WHERE id = :id")
    suspend fun updateImportant(id: Int, isImportant: Boolean)


    // Delete a note from the database by its ID
    @Query("DELETE FROM todo_table WHERE id = :id")
    suspend fun deleteById(id: Int)


    // Delete a note if it's Important from the database
    @Query("DELETE FROM todo_table WHERE isImportant = :isImportant")
    suspend fun deleteImportant(isImportant: Boolean)


    // Delete a note if it's favorite from the database
    @Query("DELETE FROM todo_table WHERE favorite = :favorite")
    suspend fun deleteFavorite(favorite: Boolean)


    // Delete a note if it's Done from the database
    @Query("DELETE FROM todo_table WHERE isSelected = :isSelected")
    suspend fun deleteDone(isSelected: Boolean)



    // Delete all notes from the database
    @Query("DELETE FROM todo_table")
    suspend fun deleteAll()
}