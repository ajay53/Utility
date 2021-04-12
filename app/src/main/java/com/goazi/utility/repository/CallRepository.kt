package com.goazi.utility.repository

import androidx.lifecycle.LiveData
import com.goazi.utility.repository.cache.dao.CallDao
import com.goazi.utility.model.Call

class CallRepository(private val callDao: CallDao) {
    val getAllCalls: LiveData<MutableList<Call>> = callDao.getAllCalls()

    suspend fun insert(call: Call) {
        callDao.insert(call)
    }
}