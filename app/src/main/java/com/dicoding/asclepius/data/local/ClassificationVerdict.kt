package com.dicoding.asclepius.data.local

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity
@Parcelize
data class ClassificationVerdict(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo("time") val time: Long,
    @ColumnInfo("image_uri") val imageUri: String,
    @ColumnInfo("label") val label: String,
    @ColumnInfo("confidence") val confidence: Float
) : Parcelable
