package com.smartdialer.app.data.device

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.CallLog
import android.provider.ContactsContract
import com.smartdialer.app.data.local.entity.CallLogEntity
import com.smartdialer.app.data.local.entity.ContactEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reads contacts and call logs directly from the Android device
 * using ContentResolver + ContactsContract / CallLog providers.
 */
@Singleton
class DeviceDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val contentResolver: ContentResolver get() = context.contentResolver

    // ===== CONTACTS =====

    fun getDeviceContacts(): List<ContactEntity> {
        val contactsMap = mutableMapOf<String, ContactEntity>()

        // 1. Read base contact info
        val contactCursor: Cursor? = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.PHOTO_URI,
                ContactsContract.Contacts.STARRED,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
            ),
            null, null,
            "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} ASC"
        )

        contactCursor?.use { cursor ->
            val idIdx = cursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
            val photoIdx = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)
            val starredIdx = cursor.getColumnIndex(ContactsContract.Contacts.STARRED)
            val hasPhoneIdx = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)

            while (cursor.moveToNext()) {
                val contactId = cursor.getString(idIdx) ?: continue
                val name = cursor.getString(nameIdx) ?: "Unknown"
                val photoUri = if (photoIdx >= 0) cursor.getString(photoIdx) else null
                val starred = if (starredIdx >= 0) cursor.getInt(starredIdx) == 1 else false
                val hasPhone = if (hasPhoneIdx >= 0) cursor.getInt(hasPhoneIdx) > 0 else false

                if (!hasPhone) continue

                contactsMap[contactId] = ContactEntity(
                    name = name,
                    photoUri = photoUri,
                    isFavorite = starred,
                    deviceContactId = contactId,
                    phoneNumbersJson = "[]",
                    emailsJson = "[]"
                )
            }
        }

        // 2. Read phone numbers
        val phoneCursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE
            ),
            null, null, null
        )

        // Collect phone numbers per contact
        val phoneMap = mutableMapOf<String, MutableList<Pair<String, String>>>()
        phoneCursor?.use { cursor ->
            val contactIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val numberIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val typeIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)

            while (cursor.moveToNext()) {
                val contactId = cursor.getString(contactIdIdx) ?: continue
                val number = cursor.getString(numberIdx) ?: continue
                val type = if (typeIdx >= 0) cursor.getInt(typeIdx) else ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE

                val label = when (type) {
                    ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> "HOME"
                    ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> "WORK"
                    ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME,
                    ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK -> "FAX"
                    else -> "MOBILE"
                }

                phoneMap.getOrPut(contactId) { mutableListOf() }.add(number to label)
            }
        }

        // 3. Read emails
        val emailCursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Email.CONTACT_ID,
                ContactsContract.CommonDataKinds.Email.ADDRESS
            ),
            null, null, null
        )

        val emailMap = mutableMapOf<String, MutableList<String>>()
        emailCursor?.use { cursor ->
            val contactIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID)
            val emailIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)

            while (cursor.moveToNext()) {
                val contactId = cursor.getString(contactIdIdx) ?: continue
                val email = cursor.getString(emailIdx) ?: continue
                emailMap.getOrPut(contactId) { mutableListOf() }.add(email)
            }
        }

        // 4. Read organization (company)
        val orgCursor: Cursor? = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            arrayOf(
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.CommonDataKinds.Organization.COMPANY
            ),
            "${ContactsContract.Data.MIMETYPE} = ?",
            arrayOf(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE),
            null
        )

        val companyMap = mutableMapOf<String, String>()
        orgCursor?.use { cursor ->
            val contactIdIdx = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)
            val companyIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY)

            while (cursor.moveToNext()) {
                val contactId = cursor.getString(contactIdIdx) ?: continue
                val company = if (companyIdx >= 0) cursor.getString(companyIdx) else null
                if (!company.isNullOrBlank()) {
                    companyMap[contactId] = company
                }
            }
        }

        // 5. Read nickname
        val nickCursor: Cursor? = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            arrayOf(
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.CommonDataKinds.Nickname.NAME
            ),
            "${ContactsContract.Data.MIMETYPE} = ?",
            arrayOf(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE),
            null
        )

        val nicknameMap = mutableMapOf<String, String>()
        nickCursor?.use { cursor ->
            val contactIdIdx = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)
            val nickIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME)

            while (cursor.moveToNext()) {
                val contactId = cursor.getString(contactIdIdx) ?: continue
                val nick = if (nickIdx >= 0) cursor.getString(nickIdx) else null
                if (!nick.isNullOrBlank()) {
                    nicknameMap[contactId] = nick
                }
            }
        }

        // 6. Merge everything
        return contactsMap.map { (deviceId, entity) ->
            val phones = phoneMap[deviceId] ?: emptyList()
            val phoneJson = JSONArray().apply {
                phones.forEach { (number, label) ->
                    put(JSONObject().apply {
                        put("number", number)
                        put("label", label)
                    })
                }
            }.toString()

            val emails = emailMap[deviceId] ?: emptyList()
            val emailJson = JSONArray().apply {
                emails.forEach { put(it) }
            }.toString()

            entity.copy(
                phoneNumbersJson = phoneJson,
                emailsJson = emailJson,
                company = companyMap[deviceId],
                nickname = nicknameMap[deviceId]
            )
        }
    }

    // ===== CALL LOGS =====

    fun getDeviceCallLogs(limit: Int = 500): List<CallLogEntity> {
        val callLogs = mutableListOf<CallLogEntity>()

        val cursor: Cursor? = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            arrayOf(
                CallLog.Calls._ID,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.NUMBER,
                CallLog.Calls.CACHED_FORMATTED_NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DURATION,
                CallLog.Calls.DATE,
                CallLog.Calls.IS_READ,
                CallLog.Calls.PHONE_ACCOUNT_ID,
                CallLog.Calls.CACHED_PHOTO_URI
            ),
            null, null,
            "${CallLog.Calls.DATE} DESC"
        )

        cursor?.use { c ->
            val idIdx = c.getColumnIndex(CallLog.Calls._ID)
            val nameIdx = c.getColumnIndex(CallLog.Calls.CACHED_NAME)
            val numberIdx = c.getColumnIndex(CallLog.Calls.NUMBER)
            val formattedIdx = c.getColumnIndex(CallLog.Calls.CACHED_FORMATTED_NUMBER)
            val typeIdx = c.getColumnIndex(CallLog.Calls.TYPE)
            val durationIdx = c.getColumnIndex(CallLog.Calls.DURATION)
            val dateIdx = c.getColumnIndex(CallLog.Calls.DATE)
            val isReadIdx = c.getColumnIndex(CallLog.Calls.IS_READ)
            val simIdx = c.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID)
            val photoIdx = c.getColumnIndex(CallLog.Calls.CACHED_PHOTO_URI)

            var count = 0
            while (c.moveToNext() && count < limit) {
                val type = if (typeIdx >= 0) c.getInt(typeIdx) else CallLog.Calls.INCOMING_TYPE

                val callType = when (type) {
                    CallLog.Calls.INCOMING_TYPE -> "INCOMING"
                    CallLog.Calls.OUTGOING_TYPE -> "OUTGOING"
                    CallLog.Calls.MISSED_TYPE -> "MISSED"
                    CallLog.Calls.REJECTED_TYPE -> "REJECTED"
                    CallLog.Calls.BLOCKED_TYPE -> "BLOCKED"
                    CallLog.Calls.VOICEMAIL_TYPE -> "VOICEMAIL"
                    else -> "INCOMING"
                }

                // Try to determine SIM slot (0 or 1)
                val simSlot = try {
                    val simId = if (simIdx >= 0) c.getString(simIdx) else null
                    if (simId != null && simId.contains("1")) 1 else 0
                } catch (e: Exception) { 0 }

                callLogs.add(
                    CallLogEntity(
                        contactName = if (nameIdx >= 0) c.getString(nameIdx) else null,
                        number = if (numberIdx >= 0) c.getString(numberIdx) ?: "" else "",
                        formattedNumber = if (formattedIdx >= 0) c.getString(formattedIdx) else null,
                        type = callType,
                        duration = if (durationIdx >= 0) c.getLong(durationIdx) else 0,
                        timestamp = if (dateIdx >= 0) c.getLong(dateIdx) else 0,
                        isRead = if (isReadIdx >= 0) c.getInt(isReadIdx) == 1 else true,
                        simSlot = simSlot,
                        photoUri = if (photoIdx >= 0) c.getString(photoIdx) else null
                    )
                )
                count++
            }
        }

        return callLogs
    }
}
