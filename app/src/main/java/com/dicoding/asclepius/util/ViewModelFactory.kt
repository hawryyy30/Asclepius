package com.dicoding.asclepius.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.repository.ResultRepository
import com.dicoding.asclepius.helper.Injection
import com.dicoding.asclepius.view.history.HistoryViewModel
import com.dicoding.asclepius.view.result.ResultViewModel
import java.lang.IllegalArgumentException


class ViewModelFactory private constructor(
    private val repository: ResultRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HistoryViewModel::class.java)){
            return HistoryViewModel(repository) as T
        } else if(modelClass.isAssignableFrom(ResultViewModel::class.java)) {
            return ResultViewModel(repository) as T
        }
        throw IllegalArgumentException(context.getString(R.string.view_model_invalid))
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun fetchInstance(context: Context): ViewModelFactory =
            INSTANCE ?: synchronized(this){
                INSTANCE ?: ViewModelFactory(Injection.provideRepository(context), context)
            }
    }
}