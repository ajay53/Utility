package com.goazi.utility.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.goazi.utility.repository.UnlockRepository
import com.goazi.utility.repository.cache.DatabaseHandler
import com.goazi.utility.repository.cache.dao.UnlockDao
import com.goazi.utility.model.Unlock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UnlockViewModel(application: Application) : AndroidViewModel(application) {
    private val unlockDao: UnlockDao = DatabaseHandler.getInstance(application)!!.unlockDao()
    private val repository: UnlockRepository = UnlockRepository(unlockDao)
    val getAllUnlocks: LiveData<MutableList<Unlock>> = repository.getAllUnlocks

    fun insert(unlock: Unlock) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(unlock)
        }
    }
}