package com.smartdialer.app.data.mapper

import com.smartdialer.app.data.local.entity.CallLogEntity
import com.smartdialer.app.data.local.entity.ContactEntity
import com.smartdialer.app.domain.model.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * Mappers between Room entities and domain models.
 * Uses org.json (built into Android) instead of Moshi reflection to avoid dependency issues.
 */
object EntityMapper {

    // ---- Contact Mapping ----

    fun ContactEntity.toDomain(): Contact {
        val phoneNumbers = try {
            val jsonArray = JSONArray(phoneNumbersJson)
            (0 until jsonArray.length()).map { i ->
                val obj = jsonArray.getJSONObject(i)
                PhoneNumber(
                    number = obj.optString("number", ""),
                    label = try {
                        PhoneNumberLabel.valueOf(obj.optString("label", "MOBILE"))
                    } catch (e: Exception) {
                        PhoneNumberLabel.MOBILE
                    }
                )
            }
        } catch (e: Exception) {
            emptyList()
        }

        val emails = try {
            val jsonArray = JSONArray(emailsJson)
            (0 until jsonArray.length()).map { i -> jsonArray.getString(i) }
        } catch (e: Exception) {
            emptyList()
        }

        return Contact(
            id = id,
            name = name,
            nickname = nickname,
            phoneNumbers = phoneNumbers,
            emails = emails,
            address = address,
            company = company,
            photoUri = photoUri,
            isFavorite = isFavorite,
            isHidden = isHidden,
            group = groupName,
            notes = notes,
            birthday = birthday,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    fun Contact.toEntity(): ContactEntity {
        val phoneNumbersJson = try {
            val jsonArray = JSONArray()
            phoneNumbers.forEach { phone ->
                val obj = JSONObject()
                obj.put("number", phone.number)
                obj.put("label", phone.label.name)
                jsonArray.put(obj)
            }
            jsonArray.toString()
        } catch (e: Exception) {
            "[]"
        }

        val emailsJson = try {
            val jsonArray = JSONArray()
            emails.forEach { jsonArray.put(it) }
            jsonArray.toString()
        } catch (e: Exception) {
            "[]"
        }

        return ContactEntity(
            id = id,
            name = name,
            nickname = nickname,
            phoneNumbersJson = phoneNumbersJson,
            emailsJson = emailsJson,
            address = address,
            company = company,
            photoUri = photoUri,
            isFavorite = isFavorite,
            isHidden = isHidden,
            groupName = group,
            notes = notes,
            birthday = birthday,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    // ---- CallLog Mapping ----

    fun CallLogEntity.toDomain(): CallLogEntry {
        return CallLogEntry(
            id = id,
            contactId = contactId,
            contactName = contactName,
            number = number,
            formattedNumber = formattedNumber,
            type = try { CallType.valueOf(type) } catch (e: Exception) { CallType.INCOMING },
            duration = duration,
            timestamp = timestamp,
            isRead = isRead,
            simSlot = simSlot,
            location = location,
            photoUri = photoUri
        )
    }

    fun CallLogEntry.toEntity(): CallLogEntity {
        return CallLogEntity(
            id = id,
            contactId = contactId,
            contactName = contactName,
            number = number,
            formattedNumber = formattedNumber,
            type = type.name,
            duration = duration,
            timestamp = timestamp,
            isRead = isRead,
            simSlot = simSlot,
            location = location,
            photoUri = photoUri
        )
    }
}
