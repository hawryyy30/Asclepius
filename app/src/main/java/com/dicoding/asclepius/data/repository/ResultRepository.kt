package com.dicoding.asclepius.data.repository

import androidx.lifecycle.LiveData
import com.dicoding.asclepius.data.local.ClassificationVerdict
import com.dicoding.asclepius.data.local.DAO

class ResultRepository(private val dao: DAO) {
    fun fetchAll(): LiveData<List<ClassificationVerdict>> {
        return dao.fetchAll()
    }

    suspend fun insertVerdict(classificationVerdict: ClassificationVerdict) {
        dao.insertVerdict(classificationVerdict)
    }

    suspend fun deleteVerdict(classificationVerdict: ClassificationVerdict) {
        dao.deleteVerdict(classificationVerdict)
    }

    companion object {
        @Volatile
        private var INSTANCE: ResultRepository? = null

        fun getInstance(
            dao: DAO,
        ): ResultRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ResultRepository(dao)
            }.also { INSTANCE = it }
    }
}