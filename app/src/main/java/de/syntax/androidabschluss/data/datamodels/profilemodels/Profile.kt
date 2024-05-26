package de.syntax.androidabschluss.data.datamodels.profilemodels

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_table")
data class Profile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userName: String,

    val profileImgUri: String?,

    )
