package com.neohear.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.neohear.data.entity.TestSession
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface TestSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: TestSession)

    @Update
    suspend fun update(session: TestSession)

    @Delete
    suspend fun delete(session: TestSession)

    @Query("SELECT * FROM test_sessions WHERE id = :id")
    suspend fun getById(id: UUID): TestSession?

    @Query("SELECT * FROM test_sessions WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getSessionsForPatient(patientId: UUID): Flow<List<TestSession>>
}
