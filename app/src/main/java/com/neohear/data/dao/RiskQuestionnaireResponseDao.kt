package com.neohear.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.neohear.data.entity.RiskQuestionnaireResponse
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface RiskQuestionnaireResponseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(response: RiskQuestionnaireResponse)

    @Update
    suspend fun update(response: RiskQuestionnaireResponse)

    @Delete
    suspend fun delete(response: RiskQuestionnaireResponse)

    @Query("SELECT * FROM risk_questionnaire_responses WHERE id = :id")
    suspend fun getById(id: UUID): RiskQuestionnaireResponse?

    @Query("SELECT * FROM risk_questionnaire_responses WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getResponsesForPatient(patientId: UUID): Flow<List<RiskQuestionnaireResponse>>
}
