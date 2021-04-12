package com.goazi.utility.repository.cache.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.goazi.utility.model.Unlock

@Dao
interface UnlockDao {

    @Insert
    suspend fun insert(unlock: Unlock)

    @Query("Select * from tbl_unlock order by id desc")
    fun getAllUnlocks(): LiveData<MutableList<Unlock>>
}