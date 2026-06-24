package com.smartdialer.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for cached call logs.
 */
@Entity(tableName = "call_logs")
data class CallLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contactId: Long? = null,
    val contactName: String? = null,
    val number: String,
    val formattedNumber: String? = null,
    val type: String, // INCOMING, OUTGOING, MISSED, REJECTED, BLOCKED, VOICEMAIL
    val duration: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = true,
    val simSlot: Int = 0,
    val location: String? = null,
    val photoUri: String? = null
)
