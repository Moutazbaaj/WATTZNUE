package de.syntax.androidabschluss.data.local.profileDatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import de.syntax.androidabschluss.data.datamodels.profilemodels.Profile

@Dao
interface ProfileDatabaseDao {

    // Insert a profile into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: Profile)


    @Query("UPDATE profile_table SET userName = :newUserName, profileImgUri = :newProfileImgUri WHERE id = :profileId")
    suspend fun updateProfile(profileId: Int, newUserName: String, newProfileImgUri: String?)

    // Get all profiles from the database
    @Query("SELECT * FROM profile_table")
    fun getAll(): LiveData<List<Profile>>


    // Delete all profiles from the database
    @Query("DELETE FROM profile_table")
    suspend fun deleteAll()
}