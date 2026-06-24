package com.smartdialer.app.domain.repository

import com.smartdialer.app.domain.model.CallLogEntry
import com.smartdialer.app.domain.model.CallType
import com.smartdialer.app.domain.model.GroupedCallLog
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for Call Log operations.
 */
interface CallLogRepository {

    /** Observe all call logs, most recent first. */
    fun getAllCallLogs(): Flow<List<CallLogEntry>>

    /** Observe grouped call logs (repeated calls from same number). */
    fun getGroupedCallLogs(): Flow<List<GroupedCallLog>>

    /** Filter call logs by type (incoming, outgoing, missed, etc.). */
    fun getCallLogsByType(type: CallType): Flow<List<CallLogEntry>>

    /** Search call logs by number or contact name. */
    fun searchCallLogs(query: String): Flow<List<CallLogEntry>>

    /** Get call logs for a specific contact. */
    fun getCallLogsForContact(contactId: Long): Flow<List<CallLogEntry>>

    /** Delete specific call log entries. */
    suspend fun deleteCallLogs(ids: List<Long>)

    /** Delete all call logs. */
    suspend fun deleteAllCallLogs()

    /** Get call statistics for analytics. */
    suspend fun getCallStatistics(): CallStatistics

    /** Sync call logs from device. */
    suspend fun syncFromDevice()
}

/**
 * Call statistics for the analytics dashboard.
 */
data class CallStatistics(
    val totalCalls: Int,
    val totalIncoming: Int,
    val totalOutgoing: Int,
    val totalMissed: Int,
    val averageDurationSeconds: Long,
    val longestCallSeconds: Long,
    val callsByDayOfWeek: Map<Int, Int>, // 1=Mon to 7=Sun
    val callsByHour: Map<Int, Int>, // 0-23
    val topContacts: List<Pair<String, Int>>, // name to call count
    val callsByCategory: Map<String, Int> // Family, Work, Friends
)
