package com.goazi.utility.data

import androidx.lifecycle.LiveData
import com.goazi.utility.data.cache.dao.CallDao
import com.goazi.utility.model.Call

class CallRepository(private val callDao: CallDao) {
    private val TAG = "CallRepository"

    val getAllCalls: LiveData<MutableList<Call>> = callDao.getAllCalls()

    suspend fun insert(call: Call) {
        callDao.insert(call)
    }

}