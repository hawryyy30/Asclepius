package com.dicoding.asclepius.view.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.local.ClassificationVerdict
import com.dicoding.asclepius.data.repository.ResultRepository
import kotlinx.coroutines.launch

class HistoryViewModel(private val repo: ResultRepository) : ViewModel() {
    private val verdicts = MutableLiveData<List<ClassificationVerdict>>()
    val results: LiveData<List<ClassificationVerdict>> = verdicts

    init {
        viewModelScope.launch {
            repo.fetchAll().observeForever {
                verdicts.postValue(it)
            }
        }
    }
}