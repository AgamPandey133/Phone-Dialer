package com.smartdialer.app.domain.model

/**
 * Domain model for a note attached to a contact.
 */
data class ContactNote(
    val id: Long = 0,
    val contactId: Long,
    val contactName: String? = null,
    val content: String,
    val summary: String? = null, // AI-generated summary
    val importantPoints: List<String> = emptyList(),
    val extractedTasks: List<ExtractedTask> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val category: NoteCategory = NoteCategory.GENERAL
)

enum class NoteCategory {
    GENERAL, MEETING, FOLLOW_UP, BIRTHDAY, REMINDER
}

/**
 * Task extracted by AI from conversation notes.
 */
data class ExtractedTask(
    val description: String,
    val dueDate: String? = null,
    val isCompleted: Boolean = false
)

/**
 * Domain model for a smart reminder.
 */
data class SmartReminder(
    val id: Long = 0,
    val contactId: Long? = null,
    val contactName: String? = null,
    val title: String,
    val description: String? = null,
    val triggerTime: Long,
    val isRecurring: Boolean = false,
    val recurringIntervalMs: Long? = null,
    val isCompleted: Boolean = false,
    val type: ReminderType = ReminderType.MISSED_CALL
)

enum class ReminderType {
    MISSED_CALL, FOLLOW_UP, BIRTHDAY, CUSTOM, TASK
}
