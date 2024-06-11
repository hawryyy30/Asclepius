package com.dicoding.asclepius.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DAO {
    @Query("SELECT * FROM classificationVerdict ORDER BY time DESC")
    fun fetchAll(): LiveData<List<ClassificationVerdict>>

    @Query("SELECT * FROM classificationVerdict WHERE id = :id")
    fun fetchClassificationVerdictById(id: Int): LiveData<ClassificationVerdict>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerdict(vararg classificationVerdict: ClassificationVerdict)

    @Delete
    suspend fun deleteVerdict(classificationVerdict: ClassificationVerdict)
}