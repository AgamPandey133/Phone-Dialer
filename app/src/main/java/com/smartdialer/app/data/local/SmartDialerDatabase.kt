package com.smartdialer.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smartdialer.app.data.local.dao.*
import com.smartdialer.app.data.local.entity.*

/**
 * Main Room database for Smart Dialer.
 * Contains all local tables for contacts, call logs, notes, reminders, and speed dials.
 */
@Database(
    entities = [
        ContactEntity::class,
        CallLogEntity::class,
        ContactNoteEntity::class,
        ReminderEntity::class,
        SpeedDialEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class SmartDialerDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao
    abstract fun callLogDao(): CallLogDao
    abstract fun noteDao(): NoteDao
    abstract fun reminderDao(): ReminderDao
    abstract fun speedDialDao(): SpeedDialDao

    companion object {
        const val DATABASE_NAME = "smart_dialer_db"
    }
}
