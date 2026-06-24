package com.smartdialer.app.di

import android.content.Context
import androidx.room.Room
import com.smartdialer.app.data.local.SmartDialerDatabase
import com.smartdialer.app.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing Room database and DAO instances.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SmartDialerDatabase {
        return Room.databaseBuilder(
            context,
            SmartDialerDatabase::class.java,
            SmartDialerDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideContactDao(db: SmartDialerDatabase): ContactDao = db.contactDao()

    @Provides
    fun provideCallLogDao(db: SmartDialerDatabase): CallLogDao = db.callLogDao()

    @Provides
    fun provideNoteDao(db: SmartDialerDatabase): NoteDao = db.noteDao()

    @Provides
    fun provideReminderDao(db: SmartDialerDatabase): ReminderDao = db.reminderDao()

    @Provides
    fun provideSpeedDialDao(db: SmartDialerDatabase): SpeedDialDao = db.speedDialDao()
}
