package com.goazi.utility.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.goazi.utility.data.CallRepository
import com.goazi.utility.data.cache.DatabaseHandler
import com.goazi.utility.data.cache.dao.CallDao
import com.goazi.utility.model.Call
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CallViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "CallViewModel"

    private val callDao: CallDao = DatabaseHandler.getInstance(application)!!.callDao()
    private val repository: CallRepository = CallRepository(callDao)
    val getAllCalls: LiveData<MutableList<Call>> = repository.getAllCalls

    fun insert(call: Call) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(call)
        }
    }
}