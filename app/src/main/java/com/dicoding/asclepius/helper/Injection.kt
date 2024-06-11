package com.dicoding.asclepius.helper

import android.content.Context
import com.dicoding.asclepius.data.local.AppDB
import com.dicoding.asclepius.data.repository.ResultRepository

object Injection {

    fun provideRepository(context: Context): ResultRepository{
        val appDB = AppDB.getDatabase(context)
        val dao = appDB.useDao()
        return ResultRepository.getInstance(dao)
    }
}