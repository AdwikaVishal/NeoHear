package com.neohear.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.neohear.data.entity.Mode
import com.neohear.data.entity.ReferralStatus
import com.neohear.data.entity.TestResult
import kotlinx.coroutines.flow.Flow

data class DashboardCounts(
    val totalTests: Int,
    val passCount: Int,
    val referCount: Int,
    val pendingReferrals: Int,
    val resolvedReferrals: Int
)

data class DailyTestCount(
    val dayStart: Long,
    val passCount: Int,
    val referCount: Int
)

data class ModeCount(
    val mode: Mode,
    val count: Int
)

@Dao
interface DashboardDao {

    @Query(
        """
        SELECT
            (SELECT COUNT(*) FROM test_sessions WHERE timestamp BETWEEN :start AND :end) AS totalTests,
            (SELECT COUNT(*) FROM test_sessions WHERE result = 'PASS' AND timestamp BETWEEN :start AND :end) AS passCount,
            (SELECT COUNT(*) FROM test_sessions WHERE result = 'REFER' AND timestamp BETWEEN :start AND :end) AS referCount,
            (SELECT COUNT(*) FROM referrals WHERE status != 'COMPLETED') AS pendingReferrals,
            (SELECT COUNT(*) FROM referrals WHERE status = 'COMPLETED') AS resolvedReferrals
        """
    )
    suspend fun getDashboardCounts(start: Long, end: Long): DashboardCounts?

    // ── Reactive queries ─────────────────────────────────────────────

    /** All test sessions — for live aggregation in the ViewModel. */
    @Query("SELECT * FROM test_sessions ORDER BY timestamp ASC")
    fun observeAllTestSessions(): Flow<List<com.neohear.data.entity.TestSession>>

    /** All referrals — for live referral counts. */
    @Query("SELECT * FROM referrals ORDER BY createdAt ASC")
    fun observeAllReferrals(): Flow<List<com.neohear.data.entity.Referral>>

    /** Mode breakdown (PROBE vs RISK_QUESTIONNAIRE) within a time window. */
    @Query(
        """
        SELECT mode, COUNT(*) AS count
        FROM test_sessions
        WHERE timestamp BETWEEN :start AND :end
        GROUP BY mode
        """
    )
    fun observeModeCounts(start: Long, end: Long): Flow<List<ModeCount>>

    /** Pass/refer counts grouped by day for the last N days (for the bar chart). */
    @Query(
        """
        SELECT
            (timestamp / 86400000) * 86400000 AS dayStart,
            SUM(CASE WHEN result = 'PASS' THEN 1 ELSE 0 END) AS passCount,
            SUM(CASE WHEN result = 'REFER' THEN 1 ELSE 0 END) AS referCount
        FROM test_sessions
        WHERE timestamp >= :since
        GROUP BY dayStart
        ORDER BY dayStart ASC
        """
    )
    fun observeDailyTestCounts(since: Long): Flow<List<DailyTestCount>>
}
