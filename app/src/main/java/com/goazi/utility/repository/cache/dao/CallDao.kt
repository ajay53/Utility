package com.goazi.utility.repository.cache.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.goazi.utility.model.Call

@Dao
interface CallDao {

    @Insert
    suspend fun insert(call: Call)

    @Query("Select * from tbl_call order by id desc")
    fun getAllCalls():LiveData<MutableList<Call>>
}