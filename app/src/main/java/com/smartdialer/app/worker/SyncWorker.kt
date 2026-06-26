package com.smartdialer.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.smartdialer.app.data.local.dao.CallLogDao
import com.smartdialer.app.data.local.dao.ContactDao
import com.smartdialer.app.data.local.dao.NoteAndReminderDao
import com.smartdialer.app.data.remote.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val api: SmartDialerApi,
    private val contactDao: ContactDao,
    private val callLogDao: CallLogDao,
    private val noteDao: NoteAndReminderDao
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // In a real app, you'd get the auth token from DataStore/SharedPreferences
            val dummyToken = "Bearer dummy_token"

            // 1. Gather all local data
            val contacts = contactDao.getAllContacts().first().map { entity ->
                ContactBackupDto(
                    name = entity.name,
                    nickname = entity.nickname,
                    phone_numbers_json = entity.phoneNumbersJson,
                    emails_json = entity.emailsJson,
                    address = entity.address,
                    company = entity.company,
                    photo_uri = entity.photoUri,
                    is_favorite = entity.isFavorite,
                    group_name = entity.groupName,
                    notes = entity.notes,
                    birthday = entity.birthday,
                    device_contact_id = entity.deviceContactId,
                    created_at = entity.createdAt,
                    updated_at = entity.updatedAt
                )
            }

            val callLogs = callLogDao.getAllCallLogs().first().map { entity ->
                CallLogBackupDto(
                    contact_name = entity.contactName,
                    number = entity.number,
                    formatted_number = entity.formattedNumber,
                    call_type = entity.callType,
                    duration = entity.duration,
                    timestamp = entity.timestamp,
                    sim_slot = entity.simSlot
                )
            }

            val notes = noteDao.getAllNotes().first().map { entity ->
                NoteBackupDto(
                    contact_id = entity.contactId,
                    content = entity.content,
                    call_summary = entity.callSummary,
                    ai_tags = entity.aiTags,
                    created_at = entity.createdAt,
                    updated_at = entity.updatedAt
                )
            }

            // 2. Prepare Sync Request
            val request = SyncRequest(
                contacts = contacts,
                call_logs = callLogs,
                notes = notes
            )

            // 3. Send to Backend
            api.syncData(dummyToken, request)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            // Retry if it's a network issue
            Result.retry()
        }
    }
}
