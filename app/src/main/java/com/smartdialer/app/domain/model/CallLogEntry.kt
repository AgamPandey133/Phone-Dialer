package com.smartdialer.app.domain.model

/**
 * Domain model representing a call log entry.
 */
data class CallLogEntry(
    val id: Long = 0,
    val contactId: Long? = null,
    val contactName: String? = null,
    val number: String,
    val formattedNumber: String? = null,
    val type: CallType,
    val duration: Long = 0, // in seconds
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = true,
    val simSlot: Int = 0, // 0 or 1 for dual SIM
    val location: String? = null,
    val photoUri: String? = null
)

enum class CallType {
    INCOMING,
    OUTGOING,
    MISSED,
    REJECTED,
    BLOCKED,
    VOICEMAIL
}

/**
 * Groups repeated calls from the same number together.
 */
data class GroupedCallLog(
    val number: String,
    val contactName: String? = null,
    val photoUri: String? = null,
    val calls: List<CallLogEntry>,
    val lastCallTimestamp: Long,
    val totalCalls: Int
) {
    val lastCallType: CallType get() = calls.first().type
    val totalDuration: Long get() = calls.sumOf { it.duration }
}
