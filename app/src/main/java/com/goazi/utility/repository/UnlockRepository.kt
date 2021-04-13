package com.goazi.utility.repository

import androidx.lifecycle.LiveData
import com.goazi.utility.repository.cache.dao.UnlockDao
import com.goazi.utility.model.Unlock

class UnlockRepository(private val unlockDao: UnlockDao) {
    val getAllUnlocks: LiveData<MutableList<Unlock>> = unlockDao.getAllUnlocks()

    suspend fun insert(unlock: Unlock) {
        unlockDao.insertSuspended(unlock)
    }
}