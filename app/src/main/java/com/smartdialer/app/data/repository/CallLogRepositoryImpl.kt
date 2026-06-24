package com.smartdialer.app.data.repository

import com.smartdialer.app.data.local.dao.CallLogDao
import com.smartdialer.app.data.mapper.EntityMapper.toDomain
import com.smartdialer.app.domain.model.CallLogEntry
import com.smartdialer.app.domain.model.CallType
import com.smartdialer.app.domain.model.GroupedCallLog
import com.smartdialer.app.domain.repository.CallLogRepository
import com.smartdialer.app.domain.repository.CallStatistics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of [CallLogRepository] backed by Room database.
 */
class CallLogRepositoryImpl @Inject constructor(
    private val callLogDao: CallLogDao
) : CallLogRepository {

    override fun getAllCallLogs(): Flow<List<CallLogEntry>> {
        return callLogDao.getAllCallLogs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getGroupedCallLogs(): Flow<List<GroupedCallLog>> {
        return callLogDao.getAllCallLogs().map { entities ->
            entities.map { it.toDomain() }
                .groupBy { it.number }
                .map { (number, calls) ->
                    val sortedCalls = calls.sortedByDescending { it.timestamp }
                    GroupedCallLog(
                        number = number,
                        contactName = sortedCalls.first().contactName,
                        photoUri = sortedCalls.first().photoUri,
                        calls = sortedCalls,
                        lastCallTimestamp = sortedCalls.first().timestamp,
                        totalCalls = sortedCalls.size
                    )
                }
                .sortedByDescending { it.lastCallTimestamp }
        }
    }

    override fun getCallLogsByType(type: CallType): Flow<List<CallLogEntry>> {
        return callLogDao.getCallLogsByType(type.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchCallLogs(query: String): Flow<List<CallLogEntry>> {
        return callLogDao.searchCallLogs(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getCallLogsForContact(contactId: Long): Flow<List<CallLogEntry>> {
        return callLogDao.getCallLogsForContact(contactId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun deleteCallLogs(ids: List<Long>) {
        callLogDao.deleteCallLogs(ids)
    }

    override suspend fun deleteAllCallLogs() {
        callLogDao.deleteAllCallLogs()
    }

    override suspend fun getCallStatistics(): CallStatistics {
        val total = callLogDao.getTotalCallCount()
        val incoming = callLogDao.getCallCountByType(CallType.INCOMING.name)
        val outgoing = callLogDao.getCallCountByType(CallType.OUTGOING.name)
        val missed = callLogDao.getCallCountByType(CallType.MISSED.name)
        val avgDuration = callLogDao.getAverageDuration() ?: 0L
        val longestCall = callLogDao.getLongestCall() ?: 0L
        val topContacts = callLogDao.getTopContacts().map { it.contactName to it.callCount }

        return CallStatistics(
            totalCalls = total,
            totalIncoming = incoming,
            totalOutgoing = outgoing,
            totalMissed = missed,
            averageDurationSeconds = avgDuration,
            longestCallSeconds = longestCall,
            callsByDayOfWeek = emptyMap(), // TODO: Implement with raw query
            callsByHour = emptyMap(),
            topContacts = topContacts,
            callsByCategory = emptyMap()
        )
    }

    override suspend fun syncFromDevice() {
        // TODO: Phase 2 - Read from device CallLog ContentProvider
    }
}
