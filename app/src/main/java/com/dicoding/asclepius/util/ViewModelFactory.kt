package com.dicoding.asclepius.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.repository.ResultRepository
import com.dicoding.asclepius.helper.Injection
import com.dicoding.asclepius.view.history.HistoryViewModel
import com.dicoding.asclepius.view.result.ArticlesViewModel
import com.dicoding.asclepius.view.result.ResultViewModel

class ViewModelFactory private constructor(
    private val repository: ResultRepository,
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HistoryViewModel::class.java) ->
                HistoryViewModel(repository) as T

            modelClass.isAssignableFrom(ResultViewModel::class.java) ->
                ResultViewModel(repository) as T

            modelClass.isAssignableFrom(ArticlesViewModel::class.java) ->
                ArticlesViewModel() as T

            else -> throw IllegalArgumentException(context.getString(R.string.view_model_invalid))
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun fetchInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: createFactory(context).also { INSTANCE = it }
            }
        }

        private fun createFactory(context: Context): ViewModelFactory {
            val repository = Injection.provideRepository(context)
            return ViewModelFactory(repository, context)
        }
    }
}
