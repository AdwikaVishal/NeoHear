package com.neohear.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.neohear.data.entity.CryAnalysis
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface CryAnalysisDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(analysis: CryAnalysis)

    @Query("SELECT * FROM cry_analyses WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getByPatient(patientId: UUID): Flow<List<CryAnalysis>>
}
