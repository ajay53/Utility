package com.goazi.utility.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_call")
data class Call(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val date: String,
    val duration: String
)