package com.goazi.utility.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_unlock")
data class Unlock(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val path: String
)
