package de.syntax.androidabschluss.data.datamodels.todomodels

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "todo_table")
data class ToDo(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val titel: String,

    val text: String,

    val creationDateTime: String,

    var favorite: Boolean = false,

    var isSelected: Boolean = false,

    var isImportant: Boolean = false,

    var audioFilePath: String? = null,

    var reminderDateTime: Long? = null,

)


