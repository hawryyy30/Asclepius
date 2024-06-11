package com.dicoding.asclepius.view.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.local.ClassificationVerdict
import com.dicoding.asclepius.data.repository.ResultRepository
import kotlinx.coroutines.launch

class ResultViewModel(private val repo: ResultRepository): ViewModel() {
    fun insert(classificationVerdict: ClassificationVerdict){
        viewModelScope.launch { repo.insertVerdict(classificationVerdict) }
    }
    fun delete(classificationVerdict: ClassificationVerdict){
        viewModelScope.launch { repo.deleteVerdict(classificationVerdict) }
    }
}