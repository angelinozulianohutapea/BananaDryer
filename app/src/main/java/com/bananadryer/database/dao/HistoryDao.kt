package com.bananadryer.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bananadryer.database.entity.ProductionHistory

@Dao
interface HistoryDao {
    @Query("SELECT * FROM production_history ORDER BY id DESC")
    fun getAllHistory(): LiveData<List<ProductionHistory>>

    @Query("SELECT * FROM production_history WHERE status = 'RUNNING' ORDER BY id DESC LIMIT 1")
    suspend fun getLatestRunning(): ProductionHistory?

    @Query("SELECT * FROM production_history ORDER BY id DESC LIMIT 1")
    suspend fun getLatestHistory(): ProductionHistory?

    @Query("SELECT * FROM production_history WHERE id = :id")
    suspend fun getById(id: Int): ProductionHistory?

    @Insert
    suspend fun insert(history: ProductionHistory)

    @Update
    suspend fun update(history: ProductionHistory)

    @Delete
    suspend fun delete(history: ProductionHistory)
}