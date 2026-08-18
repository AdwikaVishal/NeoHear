package com.neohear.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.neohear.data.entity.Referral
import com.neohear.data.entity.ReferralStatus
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ReferralDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(referral: Referral)

    @Update
    suspend fun update(referral: Referral)

    @Delete
    suspend fun delete(referral: Referral)

    @Query("SELECT * FROM referrals WHERE id = :id")
    suspend fun getById(id: UUID): Referral?

    @Query("SELECT * FROM referrals WHERE status != 'COMPLETED' ORDER BY createdAt DESC")
    fun getIncompleteReferrals(): Flow<List<Referral>>

    @Query("SELECT * FROM referrals ORDER BY createdAt DESC")
    fun getAllReferrals(): Flow<List<Referral>>

    @Query("SELECT * FROM referrals WHERE status = 'PENDING' AND createdAt <= :cutoffTimestamp")
    suspend fun getPendingReferralsOlderThan(cutoffTimestamp: Long): List<Referral>

    @Query("SELECT * FROM referrals WHERE patientId = :patientId ORDER BY createdAt DESC")
    fun getReferralsForPatient(patientId: UUID): Flow<List<Referral>>
}
