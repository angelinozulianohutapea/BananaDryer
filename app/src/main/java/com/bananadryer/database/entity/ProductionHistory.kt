package com.bananadryer.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "production_history")
data class ProductionHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tanggal: String = "",
    val jamMulai: String = "",
    val jamSelesai: String = "",
    val tb1: Long = 0L,
    val tb2: Long = 0L,
    val dc: Int = 0,
    val suhuAkhir: Float = 0f,
    val kelembabanAkhir: Float = 0f,
    val status: String = ""
)