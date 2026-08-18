package com.neohear.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.neohear.data.entity.Patient
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface PatientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(patient: Patient)

    @Update
    suspend fun update(patient: Patient)

    @Delete
    suspend fun delete(patient: Patient)

    @Query("SELECT * FROM patients WHERE id = :id")
    suspend fun getById(id: UUID): Patient?

    @Query("SELECT * FROM patients ORDER BY createdAt DESC")
    fun getAllPatients(): Flow<List<Patient>>
}
