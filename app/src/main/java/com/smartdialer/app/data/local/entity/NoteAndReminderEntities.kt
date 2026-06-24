package com.smartdialer.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for contact notes.
 */
@Entity(tableName = "contact_notes")
data class ContactNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contactId: Long,
    val contactName: String? = null,
    val content: String,
    val summary: String? = null,
    val importantPointsJson: String = "[]",
    val extractedTasksJson: String = "[]",
    val timestamp: Long = System.currentTimeMillis(),
    val category: String = "GENERAL"
)

/**
 * Room entity for smart reminders.
 */
@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contactId: Long? = null,
    val contactName: String? = null,
    val title: String,
    val description: String? = null,
    val triggerTime: Long,
    val isRecurring: Boolean = false,
    val recurringIntervalMs: Long? = null,
    val isCompleted: Boolean = false,
    val type: String = "MISSED_CALL"
)

/**
 * Room entity for speed dial entries.
 */
@Entity(tableName = "speed_dials")
data class SpeedDialEntity(
    @PrimaryKey
    val position: Int, // 2-9 on dial pad
    val contactId: Long,
    val contactName: String,
    val phoneNumber: String,
    val photoUri: String? = null
)
