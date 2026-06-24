package com.smartdialer.app.data.local.dao

import androidx.room.*
import com.smartdialer.app.data.local.entity.ContactNoteEntity
import com.smartdialer.app.data.local.entity.ReminderEntity
import com.smartdialer.app.data.local.entity.SpeedDialEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Contact Notes.
 */
@Dao
interface NoteDao {

    @Query("SELECT * FROM contact_notes WHERE contactId = :contactId ORDER BY timestamp DESC")
    fun getNotesForContact(contactId: Long): Flow<List<ContactNoteEntity>>

    @Query("SELECT * FROM contact_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<ContactNoteEntity>>

    @Query("""
        SELECT * FROM contact_notes 
        WHERE content LIKE '%' || :query || '%' 
            OR summary LIKE '%' || :query || '%'
            OR contactName LIKE '%' || :query || '%'
        ORDER BY timestamp DESC
    """)
    fun searchNotes(query: String): Flow<List<ContactNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: ContactNoteEntity): Long

    @Update
    suspend fun updateNote(note: ContactNoteEntity)

    @Query("DELETE FROM contact_notes WHERE id = :id")
    suspend fun deleteNote(id: Long)
}

/**
 * Data Access Object for Reminders.
 */
@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders WHERE isCompleted = 0 ORDER BY triggerTime ASC")
    fun getActiveReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders ORDER BY triggerTime DESC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE triggerTime <= :currentTime AND isCompleted = 0")
    suspend fun getDueReminders(currentTime: Long = System.currentTimeMillis()): List<ReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Query("UPDATE reminders SET isCompleted = 1 WHERE id = :id")
    suspend fun markCompleted(id: Long)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminder(id: Long)
}

/**
 * Data Access Object for Speed Dials.
 */
@Dao
interface SpeedDialDao {

    @Query("SELECT * FROM speed_dials ORDER BY position ASC")
    fun getAllSpeedDials(): Flow<List<SpeedDialEntity>>

    @Query("SELECT * FROM speed_dials WHERE position = :position")
    suspend fun getSpeedDial(position: Int): SpeedDialEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpeedDial(speedDial: SpeedDialEntity)

    @Query("DELETE FROM speed_dials WHERE position = :position")
    suspend fun deleteSpeedDial(position: Int)
}
