package com.smartdialer.app.data.local.dao

import androidx.room.*
import com.smartdialer.app.data.local.entity.CallLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Call Log operations.
 */
@Dao
interface CallLogDao {

    @Query("SELECT * FROM call_logs ORDER BY timestamp DESC")
    fun getAllCallLogs(): Flow<List<CallLogEntity>>

    @Query("SELECT * FROM call_logs WHERE type = :type ORDER BY timestamp DESC")
    fun getCallLogsByType(type: String): Flow<List<CallLogEntity>>

    @Query("""
        SELECT * FROM call_logs 
        WHERE number LIKE '%' || :query || '%' 
            OR contactName LIKE '%' || :query || '%'
        ORDER BY timestamp DESC
    """)
    fun searchCallLogs(query: String): Flow<List<CallLogEntity>>

    @Query("SELECT * FROM call_logs WHERE contactId = :contactId ORDER BY timestamp DESC")
    fun getCallLogsForContact(contactId: Long): Flow<List<CallLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallLog(callLog: CallLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallLogs(callLogs: List<CallLogEntity>)

    @Query("DELETE FROM call_logs WHERE id IN (:ids)")
    suspend fun deleteCallLogs(ids: List<Long>)

    @Query("DELETE FROM call_logs")
    suspend fun deleteAllCallLogs()

    // Statistics queries
    @Query("SELECT COUNT(*) FROM call_logs")
    suspend fun getTotalCallCount(): Int

    @Query("SELECT COUNT(*) FROM call_logs WHERE type = :type")
    suspend fun getCallCountByType(type: String): Int

    @Query("SELECT AVG(duration) FROM call_logs WHERE duration > 0")
    suspend fun getAverageDuration(): Long?

    @Query("SELECT MAX(duration) FROM call_logs")
    suspend fun getLongestCall(): Long?

    @Query("""
        SELECT contactName, COUNT(*) as callCount 
        FROM call_logs 
        WHERE contactName IS NOT NULL 
        GROUP BY contactName 
        ORDER BY callCount DESC 
        LIMIT 10
    """)
    suspend fun getTopContacts(): List<TopContactResult>
}

/**
 * Result class for top contacts query.
 */
data class TopContactResult(
    val contactName: String,
    val callCount: Int
)
